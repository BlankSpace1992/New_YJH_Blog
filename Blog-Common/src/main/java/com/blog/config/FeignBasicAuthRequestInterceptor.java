package com.blog.config;

import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.utils.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yujunhong
 * @date 2021/6/3 14:44
 */
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        // 获取Http请求
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 获取token，放入到feign的请求头
        String token = null;
        if (request != null) {
            if (request.getParameter(BaseSysConf.TOKEN) != null) {
                token = request.getParameter(BaseSysConf.TOKEN);
            } else if (request.getAttribute(BaseSysConf.TOKEN) != null) {
                token = request.getAttribute(BaseSysConf.TOKEN).toString();
            }
        }

        if (StringUtils.isNotEmpty(token)) {
            // 如果带有？说明还带有其它参数，我们只截取到token即可
            if (token.contains(Constants.SYMBOL_QUESTION)) {
                String[] params = token.split("\\?url=");
                token = params[0];
            }
            requestTemplate.header(BaseSysConf.PICTURE_TOKEN, token);
        }
    }
}
