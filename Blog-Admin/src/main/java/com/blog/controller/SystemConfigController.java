package com.blog.controller;

import com.blog.business.admin.domain.vo.SystemConfigVO;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResultBody.success(systemConfigService.getSystemConfig());
    }

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param keyList 键集合
     * @return 通过Key前缀清空Redis缓存
     * @author yujunhong
     * @date 2021/10/22 13:45
     */
    @ApiOperation(value = "通过Key前缀清空Redis缓存")
    @PostMapping("/cleanRedisByKey")
    public ResultBody cleanRedisByKey(@RequestBody List<String> keyList) {
        return systemConfigService.cleanRedisByKey(keyList);
    }

    /**
     * 修改系统配置
     *
     * @param systemConfigVO 编辑实体对象
     * @return 修改系统配置
     * @author yujunhong
     * @date 2021/10/22 13:50
     */
    @ApiOperation(value = "修改系统配置")
    @PostMapping("/editSystemConfig")
    public ResultBody editSystemConfig(@RequestBody SystemConfigVO systemConfigVO) {
        return systemConfigService.editSystemConfig(systemConfigVO);
    }
}
