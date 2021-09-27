package com.blog.controller;

import com.blog.business.admin.domain.vo.WebConfigVO;
import com.blog.business.web.domain.WebConfig;
import com.blog.business.web.service.WebConfigService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yujunhong
 * @date 2021/9/27 11:31
 */
@Api(value = "网站配置相关接口", tags = {"网站配置相关接口"})
@RestController
@RequestMapping("/webConfig")
public class WebConfigController {

    @Autowired
    WebConfigService webConfigService;

    /**
     * 获取网站配置
     *
     * @return 获取网站配置
     * @author yujunhong
     * @date 2021/9/27 11:31
     */
    @ApiOperation(value = "获取网站配置")
    @GetMapping("/getWebConfig")
    public ResultBody getWebConfig() {
        WebConfig webConfig = webConfigService.getWebConfig();
        return ResultBody.success(webConfig);
    }

    /**
     * 修改网站配置
     *
     * @param webConfigVO 修改实体
     * @return 修改网站配置
     * @author yujunhong
     * @date 2021/9/27 11:42
     */
    @ApiOperation(value = "修改网站配置")
    @PostMapping("/editWebConfig")
    public ResultBody editWebConfig(@RequestBody WebConfigVO webConfigVO) {
        return webConfigService.editWebConfig(webConfigVO);
    }
}
