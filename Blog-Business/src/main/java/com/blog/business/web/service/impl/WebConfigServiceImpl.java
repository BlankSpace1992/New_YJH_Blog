package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.WebConfigVO;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.WebConfig;
import com.blog.business.web.mapper.WebConfigMapper;
import com.blog.business.web.service.WebConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.enums.EnumAccountType;
import com.blog.enums.EnumLoginType;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private WebUtils webUtils;

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

    @Override
    public Boolean isOpenLoginType(String loginType) {
        // 优先从redis获取
        String loginTypeJson = (String) redisUtil.get(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType);
        // 判断redis中是否包含该登录记录
        if (StringUtils.isNotEmpty(loginTypeJson)) {
            // 如果Redis中有内容，表示开启该登录方式
            return true;
        } else if (StringUtils.isNotNull(loginTypeJson) && loginTypeJson.length() == 0) {
            // 如果内容为空串，表示没有开启该登录方式
            return false;
        }
        LambdaQueryWrapper<WebConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WebConfig::getCreateTime);
        WebConfig webConfig = this.getOne(wrapper);
        if (StringUtils.isNull(webConfig)) {
            throw new CommonErrorException(ErrorCode.SYSTEM_CONFIG_IS_NOT_EXIST,
                    BaseMessageConf.SYSTEM_CONFIG_IS_NOT_EXIST);
        }
        // 过滤一些不需要显示的用户账号信息
        String loginTypeListJson = webConfig.getLoginTypeList();
        // 判断哪些联系方式需要显示出来
        List<String> loginTypeList = JSON.parseArray(loginTypeListJson, String.class);
        for (String item : loginTypeList) {
            if (EnumLoginType.PASSWORD.getCode().equals(item)) {
                redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + BaseRedisConf.PASSWORD,
                        EnumLoginType.PASSWORD.getName());
            }
            if (EnumLoginType.GITEE.getCode().equals(item)) {
                redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + BaseRedisConf.GITEE,
                        EnumLoginType.GITEE.getName());
            }
            if (EnumLoginType.GITHUB.getCode().equals(item)) {
                redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + BaseRedisConf.GITHUB,
                        EnumLoginType.GITHUB.getName());
            }
            if (EnumLoginType.QQ.getCode().equals(item)) {
                redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + BaseRedisConf.QQ,
                        EnumLoginType.QQ.getName());
            }
            if (EnumLoginType.WECHAT.getCode().equals(item)) {
                redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + BaseRedisConf.WECHAT,
                        EnumLoginType.WECHAT.getName());
            }
        }
        // 再次判断该登录方式是否开启
        loginTypeJson = (String) redisUtil.get(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType);
        if (StringUtils.isNotEmpty(loginTypeJson)) {
            return true;
        } else {
            // 设置一个为空的字符串【防止缓存穿透】
            redisUtil.set(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_COLON + loginType, "");
            return false;
        }
    }

    @Override
    public ResultBody getWebSiteName() {
        LambdaQueryWrapper<WebConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.last(BaseSysConf.LIMIT_ONE);
        WebConfig webConfig = this.getOne(lambdaQueryWrapper);
        if (StringUtils.isNotEmpty(webConfig.getName())) {
            return ResultBody.success(webConfig.getName());
        }
        return ResultBody.success();
    }

    @Override
    public WebConfig getWebConfig() {
        LambdaQueryWrapper<WebConfig> webConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        webConfigLambdaQueryWrapper.orderByDesc(WebConfig::getCreateTime);
        webConfigLambdaQueryWrapper.last(BaseSysConf.LIMIT_ONE);
        WebConfig webConfig = this.getOne(webConfigLambdaQueryWrapper);
        //获取图片
        if (StringUtils.isNotNull(webConfig) && StringUtils.isNotEmpty(webConfig.getLogo())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(webConfig.getLogo(),
                    BaseSysConf.FILE_SEGMENTATION);
            webConfig.setPhotoList(webUtils.getPicture(picture));
        }
        return webConfig;
    }

    @Override
    public ResultBody editWebConfig(WebConfigVO webConfigVO) {
        if (StringUtils.isEmpty(webConfigVO.getUid())) {
            WebConfig webConfig = new WebConfig();
            // 设置网站配置【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(webConfigVO, webConfig, BaseSysConf.STATUS);
            this.save(webConfig);

        } else {
            WebConfig webConfig = this.getById(webConfigVO.getUid());
            // 更新网站配置【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(webConfigVO, webConfig, BaseSysConf.STATUS, BaseSysConf.UID);
            webConfig.setUpdateTime(new Date());
            this.updateById(webConfig);
        }
        // 修改配置后，清空Redis中的 WEB_CONFIG
        redisUtil.delete(BaseRedisConf.WEB_CONFIG);
        // 同时清空Redis中的登录方式
        Set<Object> keySet = redisUtil.keys(BaseRedisConf.LOGIN_TYPE + Constants.SYMBOL_STAR);
        redisUtil.delete(keySet);
        return ResultBody.success();
    }
}
