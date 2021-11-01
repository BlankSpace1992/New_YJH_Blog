package com.blog.business.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.OnlineAdmin;
import com.blog.business.admin.domain.vo.AdminVO;
import com.blog.business.admin.mapper.AdminMapper;
import com.blog.business.admin.service.AdminService;
import com.blog.business.picture.domain.Storage;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.CategoryMenu;
import com.blog.business.web.domain.Role;
import com.blog.business.web.service.CategoryMenuService;
import com.blog.business.web.service.RoleService;
import com.blog.business.web.service.SysParamsService;
import com.blog.config.jwt.Audience;
import com.blog.config.jwt.JwtTokenUtil;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.holder.RequestHolder;
import com.blog.utils.DateUtils;
import com.blog.utils.IpUtils;
import com.blog.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/5/31 13:47
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;
    @Autowired
    private RedisUtil redisUtil;
    @Value(value = "${isRememberMeExpiresSecond}")
    private int isRememberMeExpiresSecond;
    @Value(value = "${tokenHead}")
    private String tokenHead;
    @Autowired
    private Audience audience;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CategoryMenuService categoryMenuService;


    @Override
    public Admin getAdminByUserName(String username) {
        // 根据用户查询数据
        Admin admin = baseMapper.getAdminByUsername(username);
        // 判空
        if (StringUtils.isNull(admin)) {
            return null;
        }
        // 获取用户头像
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            admin.setPhotoList(webUtils.getPicture(pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA)));
        }
        return admin;
    }

    @Override
    public ResultBody getAllAdminList(AdminVO adminVO) {
        // 注入分页参数
        Page<Admin> page = new Page<>();
        page.setCurrent(adminVO.getCurrentPage());
        page.setSize(adminVO.getPageSize());
        // 查询管理员信息
        LambdaQueryWrapper<Admin> adminWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(adminVO.getKeyword())) {
            adminWrapper.like(Admin::getUserName, adminVO.getKeyword()).or().like(Admin::getNickName,
                    adminVO.getKeyword().trim());
        }
        // 去除密码
        adminWrapper.select(Admin.class, i -> !i.getProperty().equals(BaseSQLConf.PASS_WORD));
        adminWrapper.eq(Admin::getStatus, EnumsStatus.ENABLE);
        IPage<Admin> adminPage = this.page(page, adminWrapper);
        // 获取实际数据
        List<Admin> adminList = adminPage.getRecords();
        // 存储头像信息
        StringBuilder fileIds = new StringBuilder();
        // 拼接头像信息
        adminList.forEach(item -> fileIds.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION));
        // 获取管理员id信息
        List<String> adminUidList = adminList.stream().map(Admin::getUid).collect(Collectors.toList());
        // 获取头像
        List<Map<String, Object>> picture = pictureFeignClient.getPicture(fileIds.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        // 增加网站地址前缀
        picture = webUtils.getPictureMap(picture);
        // 用户id与头像一一对应
        Map<String, String> pictureMap = new HashMap<>(Constants.NUM_TEN);
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        // 获取管理员的网盘存储空间
        String storageListJson = (String) pictureFeignClient.getStorageByAdminUid(adminUidList).getResult();
        List<Storage> storageList = JSON.parseArray(storageListJson, Storage.class);
        // 将存储空间进行拆分
        Map<String, List<Storage>> storageMap =
                storageList.stream().collect(Collectors.groupingBy(Storage::getAdminUid));
        // 循环遍历处理信息
        for (Admin item : adminList) {
            Role role = roleService.getById(item.getRoleUid());
            item.setRole(role);

            //获取图片
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                List<String> pictureIdsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, item.getAvatar()
                );
                List<String> pictureListTemp = new ArrayList<>();
                pictureIdsTemp.forEach(pictureItem -> {
                    if (StringUtils.isNotEmpty(pictureMap.get(pictureItem))) {
                        pictureListTemp.add(pictureMap.get(pictureItem));
                    }
                });
                item.setPhotoList(pictureListTemp);
            }
            // 设置已用容量大小和最大容量
            if (storageMap.get(item.getUid()) != null && storageMap.get(item.getUid()).get(0) != null) {
                Storage storage = storageMap.get(item.getUid()).get(0);
                item.setStorageSize(storage.getStorageSize());
                item.setMaxStorageSize(storage.getMaxStorageSize());
            } else {
                // 如果没有，默认为0
                item.setStorageSize(0L);
                item.setMaxStorageSize(0L);
            }
        }
        adminPage.setRecords(adminList);
        return ResultBody.success(adminPage);
    }

    @Override
    public ResultBody resetPassword(AdminVO adminVO) {
        // 获取默认密码
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "重置密码失败"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = (String) request.getAttribute(BaseSysConf.ADMIN_UID);
        }
        Admin admin = this.getById(adminVO.getUid());
        // 判断是否是admin重置密码【其它超级管理员，无法重置admin的密码】
        if (BaseSysConf.ADMIN.equals(admin.getUserName()) && !admin.getUid().equals(adminUid)) {
            return ResultBody.error(BaseMessageConf.UPDATE_ADMIN_PASSWORD_FAILED);
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        admin.setPassWord(encoder.encode(defaultPassword));
        admin.setUpdateTime(new Date());
        this.updateById(admin);
        return ResultBody.success(BaseMessageConf.UPDATE_SUCCESS);
    }

    @Override
    public ResultBody add(AdminVO adminVO) {
        String mobile = adminVO.getMobile();
        String userName = adminVO.getUserName();
        String email = adminVO.getEmail();
        if (StringUtils.isEmpty(userName)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            return ResultBody.error("邮箱和手机号至少一项不能为空");
        }
        // 默认密码
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        // 查询当前用户名是否已存在
        LambdaQueryWrapper<Admin> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(Admin::getUserName, userName);
        Admin adminTemp = this.getOne(adminWrapper);
        if (StringUtils.isNotNull(adminTemp)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Admin admin = new Admin();
        admin.setAvatar(adminVO.getAvatar());
        admin.setEmail(adminVO.getEmail());
        admin.setGender(adminVO.getGender());
        admin.setUserName(adminVO.getUserName());
        admin.setNickName(adminVO.getNickName());
        admin.setRoleUid(adminVO.getRoleUid());
        admin.setStatus(EnumsStatus.ENABLE);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        //设置默认密码
        admin.setPassWord(encoder.encode(defaultPassword));
        this.save(admin);


        // 更新成功后，同时申请网盘存储空间
        String maxStorageSizeString = sysParamsService.getSysParamsValueByKey(BaseSysConf.MAX_STORAGE_SIZE);
        long maxStorageSize = StringUtils.isNotEmpty(maxStorageSizeString) ? Long.parseLong(maxStorageSizeString) : 0L;
        // 发送邮件告知密码
        rabbitMqUtils.sendSimpleEmail(email, "初始密码:" + defaultPassword);
        // 初始化网盘的容量, 单位 B
        pictureFeignClient.initStorageSize(admin.getUid(), maxStorageSize * 1024 * 1024);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(AdminVO adminVO) {
        // 根据用户id获取用户信息
        Admin admin = this.getById(adminVO.getUid());
        if (StringUtils.isNull(admin)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        //判断修改的对象是否是admin，admin的用户名必须是admin
        if (admin.getUserName().equals(BaseSysConf.ADMIN) && !adminVO.getUserName().equals(BaseSysConf.ADMIN)) {
            return ResultBody.error("超级管理员用户名必须为admin");
        }
        // 判断是否更改了RoleUid，更新redis中admin的URL访问路径
        if (StringUtils.isNotEmpty(adminVO.getRoleUid()) && !adminVO.getRoleUid().equals(admin.getRoleUid())) {
            redisUtil.delete(BaseRedisConf.ADMIN_VISIT_MENU + BaseRedisConf.SEGMENTATION + admin.getUid());
        }
        admin.setUserName(adminVO.getUserName());
        admin.setAvatar(adminVO.getAvatar());
        admin.setNickName(adminVO.getNickName());
        admin.setGender(adminVO.getGender());
        admin.setEmail(adminVO.getEmail());
        admin.setQqNumber(adminVO.getQqNumber());
        admin.setGithub(adminVO.getGithub());
        admin.setGitee(adminVO.getGitee());
        admin.setOccupation(adminVO.getOccupation());
        admin.setUpdateTime(new Date());
        admin.setMobile(adminVO.getMobile());
        admin.setRoleUid(adminVO.getRoleUid());
        this.updateById(admin);
        // 更新完成后，判断是否调整了网盘的大小
        return pictureFeignClient.editStorageSize(admin.getUid(), adminVO.getMaxStorageSize() * 1024 * 1024);
    }

    @Override
    public ResultBody delete(List<String> adminIds) {
        List<Admin> adminList = new ArrayList<>();
        adminIds.forEach(item -> {
            Admin admin = new Admin();
            admin.setUid(item);
            admin.setStatus(EnumsStatus.DISABLED);
            admin.setUpdateTime(new Date());
            adminList.add(admin);
        });
        this.updateBatchById(adminList);
        return ResultBody.success();
    }

    @Override
    public ResultBody getOnlineAdminList(AdminVO adminVO) {
        // 获取所有redis中匹配的key
        Set<Object> keys = redisUtil.keys(BaseRedisConf.LOGIN_TOKEN_KEY + "*");
        // 获取所有对应的在线苏剧
        List<Object> onlineAdminJsonList = redisUtil.multiGet(keys);
        // 拼装分页数据
        int pageSize = adminVO.getPageSize().intValue();
        int currentPage = adminVO.getCurrentPage().intValue();
        int total = onlineAdminJsonList.size();
        // 开始索引位置
        int startIndex = Math.max((currentPage - 1) * pageSize, 0);
        // 结束索引位置
        int endIndex = Math.min(currentPage * pageSize, total);
        List<Object> onlineAdminSubList = onlineAdminJsonList.subList(startIndex, endIndex);
        List<OnlineAdmin> onlineAdminList = new ArrayList<>();
        for (Object item : onlineAdminSubList) {
            OnlineAdmin onlineAdmin = JSON.parseObject(String.valueOf(item), OnlineAdmin.class);
            // 数据脱敏【移除用户的token令牌】
            onlineAdmin.setToken("");
            onlineAdminList.add(onlineAdmin);
        }
        Page<OnlineAdmin> page = new Page<>();
        page.setCurrent(currentPage);
        page.setTotal(total);
        page.setSize(pageSize);
        page.setRecords(onlineAdminList);
        return ResultBody.success(page);
    }

    @Override
    public void addOnlineAdmin(Admin admin, Long expirationSecond) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "登录失败"));
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(BaseSysConf.OS);
        String browser = map.get(BaseSysConf.BROWSER);
        String ip = IpUtils.getIpAddr(request);
        OnlineAdmin onlineAdmin = new OnlineAdmin();
        onlineAdmin.setAdminUid(admin.getUid());
        onlineAdmin.setTokenId(admin.getTokenUid());
        onlineAdmin.setToken(admin.getValidCode());
        onlineAdmin.setOs(os);
        onlineAdmin.setBrowser(browser);
        onlineAdmin.setIpaddr(ip);
        onlineAdmin.setLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", DateUtils.getNowDate()));
        onlineAdmin.setRoleName(admin.getRole().getRoleName());
        onlineAdmin.setUserName(admin.getUserName());
        onlineAdmin.setExpireTime(DateUtils.getDateStr(new Date(), expirationSecond));
        //从Redis中获取IP来源
        String jsonResult = (String) redisUtil.get(BaseRedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip);
        if (StringUtils.isEmpty(jsonResult)) {
          String addresses = IpUtils.getAddresses(BaseSysConf.IP + BaseSysConf.EQUAL_TO + ip, BaseSysConf.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                onlineAdmin.setLoginLocation(addresses);
                redisUtil.set(BaseRedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip, addresses, 24 * 60 * 60);
            }
        } else {
            onlineAdmin.setLoginLocation(jsonResult);
        }
        // 将登录的管理员存储到在线用户表
        redisUtil.set(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + admin.getValidCode(),
                JSON.toJSONString(onlineAdmin), expirationSecond);
        // 在维护一张表，用于 uuid - token 互相转换
        redisUtil.set(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + admin.getTokenUid(),
                admin.getValidCode()
                , expirationSecond);
    }

    @Override
    public ResultBody forceLogout(List<String> tokenUidList) {
        // 从Redis中通过TokenUid获取到用户的真实token
        List<String> tokenList = new ArrayList<>();
        tokenUidList.forEach(item -> {
            String token = (String) redisUtil.get(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + item);
            if (StringUtils.isNotEmpty(token)) {
                tokenList.add(token);
            }
        });
        // 根据token删除Redis中的在线用户
        List<String> keyList = new ArrayList<>();
        String keyPrefix = BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION;
        for (String token : tokenList) {
            redisUtil.delete(keyPrefix + token);
        }
        return ResultBody.success();
    }

    @Override
    public Admin getCurrentAdmin() {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(
                        "获取当前管理员失败"));
        if (request.getAttribute(BaseSysConf.ADMIN_UID) == null || request.getAttribute(BaseSysConf.ADMIN_UID) == "") {
            return new Admin();
        }
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        //清空密码，防止泄露
        admin.setPassWord(null);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtils.getPicture(picture));
        }
        return admin;
    }

    @Override
    public ResultBody login(HttpServletRequest request, String username, String password, Boolean isRememberMe) {
        // 获取ip地址值
        String ipAddr = IpUtils.getIpAddr(request);
        // 获取限制次数
        Object limitCount = redisUtil.get(BaseRedisConf.LOGIN_LIMIT + BaseRedisConf.SEGMENTATION + ipAddr);
        if (StringUtils.isNotNull(limitCount)) {
            int tempLimitCount = Integer.parseInt((String) limitCount);
            if (tempLimitCount >= Constants.NUM_FIVE) {
                return ResultBody.error("密码输错次数过多,已被锁定30分钟");
            }
        }
        boolean isEmail = StringUtils.checkEmail(username);
        boolean isMobile = StringUtils.checkMobileNumber(username);
        // 账号可能为手机号,邮箱,或者自定义账号
        LambdaQueryWrapper<Admin> adminWrapper = new LambdaQueryWrapper<>();
        if (isEmail) {
            adminWrapper.eq(Admin::getEmail, username);
        } else if (isMobile) {
            adminWrapper.eq(Admin::getMobile, username);
        } else {
            adminWrapper.eq(Admin::getUserName, username);
        }
        adminWrapper.last(BaseSysConf.LIMIT_ONE);
        adminWrapper.eq(Admin::getStatus, EnumsStatus.ENABLE);
        Admin admin = this.getOne(adminWrapper);
        // 判断用户是否为空
        if (StringUtils.isNull(admin)) {
            return ResultBody.error(String.format(BaseMessageConf.LOGIN_ERROR, setLoginCommit(request)));
        }
        // 对密码进行加盐加密验证，采用SHA-256 + 随机盐【动态加盐】 + 密钥对密码进行加密
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isPassword = encoder.matches(password, admin.getPassWord());
        if (!isPassword) {
            //密码错误，返回提示
            return ResultBody.error(String.format(BaseMessageConf.LOGIN_ERROR, setLoginCommit(request)));
        }
        // 账号角色id集合
        List<String> roleUidList = new ArrayList<>();
        roleUidList.add(admin.getRoleUid());
        // 获取角色信息
        List<Role> roles = roleService.listByIds(roleUidList);
        if (roles.size() <= 0) {
            return ResultBody.error(BaseMessageConf.NO_ROLE);
        }
        // 拼接多个角色名称
        StringBuilder roleNames = new StringBuilder();
        for (Role role : roles) {
            roleNames.append(role.getRoleName()).append(Constants.SYMBOL_COMMA);
        }
        String roleName = roleNames.substring(0, roleNames.length() - 2);
        // 设置是否记住我
        long expiration = isRememberMe ? isRememberMeExpiresSecond : Long.parseLong(audience.getExpiresSecond());
        // 创建token
        String jwtToken = jwtTokenUtil.createJwt(admin.getUserName(),
                admin.getUid(),
                roleName,
                audience.getClientId(),
                audience.getName(),
                expiration * 1000,
                audience.getBase64Secret());
        String token = tokenHead + jwtToken;
        Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
        result.put(BaseSysConf.TOKEN, token);
        //进行登录相关操作
        Integer count = admin.getLoginCount() + 1;
        admin.setLoginCount(count);
        admin.setLastLoginIp(IpUtils.getIpAddr(request));
        admin.setLastLoginTime(new Date());
        this.updateById(admin);
        // 设置token到validCode，用于记录登录用户
        admin.setValidCode(token);
        // 设置tokenUid，【主要用于换取token令牌，防止token直接暴露到在线用户管理中】
        admin.setTokenUid(StringUtils.getUUID());
        admin.setRole(roles.get(0));
        // 添加在线用户到Redis中【设置过期时间】
        this.addOnlineAdmin(admin, expiration);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody info(HttpServletRequest request, String token) {
        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
        if (StringUtils.isNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            return ResultBody.error("token用户过期");
        }
        // 获取用户信息
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        map.put(BaseSysConf.TOKEN, token);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(admin.getAvatar(),
                    BaseSysConf.FILE_SEGMENTATION);
            picture = webUtils.getPictureMap(picture);
            if (picture.size() > 0) {
                map.put(BaseSysConf.AVATAR, picture.get(0).get(BaseSysConf.URL));
            } else {
                map.put(BaseSysConf.AVATAR, "https://gitee.com/moxi159753/wx_picture/raw/master/picture/favicon.png");
            }
        }
        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        List<Role> roleList = roleService.listByIds(roleUid);
        map.put(BaseSysConf.ROLES, roleList);
        return ResultBody.success(map);
    }

    @Override
    public ResultBody getMenu(HttpServletRequest request) {
        // 获取当前用户信息
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        // 获取用户角色信息
        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        List<Role> roles = roleService.listByIds(roleUid);
        // 获取角色规则
        List<String> categoryMenuUidList = new ArrayList<>();
        roles.forEach(item -> {
            String categoryMenuUid = item.getCategoryMenuUids();
            String[] uidArray = categoryMenuUid.replace("[", "").replace("]", "").replace("\"", "").split(",");
            categoryMenuUidList.addAll(Arrays.asList(uidArray));
        });
        List<CategoryMenu> categoryMenuList = categoryMenuService.listByIds(categoryMenuUidList);
        // 从三级级分类中查询出 二级分类
        List<CategoryMenu> buttonList = new ArrayList<>();
        Set<String> secondMenuUidList = new HashSet<>();
        categoryMenuList.forEach(item -> {
            // 查询二级分类
            if (item.getMenuType() == EnumsLevel.MENU && item.getMenuLevel() == BaseSysConf.TWO) {
                secondMenuUidList.add(item.getUid());
            }
            // 从三级分类中，得到二级分类
            if (item.getMenuType() == EnumsLevel.BUTTON && StringUtils.isNotEmpty(item.getParentUid())) {
                // 找出二级菜单
                secondMenuUidList.add(item.getParentUid());
                // 找出全部按钮
                buttonList.add(item);
            }
        });
        List<CategoryMenu> childCategoryMenuList = new ArrayList<>();
        List<CategoryMenu> parentCategoryMenuList = new ArrayList<>();
        List<String> parentCategoryMenuUidList = new ArrayList<>();

        if (secondMenuUidList.size() > 0) {
            childCategoryMenuList = categoryMenuService.listByIds(secondMenuUidList);
        }

        childCategoryMenuList.forEach(item -> {
            //选出所有的二级分类
            if (item.getMenuLevel() == BaseSysConf.TWO) {

                if (StringUtils.isNotEmpty(item.getParentUid())) {
                    parentCategoryMenuUidList.add(item.getParentUid());
                }
            }
        });
        if (parentCategoryMenuUidList.size() > 0) {
            parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuUidList);
        }
        //对parent进行排序
        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
        Collections.sort(parentCategoryMenuList);
        map.put(BaseSysConf.PARENT_LIST, parentCategoryMenuList);
        map.put(BaseSysConf.SON_LIST, childCategoryMenuList);
        map.put(BaseSysConf.BUTTON_LIST, buttonList);
        return ResultBody.success(map);
    }

    @Override
    public ResultBody logout() {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attribute != null;
        HttpServletRequest request = attribute.getRequest();
        String token = request.getAttribute(BaseSysConf.TOKEN).toString();
        if (StringUtils.isEmpty(token)) {
            return ResultBody.error(BaseMessageConf.OPERATION_FAIL);
        } else {
            // 获取在线用户信息
            String adminJson =
                    (String) redisUtil.get(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + token);
            if (StringUtils.isNotEmpty(adminJson)) {
                OnlineAdmin onlineAdmin = JSON.parseObject(adminJson, OnlineAdmin.class);
                String tokenUid = onlineAdmin.getTokenId();
                // 移除Redis中的TokenUid
                redisUtil.delete(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + tokenUid);
            }
            // 移除Redis中的用户
            redisUtil.delete(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + token);
            return ResultBody.success(BaseMessageConf.OPERATION_SUCCESS);
        }
    }

    @Override
    public Admin getMe() {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取信息失败"));
        if (StringUtils.isEmpty(request.getAttribute(BaseSysConf.ADMIN_UID).toString())) {
            return new Admin();
        }
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        //清空密码，防止泄露
        admin.setPassWord(null);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtils.getPicture(picture));
        }
        return admin;
    }

    @Override
    public ResultBody editMe(AdminVO adminVO) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取信息失败"));
        if (StringUtils.isEmpty(request.getAttribute(BaseSysConf.ADMIN_UID).toString())) {
            ResultBody.error(BaseMessageConf.INVALID_TOKEN);
        }
        Admin admin = new Admin();
        // 【使用Spring工具类提供的深拷贝，减少大量模板代码】
        BeanUtils.copyProperties(adminVO, admin, BaseSysConf.STATUS);
        admin.setUpdateTime(new Date());
        this.updateById(admin);
        return ResultBody.success();
    }

    @Override
    public ResultBody changePwd(String oldPwd, String newPwd) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取信息失败"));
        if (StringUtils.isEmpty(request.getAttribute(BaseSysConf.ADMIN_UID).toString())) {
            ResultBody.error(BaseMessageConf.INVALID_TOKEN);
        }
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isPassword = encoder.matches(oldPwd, admin.getPassWord());
        if (isPassword) {
            admin.setPassWord(encoder.encode(newPwd));
            admin.setUpdateTime(new Date());
            this.updateById(admin);
            return ResultBody.success();
        } else {
            return ResultBody.error(BaseMessageConf.ERROR_PASSWORD);
        }
    }

    /**
     * 设置登录限制，返回剩余次数
     * 密码错误五次，将会锁定30分钟
     *
     * @param request 请求
     * @return 登录错误次数
     * @author yujunhong
     * @date 2021/9/17 17:05
     */
    private Integer setLoginCommit(HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        Object count = redisUtil.get(BaseRedisConf.LOGIN_LIMIT + BaseRedisConf.SEGMENTATION + ip);
        Integer surplusCount = 5;
        if (StringUtils.isNotNull(count)) {
            Integer countTemp = Integer.parseInt((String) count) + 1;
            surplusCount = surplusCount - countTemp;
            redisUtil.set(BaseRedisConf.LOGIN_LIMIT + BaseRedisConf.SEGMENTATION + ip, String.valueOf(countTemp),
                    30 * 60);
        } else {
            surplusCount = surplusCount - 1;
            redisUtil.set(BaseRedisConf.LOGIN_LIMIT + BaseRedisConf.SEGMENTATION + ip, Constants.STR_ONE, 30 * 60);
        }
        return surplusCount;
    }
}

