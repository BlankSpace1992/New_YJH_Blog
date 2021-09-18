package com.blog.security;

import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSON;
import com.blog.config.jwt.Audience;
import com.blog.config.jwt.JwtTokenUtil;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.entity.OnlineAdminUser;
import com.blog.utils.CookieUtils;
import com.blog.utils.DateUtils;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author yujunhong
 * @date 2021/5/28 17:13
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private Audience audience;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value(value = "${tokenHead}")
    private String tokenHead;

    /**
     * token 传参header
     */
    @Value(value = "${tokenHeader}")
    private String tokenHeader;

    /**
     * token过期的时间
     */
    @Value(value = "${audience.expiresSecond}")
    private Long expiresSecond;

    /**
     * token刷新的时间
     */
    @Value(value = "${audience.refreshSecond}")
    private Long refreshSecond;

    /**
     * redis 工具类
     */
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 得到消息头的 authorization信息
        String authHeader = httpServletRequest.getHeader(tokenHeader);
        // 请求头 "Authorization" = tokenHead + token
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith(tokenHead)) {
            log.info("传递过来的token为: {}", authHeader);
            // 获取token值
            String token = authHeader.substring(tokenHead.length());
            // 获取私钥
            String base64Secret = audience.getBase64Secret();
            // 获取在线管理员信息
            String onlineAdminUser =
                    (String) redisUtil.get(BaseRedisConf.LOGIN_TOKEN_KEY + BaseRedisConf.SEGMENTATION + authHeader);
            // 判断管理员是否在线,且token是否过期
            if (StringUtils.isNotEmpty(onlineAdminUser) && !jwtTokenUtil.isExpired(token, base64Secret)) {
                // 获取过期时间
                Date expire = jwtTokenUtil.getExpire(token, base64Secret);
                // 获取当前时间
                Date currentNow = DateUtils.getNowDate();
                // 获取当前时间与过期时间间隔--秒
                long survivalSecond = DateUtils.between(expire, currentNow, DateUnit.SECOND);
                // 当存活时间小于更新时间 那么将生成新的token导客户端,同时重置过期时间
                // 而旧的token将会在不久之后在redis之中过期
                if (survivalSecond < refreshSecond) {
                    // 生成一个新的token
                    String newToken = tokenHead + jwtTokenUtil.refreshToken(token, base64Secret, expiresSecond * 1000);
                    // 生成新的token发送到客户端
                    CookieUtils.setCookie("Admin-Token", newToken, expiresSecond.intValue());
                    // 将onlineAdminUser转换为onlineAdminUser对象
                    OnlineAdminUser onlineAdmin = JSON.parseObject(onlineAdminUser, OnlineAdminUser.class);
                    // 获取旧的TokenUid
                    String oldTokenId = onlineAdmin.getTokenId();
                    // 随机生成一个TokenUid，用于换取Token令牌
                    String tokenUid = StringUtils.getUUID();
                    onlineAdmin.setTokenId(tokenUid);
                    onlineAdmin.setToken(newToken);
                    onlineAdmin.setExpireTime(DateUtils.getDateStr(new Date(), expiresSecond));
                    onlineAdmin.setLoginTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", DateUtils.getNowDate()));
                    // 将原有redis中的旧token和tokenId移除
                    redisUtil.delete(BaseRedisConf.LOGIN_TOKEN_KEY + Constants.SYMBOL_COLON + authHeader);
                    redisUtil.delete(BaseRedisConf.LOGIN_UUID_KEY + Constants.SYMBOL_COLON + oldTokenId);
                    // 将新token赋值，用于后续使用
                    authHeader = newToken;
                    // 将新的Token存入Redis中
                    redisUtil.set(BaseRedisConf.LOGIN_TOKEN_KEY + Constants.SYMBOL_COLON + newToken,
                            JSON.toJSONString(onlineAdmin), expiresSecond);
                    // 维护 uuid - token 互相转换的Redis集合【主要用于在线用户管理】
                    redisUtil.set(BaseRedisConf.LOGIN_UUID_KEY + Constants.SYMBOL_COLON + tokenUid, newToken,
                            expiresSecond);
                }
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            // 根据token获取用户名
            String username = jwtTokenUtil.getUsername(token, base64Secret);
            // 获取用户uuid
            String userUid = jwtTokenUtil.getUserUid(token, base64Secret);
            httpServletRequest.setAttribute(BaseSysConf.ADMIN_UID, userUid);
            httpServletRequest.setAttribute(BaseSysConf.USER_NAME, username);
            httpServletRequest.setAttribute(BaseSysConf.TOKEN, authHeader);
            log.info("解析出来用户: {}", username);
            log.info("解析出来的用户Uid: {}", userUid);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 通过用户名加载SpringSecurity用户
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // 校验Token的有效性
                if (jwtTokenUtil.validateToken(token, userDetails, base64Secret)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            httpServletRequest));
                    //以后可以security中取得SecurityUser信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
