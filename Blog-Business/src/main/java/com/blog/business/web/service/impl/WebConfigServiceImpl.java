package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.WebConfig;
import com.blog.business.web.mapper.WebConfigMapper;
import com.blog.business.web.service.WebConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.enums.EnumAccountType;
import com.blog.exception.CommonErrorException;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PictureFeignClient pictureFeignClient;

    @Override
    public WebConfig getWebConfigByShowList() {
        // 优先从redis中获取Ip资源
        String result = (String) redisUtil.get(BaseRedisConf.WEB_CONFIG);
        // 判断redis中是否存在资源
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, WebConfig.class);
        }
        // 从数据获取
        LambdaQueryWrapper<WebConfig> webConfigWrapper = new LambdaQueryWrapper<>();
        WebConfig webConfig = Optional.ofNullable(this.getOne(webConfigWrapper)).orElseThrow(() ->
                new CommonErrorException(ErrorCode.SYSTEM_CONFIG_IS_NOT_EXIST,
                        BaseMessageConf.SYSTEM_CONFIG_IS_NOT_EXIST));
        // 拼接图片信息
        StringBuilder pictureBuilder = new StringBuilder();
        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo())) {
            pictureBuilder.append(webConfig.getLogo()).append(Constants.SYMBOL_COMMA);
        }
        // 支付宝图片
        if (StringUtils.isNotEmpty(webConfig.getAliPay())) {
            pictureBuilder.append(webConfig.getAliPay()).append(Constants.SYMBOL_COMMA);
        }
        // 微信图片
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay())) {
            pictureBuilder.append(webConfig.getWeixinPay()).append(Constants.SYMBOL_COMMA);
        }
        List<Map<String, Object>> picture = new ArrayList<>();
        if (StringUtils.isNotEmpty(pictureBuilder)) {
            picture = pictureFeignClient.getPicture(pictureBuilder.toString(),
                    Constants.SYMBOL_COMMA);
        }
        // 按照图片uid进行分组
        Map<String, String> pictureMap = new HashMap<>();
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSQLConf.UID).toString(), item.get(BaseSQLConf.URL).toString());
        });
        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo()) && StringUtils.isNotNull(pictureMap.get(webConfig.getLogo()))) {
            webConfig.setLogoPhoto(pictureMap.get(webConfig.getLogo()));
        }
        // 获取阿里支付码
        if (StringUtils.isNotEmpty(webConfig.getAliPay()) && StringUtils.isNotNull(pictureMap.get(webConfig.getAliPay()))) {
            webConfig.setAliPayPhoto(pictureMap.get(webConfig.getAliPay()));
        }
        // 获取微信支付码
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay()) && StringUtils.isNotNull(pictureMap.get(webConfig.getWeixinPay()))) {
            webConfig.setWeixinPayPhoto(pictureMap.get(webConfig.getWeixinPay()));
        }
        // 过滤一些不需要显示的用户账号信息
        String showListJson = webConfig.getShowList();
        // 获取联系方式
        String email = webConfig.getEmail();
        String qqNumber = webConfig.getQqNumber();
        String qqGroup = webConfig.getQqGroup();
        String github = webConfig.getGithub();
        String gitee = webConfig.getGitee();
        String weChat = webConfig.getWeChat();

        // 将联系方式全部置空
        webConfig.setEmail("");
        webConfig.setQqNumber("");
        webConfig.setQqGroup("");
        webConfig.setGithub("");
        webConfig.setGitee("");
        webConfig.setWeChat("");

        // 判断哪些联系方式需要显示出来
        List<String> showList = JSON.parseArray(showListJson, String.class);
        for (String item : showList) {
            if (EnumAccountType.EMail.getCode().equals(item)) {
                webConfig.setEmail(email);
            }
            if (EnumAccountType.QQNumber.getCode().equals(item)) {
                webConfig.setQqNumber(qqNumber);
            }
            if (EnumAccountType.QQGroup.getCode().equals(item)) {
                webConfig.setQqGroup(qqGroup);
            }
            if (EnumAccountType.Github.getCode().equals(item)) {
                webConfig.setGithub(github);
            }
            if (EnumAccountType.Gitee.getCode().equals(item)) {
                webConfig.setGitee(gitee);
            }
            if (EnumAccountType.WeChat.getCode().equals(item)) {
                webConfig.setWeChat(weChat);
            }
        }
        // 将WebConfig存到Redis中 [过期时间24小时]
        redisUtil.set(BaseRedisConf.WEB_CONFIG, JSON.toJSONString(webConfig), 24 * 60 * 60);
        return webConfig;
    }
}
