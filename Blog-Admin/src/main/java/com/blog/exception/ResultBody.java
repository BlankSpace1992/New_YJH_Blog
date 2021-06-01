package com.blog.exception;

import com.alibaba.fastjson.JSONObject;
import com.blog.exception.enums.BaseErrorCommonEnum;
import com.blog.exception.interfaces.BaseErrorInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yujunhong
 * @date 2021/4/29 11:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultBody {
    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object result;

    public ResultBody(BaseErrorInterface baseErrorInterface) {
        this.code = baseErrorInterface.getErrorCode();
        this.message = baseErrorInterface.getErrorMessage();
    }

    /**
     * 成功返回实体
     *
     * @return 成功实体对象
     * @author yujunhong
     * @date 2021/4/29 11:01
     */
    public static ResultBody success() {
        return success(null);
    }

    /**
     * 成功返回实体 并返回信息
     *
     * @param data 获取数据
     * @return 成功实体对象
     * @author yujunhong
     * @date 2021/4/29 11:03
     */
    public static ResultBody success(Object data) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(BaseErrorCommonEnum.SUCCESS.getErrorCode());
        resultBody.setMessage(BaseErrorCommonEnum.SUCCESS.getErrorMessage());
        resultBody.setResult(data);
        return resultBody;
    }

    /**
     * 失败返回实体
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 失败实体对象
     * @author yujunhong
     * @date 2021/4/29 11:09
     */
    public static ResultBody error(String code, String message) {
        ResultBody resultBody = new ResultBody();
        resultBody.setMessage(message);
        resultBody.setCode(code);
        resultBody.setResult(null);
        return resultBody;
    }

    /**
     * 失败返回实体
     *
     * @param baseErrorInterface 公共错误接口
     * @return 失败实体对象
     * @author yujunhong
     * @date 2021/4/29 11:09
     */
    public static ResultBody error(BaseErrorInterface baseErrorInterface) {
        ResultBody resultBody = new ResultBody();
        resultBody.setMessage(baseErrorInterface.getErrorMessage());
        resultBody.setCode(baseErrorInterface.getErrorCode());
        resultBody.setResult(null);
        return resultBody;
    }

    /**
     * 失败返回实体
     *
     * @param message 错误信息
     * @return 失败实体对象
     * @author yujunhong
     * @date 2021/4/29 11:09
     */
    public static ResultBody error(String message) {
        ResultBody resultBody = new ResultBody();
        resultBody.setMessage(message);
        resultBody.setCode("-1");
        resultBody.setResult(null);
        return resultBody;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
