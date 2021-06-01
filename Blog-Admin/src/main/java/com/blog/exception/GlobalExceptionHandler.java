package com.blog.exception;


import com.blog.exception.enums.BaseErrorCommonEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author yujunhong
 * @date 2021/4/29 11:18
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常
     *
     * @param commonErrorException 公共异常类
     * @return 异常实体对象
     * @author yujunhong
     * @date 2021/4/29 11:19
     */
    public ResultBody commonExceptionHandler(CommonErrorException commonErrorException) {
        log.error("发生业务异常！原因是：{}", commonErrorException.errorMessage);
        return ResultBody.error(commonErrorException.errorCode, commonErrorException.errorMessage);
    }

    /**
     * 处理空指针异常
     *
     * @param exception 空指针异常
     * @return 异常实体对象
     * @author yujunhong
     * @date 2021/4/29 11:22
     */
    @ExceptionHandler(value = NullPointerException.class)
    public ResultBody nullPointerExceptionHandler(NullPointerException exception) {
        log.error("发生空指针异常！原因是:", exception);
        return ResultBody.error(BaseErrorCommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 处理其他异常
     *
     * @param exception 异常
     * @return 异常实体对象
     * @author yujunhong
     * @date 2021/4/29 11:25
     */
    public ResultBody exceptionHandler(Exception exception) {
        log.error("发生其他异常！原因是:", exception);
        return ResultBody.error(BaseErrorCommonEnum.INTERNAL_SERVER_ERROR);
    }
}
