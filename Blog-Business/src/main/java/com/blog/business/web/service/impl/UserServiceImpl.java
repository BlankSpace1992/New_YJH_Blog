package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.mapper.UserMapper;
import com.blog.business.web.service.SysParamsService;
import com.blog.business.web.service.UserService;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.holder.RequestHolder;
import com.blog.utils.IpUtils;
import com.blog.utils.Md5Utils;
import com.blog.utils.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RedisUtil redisUtil;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private SysParamsService sysParamsService;

    @Override
    public List<User> getUserListByIds(List<String> userUidList) {
        return baseMapper.getUserListByIds(userUidList);
    }

    @Override
    public User getUserBySourceAndUuid(String source, String uuid) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUuid, uuid);
        userLambdaQueryWrapper.eq(User::getSource, source);
        return this.getOne(userLambdaQueryWrapper);
    }

    @Override
    public User setRequestInfo(User user) {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get("OS");
        String browser = map.get("BROWSER");
        String ip = IpUtils.getIpAddr(request);
        user.setLastLoginIp(ip);
        user.setOs(os);
        user.setBrowser(browser);
        user.setLastLoginTime(new Date());
        // // TODO: 2021/9/7 处理ip来源问题
        return user;
    }

    @Override
    public void editUser(UserVO userVO, String userUid, String token) {
        // 获取用户信息
        User user = Optional.ofNullable(this.getById(userUid)).orElseThrow(() -> new CommonErrorException("用户信息不存在"));
        // 设置用户信息
        user.setNickName(userVO.getNickName());
        user.setAvatar(userVO.getAvatar());
        user.setBirthday(userVO.getBirthday());
        user.setSummary(userVO.getSummary());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setOccupation(userVO.getOccupation());
        // 如果开启邮件通知，必须保证邮箱已存在
        if (userVO.getStartEmailNotification() == BaseSysConf.ONE && !StringUtils.isNotEmpty(user.getEmail())) {
            throw new CommonErrorException(BaseSysConf.ERROR, "必须填写并绑定邮箱后，才能开启评论邮件通知~");
        }
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        this.updateById(user);
        // 密码设置为空
        user.setPassWord("");
        // 设置图片信息
        user.setPhotoUrl(userVO.getPhotoUrl());
        // 判断用户是否更改了邮箱
        if (userVO.getEmail() != null && !userVO.getEmail().equals(user.getEmail())) {
            user.setEmail(userVO.getEmail());
        }
        // 使用RabbitMQ发送邮件
        rabbitMqUtils.sendRegisterEmail(user.getEmail(), user.getNickName(), user.getValidCode(), token);
        redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token,
                JSON.toJSONString(user), userTokenSurvivalTime * 60 * 60);
    }

    @Override
    public ResultBody login(UserVO userVO) {
        // 获取用户名称
        @NotBlank(message = "用户名不允许为空") @Range(min = 5, max = 30) String userName = userVO.getUserName();
        // 查询当前用户名是否已经存在
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserName, userName);
        User user = this.getOne(userWrapper);
        // 判断用户是否存在
        if (StringUtils.isNull(user) || EnumsStatus.DISABLED == user.getStatus()) {
            return ResultBody.error("用户不存在");
        }
        // 判断账户是否未激活
        if (EnumsStatus.FREEZE == user.getStatus()) {
            return ResultBody.error("用户账号未激活");
        }
        // 密码不允许为空
        if (StringUtils.isNotEmpty(user.getPassWord()) && user.getPassWord().equals(Md5Utils.stringToMd5(userVO.getPassWord()))) {
            // 更新登录信息
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("登录失败"));
            // 获取登录ip
            String ipAddr = IpUtils.getIpAddr(request);
            // 获取登录信息
            Map<String, String> osAndBrowserInfo = IpUtils.getOsAndBrowserInfo(request);
            user.setBrowser(osAndBrowserInfo.get(BaseSysConf.BROWSER));
            user.setOs(osAndBrowserInfo.get(BaseSysConf.OS));
            user.setLastLoginIp(ipAddr);
            user.setLastLoginTime(new Date());
            this.updateById(user);
            // 获取用户头像
            if (!StringUtils.isEmpty(user.getAvatar())) {
                List<Map<String, Object>> picture = pictureFeignClient.getPicture(user.getAvatar(), ",");
                if (picture != null && picture.size() > 0) {
                    user.setPhotoUrl(webUtils.getPicture(picture).get(0));
                }
            }
            // 生成token
            String token = StringUtils.getUUID();
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中
            redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JSON.toJSONString(user),
                    userTokenSurvivalTime * 60 * 60);
            return ResultBody.success(token);
        } else {
            return ResultBody.error("账号或密码错误");
        }
    }

    @Override
    public ResultBody register(UserVO userVO) {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("登录失败"));
        // 获取ip地址值
        String ipAddr = IpUtils.getIpAddr(request);
        // 获取平台信息
        Map<String, String> osAndBrowserInfo = IpUtils.getOsAndBrowserInfo(request);
        // 查询当前用户名是否已经存在
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserName, userVO.getUserName());
        userWrapper.eq(User::getStatus, EnumsStatus.ENABLE);
        User user = this.getOne(userWrapper);
        if (StringUtils.isNotNull(user)) {
            return ResultBody.error("账号已经存在,不允许再次添加");
        }
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(Md5Utils.stringToMd5(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // 设置账号来源，蘑菇博客
        user.setSource(BaseSysConf.MOGU);
        user.setLastLoginIp(ipAddr);
        user.setBrowser(osAndBrowserInfo.get(BaseSysConf.BROWSER));
        user.setOs(osAndBrowserInfo.get(BaseSysConf.OS));
        // 判断是否开启用户邮件激活状态
        SystemConfig systemConfig = systemConfigService.getSystemConfig();
        String openEmailActivate = systemConfig.isOpenEmailActivate() ? "1" : "0";
        String resultMessage = "注册成功";
        if (EnumsStatus.OPEN.equals(openEmailActivate)) {
            user.setStatus(EnumsStatus.FREEZE);
        } else {
            // 未开启注册用户邮件激活，直接设置成激活状态
            user.setStatus(EnumsStatus.ENABLE);
        }
        this.save(user);
        // 判断是否需要发送邮件通知
        if (EnumsStatus.OPEN.equals(openEmailActivate)) {
            // 生成随机激活的token
            String token = StringUtils.getUUID();
            //将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
            redisUtil.set(BaseRedisConf.ACTIVATE_USER + BaseRedisConf.SEGMENTATION + token, JSON.toJSONString(user),
                    60 * 60);
            // 发送邮件，进行账号激活
            rabbitMqUtils.sendActivateEmail(user.getEmail(), user.getNickName(), token);
            resultMessage = "注册成功，请登录邮箱进行账号激活";
        }
        return ResultBody.success(resultMessage);
    }

    @Override
    public ResultBody activeUser(String token) {
        // 从redis中获取信息
        String userInfo = (String) redisUtil.get(BaseRedisConf.ACTIVATE_USER + BaseRedisConf.SEGMENTATION + token);
        // 判断用户信息是否存在
        if (StringUtils.isEmpty(userInfo)) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        }
        User user = JSON.parseObject(userInfo, User.class);
        if (EnumsStatus.FREEZE != user.getStatus()) {
            return ResultBody.error("用户账号已经被激活");
        }
        user.setStatus(EnumsStatus.ENABLE);
        this.updateById(user);
        return ResultBody.success();
    }

    @Override
    public Integer getUserCount(int enableFlag) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, enableFlag);
        return this.count(wrapper);
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {
        // 注入分页参数
        IPage<User> page = new Page<>();
        page.setSize(userVO.getPageSize());
        page.setCurrent(userVO.getCurrentPage());
        IPage<User> pageList = baseMapper.getPageList(page, userVO);
        // 获取实际数据
        List<User> userList = pageList.getRecords();
        // 拼接头像
        StringBuilder fileUidBuilder = new StringBuilder();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUidBuilder.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> picture = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUidBuilder)) {
            picture = this.pictureFeignClient.getPicture(fileUidBuilder.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }
        Map<String, String> pictureMap = new HashMap<>();
        picture = webUtils.getPictureMap(picture);
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });

        for (User item : userList) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                List<String> pictureUidsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,
                        item.getAvatar());
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(pictureTemp -> {
                    if (pictureMap.get(pictureTemp) != null && pictureMap.get(pictureTemp) != "") {
                        pictureListTemp.add(pictureMap.get(pictureTemp));
                    }
                });
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                }
            }
        }
        pageList.setRecords(userList);
        return pageList;
    }

    @Override
    public ResultBody add(UserVO userVO) {
        User user = new User();
        // 字段拷贝【将userVO中的内容拷贝至user】
        BeanUtils.copyProperties(userVO, user, BaseSysConf.STATUS);
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        user.setPassWord(Md5Utils.stringToMd5(defaultPassword));
        user.setSource("MOGU");
        this.save(user);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        if (StringUtils.isNull(user)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        user.setUserName(userVO.getUserName());
        user.setEmail(userVO.getEmail());
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        user.setOccupation(userVO.getOccupation());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setSummary(userVO.getSummary());
        user.setBirthday(userVO.getBirthday());
        user.setAvatar(userVO.getAvatar());
        user.setNickName(userVO.getNickName());
        user.setUserTag(userVO.getUserTag());
        user.setCommentStatus(userVO.getCommentStatus());
        user.setUpdateTime(new Date());
        this.updateById(user);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        if (StringUtils.isNull(user)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        user.setStatus(EnumsStatus.DISABLED);
        user.setUpdateTime(new Date());
        this.updateById(user);
        return ResultBody.success();
    }

    @Override
    public ResultBody resetUserPassword(UserVO userVO) {
        String defaultPassword = sysParamsService.getSysParamsValueByKey(BaseSysConf.SYS_DEFAULT_PASSWORD);
        User user = this.getById(userVO.getUid());
        if (StringUtils.isNull(user)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        user.setPassWord(Md5Utils.stringToMd5(defaultPassword));
        user.setUpdateTime(new Date());
        this.updateById(user);
        return ResultBody.success();
    }
}
