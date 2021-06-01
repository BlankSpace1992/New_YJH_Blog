package com.blog.exception.enums;


import com.blog.exception.interfaces.BaseErrorInterface;

/**
 * @author yujunhong
 * @date 2021/4/29 10:46
 */
public enum BaseErrorCommonEnum implements BaseErrorInterface {
    /**
     * 成功-200
     */
    SUCCESS("200", "成功"),
    /**
     * 请求的数据格式不符-400
     */
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    /**
     * 请求的数字签名不匹配-401
     */
    SIGNATURE_NOT_MATCH("401", "请求的数字签名不匹配!"),
    /**
     * 未找到该资源-404
     */
    NOT_FOUND("404", "未找到该资源!"),
    /**
     * 服务器内部错误-500
     */
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    /**
     * 服务器正忙，请稍后再试-503
     */
    SERVER_BUSY("503", "服务器正忙，请稍后再试!");


    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误信息
     */
    private final String errorMessage;

    BaseErrorCommonEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
