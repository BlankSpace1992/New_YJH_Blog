package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.User;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.mapper.UserMapper;
import com.blog.business.web.service.UserService;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.exception.CommonErrorException;
import com.blog.holder.RequestHolder;
import com.blog.utils.IpUtils;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
}
