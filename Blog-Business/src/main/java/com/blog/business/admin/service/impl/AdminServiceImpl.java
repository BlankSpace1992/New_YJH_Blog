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
        // ????????????????????????
        Admin admin = baseMapper.getAdminByUsername(username);
        // ??????
        if (StringUtils.isNull(admin)) {
            return null;
        }
        // ??????????????????
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            admin.setPhotoList(webUtils.getPicture(pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA)));
        }
        return admin;
    }

    @Override
    public ResultBody getAllAdminList(AdminVO adminVO) {
        // ??????????????????
        Page<Admin> page = new Page<>();
        page.setCurrent(adminVO.getCurrentPage());
        page.setSize(adminVO.getPageSize());
        // ?????????????????????
        LambdaQueryWrapper<Admin> adminWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(adminVO.getKeyword())) {
            adminWrapper.like(Admin::getUserName, adminVO.getKeyword()).or().like(Admin::getNickName,
                    adminVO.getKeyword().trim());
        }
        // ????????????
        adminWrapper.select(Admin.class, i -> !i.getProperty().equals(BaseSQLConf.PASS_WORD));
        adminWrapper.eq(Admin::getStatus, EnumsStatus.ENABLE);
        IPage<Admin> adminPage = this.page(page, adminWrapper);
        // ??????????????????
        List<Admin> adminList = adminPage.getRecords();
        // ??????????????????
        StringBuilder fileIds = new StringBuilder();
        // ??????????????????
        adminList.forEach(item -> fileIds.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION));
        // ???????????????id??????
        List<String> adminUidList = adminList.stream().map(Admin::getUid).collect(Collectors.toList());
        // ????????????
        List<Map<String, Object>> picture = pictureFeignClient.getPicture(fileIds.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        // ????????????????????????
        picture = webUtils.getPictureMap(picture);
        // ??????id?????????????????????
        Map<String, String> pictureMap = new HashMap<>(Constants.NUM_TEN);
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        // ????????????????????????????????????
        String storageListJson = (String) pictureFeignClient.getStorageByAdminUid(adminUidList).getResult();
        List<Storage> storageList = JSON.parseArray(storageListJson, Storage.class);
        // ???????????????????????????
        Map<String, List<Storage>> storageMap =
                storageList.stream().collect(Collectors.groupingBy(Storage::getAdminUid));
        // ????????????????????????
        for (Admin item : adminList) {
            Role role = roleService.getById(item.getRoleUid());
            item.setRole(role);

            //????????????
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
            // ???????????????????????????????????????
            if (storageMap.get(item.getUid()) != null && storageMap.get(item.getUid()).get(0) != null) {
                Storage storage = storageMap.get(item.getUid()).get(0);
                item.setStorageSize(storage.getStorageSize());
                item.setMaxStorageSize(storage.getMaxStorageSize());
            } else {
                // ????????????????????????0
                item.setStorageSize(0L);
                item.setMaxStorageSize(0L);
            }
        }
        adminPage.setRecords(adminList);
        return ResultBody.success(adminPage);
    }

    @Override
    public ResultBody resetPassword(AdminVO adminVO) {
        // ??????????????????
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "??????????????????"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = (String) request.getAttribute(BaseSysConf.ADMIN_UID);
        }
        Admin admin = this.getById(adminVO.getUid());
        // ???????????????admin???????????????????????????????????????????????????admin????????????
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
            return ResultBody.error("??????????????????????????????????????????");
        }
        // ????????????
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        // ????????????????????????????????????
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
        //??????????????????
        admin.setPassWord(encoder.encode(defaultPassword));
        this.save(admin);


        // ????????????????????????????????????????????????
        String maxStorageSizeString = sysParamsService.getSysParamsValueByKey(BaseSysConf.MAX_STORAGE_SIZE);
        long maxStorageSize = StringUtils.isNotEmpty(maxStorageSizeString) ? Long.parseLong(maxStorageSizeString) : 0L;
        // ????????????????????????
        rabbitMqUtils.sendSimpleEmail(email, "????????????:" + defaultPassword);
        // ????????????????????????, ?????? B
        pictureFeignClient.initStorageSize(admin.getUid(), maxStorageSize * 1024 * 1024);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(AdminVO adminVO) {
        // ????????????id??????????????????
        Admin admin = this.getById(adminVO.getUid());
        if (StringUtils.isNull(admin)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        //??????????????????????????????admin???admin?????????????????????admin
        if (admin.getUserName().equals(BaseSysConf.ADMIN) && !adminVO.getUserName().equals(BaseSysConf.ADMIN)) {
            return ResultBody.error("?????????????????????????????????admin");
        }
        // ?????????????????????RoleUid?????????redis???admin???URL????????????
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
        // ??????????????????????????????????????????????????????
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
        // ????????????redis????????????key
        Set<Object> keys = redisUtil.keys(BaseRedisConf.LOGIN_TOKEN_KEY + "*");
        // ?????????????????????????????????
        List<Object> onlineAdminJsonList = redisUtil.multiGet(keys);
        // ??????????????????
        int pageSize = adminVO.getPageSize().intValue();
        int currentPage = adminVO.getCurrentPage().intValue();
        int total = onlineAdminJsonList.size();
        // ??????????????????
        int startIndex = Math.max((currentPage - 1) * pageSize, 0);
        // ??????????????????
        int endIndex = Math.min(currentPage * pageSize, total);
        List<Object> onlineAdminSubList = onlineAdminJsonList.subList(startIndex, endIndex);
        List<OnlineAdmin> onlineAdminList = new ArrayList<>();
        for (Object item : onlineAdminSubList) {
            OnlineAdmin onlineAdmin = JSON.parseObject(String.valueOf(item), OnlineAdmin.class);
            // ??????????????????????????????token?????????
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
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "????????????"));
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
        //???Redis?????????IP??????
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
        // ?????????????????????????????????????????????
        redisUtil.set(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + admin.getValidCode(),
                JSON.toJSONString(onlineAdmin), expirationSecond);
        // ??????????????????????????? uuid - token ????????????
        redisUtil.set(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + admin.getTokenUid(),
                admin.getValidCode()
                , expirationSecond);
    }

    @Override
    public ResultBody forceLogout(List<String> tokenUidList) {
        // ???Redis?????????TokenUid????????????????????????token
        List<String> tokenList = new ArrayList<>();
        tokenUidList.forEach(item -> {
            String token = (String) redisUtil.get(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + item);
            if (StringUtils.isNotEmpty(token)) {
                tokenList.add(token);
            }
        });
        // ??????token??????Redis??????????????????
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
                        "???????????????????????????"));
        if (request.getAttribute(BaseSysConf.ADMIN_UID) == null || request.getAttribute(BaseSysConf.ADMIN_UID) == "") {
            return new Admin();
        }
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        //???????????????????????????
        admin.setPassWord(null);
        //????????????
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtils.getPicture(picture));
        }
        return admin;
    }

    @Override
    public ResultBody login(HttpServletRequest request, String username, String password, Boolean isRememberMe) {
        // ??????ip?????????
        String ipAddr = IpUtils.getIpAddr(request);
        // ??????????????????
        Object limitCount = redisUtil.get(BaseRedisConf.LOGIN_LIMIT + BaseRedisConf.SEGMENTATION + ipAddr);
        if (StringUtils.isNotNull(limitCount)) {
            int tempLimitCount = Integer.parseInt((String) limitCount);
            if (tempLimitCount >= Constants.NUM_FIVE) {
                return ResultBody.error("????????????????????????,????????????30??????");
            }
        }
        boolean isEmail = StringUtils.checkEmail(username);
        boolean isMobile = StringUtils.checkMobileNumber(username);
        // ????????????????????????,??????,?????????????????????
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
        // ????????????????????????
        if (StringUtils.isNull(admin)) {
            return ResultBody.error(String.format(BaseMessageConf.LOGIN_ERROR, setLoginCommit(request)));
        }
        // ??????????????????????????????????????????SHA-256 + ??????????????????????????? + ???????????????????????????
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isPassword = encoder.matches(password, admin.getPassWord());
        if (!isPassword) {
            //???????????????????????????
            return ResultBody.error(String.format(BaseMessageConf.LOGIN_ERROR, setLoginCommit(request)));
        }
        // ????????????id??????
        List<String> roleUidList = new ArrayList<>();
        roleUidList.add(admin.getRoleUid());
        // ??????????????????
        List<Role> roles = roleService.listByIds(roleUidList);
        if (roles.size() <= 0) {
            return ResultBody.error(BaseMessageConf.NO_ROLE);
        }
        // ????????????????????????
        StringBuilder roleNames = new StringBuilder();
        for (Role role : roles) {
            roleNames.append(role.getRoleName()).append(Constants.SYMBOL_COMMA);
        }
        String roleName = roleNames.substring(0, roleNames.length() - 2);
        // ?????????????????????
        long expiration = isRememberMe ? isRememberMeExpiresSecond : Long.parseLong(audience.getExpiresSecond());
        // ??????token
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
        //????????????????????????
        Integer count = admin.getLoginCount() + 1;
        admin.setLoginCount(count);
        admin.setLastLoginIp(IpUtils.getIpAddr(request));
        admin.setLastLoginTime(new Date());
        this.updateById(admin);
        // ??????token???validCode???????????????????????????
        admin.setValidCode(token);
        // ??????tokenUid????????????????????????token???????????????token???????????????????????????????????????
        admin.setTokenUid(StringUtils.getUUID());
        admin.setRole(roles.get(0));
        // ?????????????????????Redis???????????????????????????
        this.addOnlineAdmin(admin, expiration);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody info(HttpServletRequest request, String token) {
        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
        if (StringUtils.isNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            return ResultBody.error("token????????????");
        }
        // ??????????????????
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        map.put(BaseSysConf.TOKEN, token);
        //????????????
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
        // ????????????????????????
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        // ????????????????????????
        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        List<Role> roles = roleService.listByIds(roleUid);
        // ??????????????????
        List<String> categoryMenuUidList = new ArrayList<>();
        roles.forEach(item -> {
            String categoryMenuUid = item.getCategoryMenuUids();
            String[] uidArray = categoryMenuUid.replace("[", "").replace("]", "").replace("\"", "").split(",");
            categoryMenuUidList.addAll(Arrays.asList(uidArray));
        });
        List<CategoryMenu> categoryMenuList = categoryMenuService.listByIds(categoryMenuUidList);
        // ?????????????????????????????? ????????????
        List<CategoryMenu> buttonList = new ArrayList<>();
        Set<String> secondMenuUidList = new HashSet<>();
        categoryMenuList.forEach(item -> {
            // ??????????????????
            if (item.getMenuType() == EnumsLevel.MENU && item.getMenuLevel() == BaseSysConf.TWO) {
                secondMenuUidList.add(item.getUid());
            }
            // ???????????????????????????????????????
            if (item.getMenuType() == EnumsLevel.BUTTON && StringUtils.isNotEmpty(item.getParentUid())) {
                // ??????????????????
                secondMenuUidList.add(item.getParentUid());
                // ??????????????????
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
            //???????????????????????????
            if (item.getMenuLevel() == BaseSysConf.TWO) {

                if (StringUtils.isNotEmpty(item.getParentUid())) {
                    parentCategoryMenuUidList.add(item.getParentUid());
                }
            }
        });
        if (parentCategoryMenuUidList.size() > 0) {
            parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuUidList);
        }
        //???parent????????????
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
            // ????????????????????????
            String adminJson =
                    (String) redisUtil.get(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + token);
            if (StringUtils.isNotEmpty(adminJson)) {
                OnlineAdmin onlineAdmin = JSON.parseObject(adminJson, OnlineAdmin.class);
                String tokenUid = onlineAdmin.getTokenId();
                // ??????Redis??????TokenUid
                redisUtil.delete(BaseRedisConf.LOGIN_UUID_KEY + BaseRedisConf.SEGMENTATION + tokenUid);
            }
            // ??????Redis????????????
            redisUtil.delete(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + token);
            return ResultBody.success(BaseMessageConf.OPERATION_SUCCESS);
        }
    }

    @Override
    public Admin getMe() {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "??????????????????"));
        if (StringUtils.isEmpty(request.getAttribute(BaseSysConf.ADMIN_UID).toString())) {
            return new Admin();
        }
        Admin admin = this.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        //???????????????????????????
        admin.setPassWord(null);
        //????????????
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
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "??????????????????"));
        if (StringUtils.isEmpty(request.getAttribute(BaseSysConf.ADMIN_UID).toString())) {
            ResultBody.error(BaseMessageConf.INVALID_TOKEN);
        }
        Admin admin = new Admin();
        // ?????????Spring?????????????????????????????????????????????????????????
        BeanUtils.copyProperties(adminVO, admin, BaseSysConf.STATUS);
        admin.setUpdateTime(new Date());
        this.updateById(admin);
        return ResultBody.success();
    }

    @Override
    public ResultBody changePwd(String oldPwd, String newPwd) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "??????????????????"));
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
     * ???????????????????????????????????????
     * ?????????????????????????????????30??????
     *
     * @param request ??????
     * @return ??????????????????
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

