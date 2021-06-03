package com.blog.picture.config;

import com.alibaba.fastjson.JSON;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.entity.OnlineAdminUser;
import com.blog.utils.SpringUtils;
import com.blog.utils.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token拦截器
 *
 * @author yujunhong
 * @date 2021/6/3 11:42
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //得到请求头信息authorization信息
        String authHeader = "";

        if (request.getHeader("Authorization") != null) {
            authHeader = request.getHeader("Authorization");
        } else if (request.getParameter(BaseSysConf.TOKEN) != null) {
            authHeader = request.getParameter(BaseSysConf.TOKEN);
        }

        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("bearer_")) {
            // 获取在线的管理员信息
            RedisUtil redisUtil = SpringUtils.getBean(RedisUtil.class);
            String onlineAdmin =
                    (String) redisUtil.get(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + authHeader);
            if (StringUtils.isNotEmpty(onlineAdmin)) {
                // 得到管理员UID和 Name
                OnlineAdminUser admin = JSON.parseObject(onlineAdmin, OnlineAdminUser.class);
                request.setAttribute(BaseSysConf.ADMIN_UID, admin.getAdminUid());
                request.setAttribute(BaseSysConf.NAME, admin.getUserName());
                request.setAttribute(BaseSysConf.TOKEN, authHeader);
            }
        }
        return true;
    }
}
