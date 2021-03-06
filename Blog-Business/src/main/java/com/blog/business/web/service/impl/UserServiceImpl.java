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
        // // TODO: 2021/9/7 ??????ip????????????
        return user;
    }

    @Override
    public void editUser(UserVO userVO, String userUid, String token) {
        // ??????????????????
        User user = Optional.ofNullable(this.getById(userUid)).orElseThrow(() -> new CommonErrorException("?????????????????????"));
        // ??????????????????
        user.setNickName(userVO.getNickName());
        user.setAvatar(userVO.getAvatar());
        user.setBirthday(userVO.getBirthday());
        user.setSummary(userVO.getSummary());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setOccupation(userVO.getOccupation());
        // ??????????????????????????????????????????????????????
        if (userVO.getStartEmailNotification() == BaseSysConf.ONE && !StringUtils.isNotEmpty(user.getEmail())) {
            throw new CommonErrorException(BaseSysConf.ERROR, "???????????????????????????????????????????????????????????????~");
        }
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        this.updateById(user);
        // ??????????????????
        user.setPassWord("");
        // ??????????????????
        user.setPhotoUrl(userVO.getPhotoUrl());
        // ?????????????????????????????????
        if (userVO.getEmail() != null && !userVO.getEmail().equals(user.getEmail())) {
            user.setEmail(userVO.getEmail());
        }
        // ??????RabbitMQ????????????
        rabbitMqUtils.sendRegisterEmail(user.getEmail(), user.getNickName(), user.getValidCode(), token);
        redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token,
                JSON.toJSONString(user), userTokenSurvivalTime * 60 * 60);
    }

    @Override
    public ResultBody login(UserVO userVO) {
        // ??????????????????
        @NotBlank(message = "????????????????????????") @Range(min = 5, max = 30) String userName = userVO.getUserName();
        // ???????????????????????????????????????
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserName, userName);
        User user = this.getOne(userWrapper);
        // ????????????????????????
        if (StringUtils.isNull(user) || EnumsStatus.DISABLED == user.getStatus()) {
            return ResultBody.error("???????????????");
        }
        // ???????????????????????????
        if (EnumsStatus.FREEZE == user.getStatus()) {
            return ResultBody.error("?????????????????????");
        }
        // ?????????????????????
        if (StringUtils.isNotEmpty(user.getPassWord()) && user.getPassWord().equals(Md5Utils.stringToMd5(userVO.getPassWord()))) {
            // ??????????????????
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("????????????"));
            // ????????????ip
            String ipAddr = IpUtils.getIpAddr(request);
            // ??????????????????
            Map<String, String> osAndBrowserInfo = IpUtils.getOsAndBrowserInfo(request);
            user.setBrowser(osAndBrowserInfo.get(BaseSysConf.BROWSER));
            user.setOs(osAndBrowserInfo.get(BaseSysConf.OS));
            user.setLastLoginIp(ipAddr);
            user.setLastLoginTime(new Date());
            this.updateById(user);
            // ??????????????????
            if (!StringUtils.isEmpty(user.getAvatar())) {
                List<Map<String, Object>> picture = pictureFeignClient.getPicture(user.getAvatar(), ",");
                if (picture != null && picture.size() > 0) {
                    user.setPhotoUrl(webUtils.getPicture(picture).get(0));
                }
            }
            // ??????token
            String token = StringUtils.getUUID();
            // ????????????
            user.setPassWord("");
            //???????????????????????????????????????redis???
            redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JSON.toJSONString(user),
                    userTokenSurvivalTime * 60 * 60);
            return ResultBody.success(token);
        } else {
            return ResultBody.error("?????????????????????");
        }
    }

    @Override
    public ResultBody register(UserVO userVO) {
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("????????????"));
        // ??????ip?????????
        String ipAddr = IpUtils.getIpAddr(request);
        // ??????????????????
        Map<String, String> osAndBrowserInfo = IpUtils.getOsAndBrowserInfo(request);
        // ???????????????????????????????????????
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserName, userVO.getUserName());
        userWrapper.eq(User::getStatus, EnumsStatus.ENABLE);
        User user = this.getOne(userWrapper);
        if (StringUtils.isNotNull(user)) {
            return ResultBody.error("??????????????????,?????????????????????");
        }
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(Md5Utils.stringToMd5(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // ?????????????????????????????????
        user.setSource(BaseSysConf.MOGU);
        user.setLastLoginIp(ipAddr);
        user.setBrowser(osAndBrowserInfo.get(BaseSysConf.BROWSER));
        user.setOs(osAndBrowserInfo.get(BaseSysConf.OS));
        // ??????????????????????????????????????????
        SystemConfig systemConfig = systemConfigService.getSystemConfig();
        String openEmailActivate = systemConfig.isOpenEmailActivate() ? "1" : "0";
        String resultMessage = "????????????";
        if (EnumsStatus.OPEN.equals(openEmailActivate)) {
            user.setStatus(EnumsStatus.FREEZE);
        } else {
            // ???????????????????????????????????????????????????????????????
            user.setStatus(EnumsStatus.ENABLE);
        }
        this.save(user);
        // ????????????????????????????????????
        if (EnumsStatus.OPEN.equals(openEmailActivate)) {
            // ?????????????????????token
            String token = StringUtils.getUUID();
            //???????????????????????????????????????redis?????????????????????????????????1???????????????
            redisUtil.set(BaseRedisConf.ACTIVATE_USER + BaseRedisConf.SEGMENTATION + token, JSON.toJSONString(user),
                    60 * 60);
            // ?????????????????????????????????
            rabbitMqUtils.sendActivateEmail(user.getEmail(), user.getNickName(), token);
            resultMessage = "????????????????????????????????????????????????";
        }
        return ResultBody.success(resultMessage);
    }

    @Override
    public ResultBody activeUser(String token) {
        // ???redis???????????????
        String userInfo = (String) redisUtil.get(BaseRedisConf.ACTIVATE_USER + BaseRedisConf.SEGMENTATION + token);
        // ??????????????????????????????
        if (StringUtils.isEmpty(userInfo)) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        }
        User user = JSON.parseObject(userInfo, User.class);
        if (EnumsStatus.FREEZE != user.getStatus()) {
            return ResultBody.error("???????????????????????????");
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
        // ??????????????????
        IPage<User> page = new Page<>();
        page.setSize(userVO.getPageSize());
        page.setCurrent(userVO.getCurrentPage());
        IPage<User> pageList = baseMapper.getPageList(page, userVO);
        // ??????????????????
        List<User> userList = pageList.getRecords();
        // ????????????
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
            //????????????
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
        // ??????????????????userVO?????????????????????user???
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
