package com.blog.exception.interfaces;

/**
 * @author yujunhong
 * @date 2021/4/29 10:43
 */
public interface BaseErrorInterface {
    /**
     * 获取错误代码
     *
     * @return 错误代码
     * @author yujunhong
     * @date 2021/4/29 10:43
     */
    String getErrorCode();

    /**
     * 获取错误信息描述
     *
     * @return 错误信息描述
     * @author yujunhong
     * @date 2021/4/29 10:44
     */
    String getErrorMessage();
}
