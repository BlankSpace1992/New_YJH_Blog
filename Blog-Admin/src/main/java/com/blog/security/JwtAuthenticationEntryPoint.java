package com.blog.security;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.blog.constants.HttpCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/5/28 16:34
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        String msg = CharSequenceUtil.format("请求访问：{}，认证失败，无法访问系统资源", httpServletRequest.getRequestURI());
        httpServletResponse.setStatus(HttpCode.UNAUTHORIZED);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        Map<String, Object> returnResult = new HashMap<>();
        returnResult.put("code", HttpCode.UNAUTHORIZED);
        returnResult.put("msg", msg);
        returnResult.put("data", "token无效或过期,请重新登录");
        httpServletResponse.getWriter().write(JSON.toJSONString(returnResult));
    }
}
