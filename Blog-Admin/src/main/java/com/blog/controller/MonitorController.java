package com.blog.controller;

import com.blog.business.admin.domain.vo.ServerInfo.ServerInfo;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/10/11 16:41
 */
@RestController
@RequestMapping("/monitor")
@Api(value = "服务监控相关接口", tags = {"系统设置相关接口"})
public class MonitorController {

    /**
     * 获取服务信息
     *
     * @return 获取服务信息
     * @author yujunhong
     * @date 2021/10/11 16:41
     */
    @ApiOperation(value = "获取服务信息")
    @GetMapping("/getServerInfo")
    public ResultBody getServerInfo() {
        ServerInfo server = new ServerInfo();
        server.copyTo();
        return ResultBody.success(server);
    }
}
