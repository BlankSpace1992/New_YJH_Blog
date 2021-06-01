package com.cloud.blog.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器
 *
 * @author yujunhong
 * @date 2021/6/1 10:20
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 得到请求头信息authorization信息
        String accessToken = httpServletRequest.getHeader("Authorization");
        // 判空
        if (StringUtils.isNotEmpty(accessToken)) {
            // 从redis中获取内容
            String userInfo = (String) redisUtil.get(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
            if (StringUtils.isNotEmpty(userInfo)) {
                JSONObject jsonObject = JSON.parseObject(userInfo, JSONObject.class);
                // 将userId存储进request中
                httpServletRequest.setAttribute(BaseSysConf.TOKEN, accessToken);
                httpServletRequest.setAttribute(BaseSysConf.USER_NAME, jsonObject.getString(BaseSysConf.UID));
                httpServletRequest.setAttribute(BaseSysConf.USER_NAME, jsonObject.getString(BaseSysConf.NICK_NAME));
                log.info("解析出来的用户:{}", jsonObject.get(BaseSysConf.NICK_NAME));
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
