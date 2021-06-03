package com.blog.controller;

import com.blog.business.admin.service.SystemConfigService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/6/3 14:18
 */
@RestController
@RequestMapping(value = "/systemConfig")
@Api(value = "002 - 系统配置相关接口模块", tags = "002 - 系统配置相关接口模块")
public class SystemConfigController {
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取配置信息
     *
     * @return 配置信息
     * @author yujunhong
     * @date 2021/6/3 14:25
     */
    @ApiOperation(value = "获取配置信息")
    @GetMapping(value = "/getSystemConfig")
    public ResultBody getSystemConfig() {
        return ResultBody.success(systemConfigService.getsSystemConfig());
    }
}
