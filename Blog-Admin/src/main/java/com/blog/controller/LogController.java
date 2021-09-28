package com.blog.controller;

import com.blog.business.web.service.ExceptionLogService;
import com.blog.business.web.service.SysLogService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/9/28 15:51
 */
@RestController
@Api(value = "操作日志相关接口", tags = {"操作日志相关接口"})
@RequestMapping("/log")
public class LogController {

    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private ExceptionLogService exceptionLogService;
}
