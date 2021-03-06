package com.blog.holder;

import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.ErrorCode;
import com.blog.exception.CommonErrorException;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 处理请求工具类
 *
 * @author yujunhong
 * @date 2021/7/7 16:54
 */
@Slf4j
public class RequestHolder {
    /**
     * 获取request
     *
     * @return request请求
     * @author yujunhong
     * @date 2021/7/7 16:56
     */
    public static HttpServletRequest getRequest() {
        log.debug("getRequest -- Thread id :{}, name : {}", Thread.currentThread().getId(),
                Thread.currentThread().getName());
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (StringUtils.isNull(requestAttributes)) {
            return null;
        }
        return requestAttributes.getRequest();
    }

    /**
     * 获取response
     *
     * @return response响应
     * @author yujunhong
     * @date 2021/7/7 16:59
     */
    public static HttpServletResponse getResponse() {
        log.debug("getResponse -- Thread id :{}, name : {}", Thread.currentThread().getId(),
                Thread.currentThread().getName());
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (StringUtils.isNull(requestAttributes)) {
            return null;
        }
        return requestAttributes.getResponse();
    }

    /**
     * 获取session
     *
     * @return session
     * @author yujunhong
     * @date 2021/7/7 17:00
     */
    public static HttpSession getSession() {
        log.debug("getSession -- Thread id :{}, name : {}", Thread.currentThread().getId(),
                Thread.currentThread().getName());
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (StringUtils.isNull(request = getRequest())) {
            return null;
        }
        return request.getSession();
    }

    /**
     * 获取AdminUid
     *
     * @return 用户uid
     * @author yujunhong
     * @date 2021/10/9 15:42
     */
    public static String getAdminUid() {
        HttpServletRequest request = getRequest();
        if (StringUtils.isNotNull(request)) {
            if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
                return request.getAttribute(BaseSysConf.ADMIN_UID).toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取AdminToken
     *
     * @return 获取AdminToken
     * @author yujunhong
     * @date 2021/10/9 15:44
     */
    public static String getAdminToken() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (StringUtils.isNotNull(request)) {
            if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.TOKEN))) {
                return request.getAttribute(BaseSysConf.TOKEN).toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 检查当前用户是否登录【未登录操作将抛出QueryException异常】
     *
     * @return 检查当前用户是否登录
     * @author yujunhong
     * @date 2021/10/9 15:45
     */
    public static String checkLogin() {
        if (StringUtils.isEmpty(getAdminUid())) {
            log.error("用户未登录");
            throw new CommonErrorException(ErrorCode.INVALID_TOKEN, BaseMessageConf.INVALID_TOKEN);
        }
        return getAdminUid();
    }
}
