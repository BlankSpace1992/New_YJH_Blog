package com.cloud.blog.controller;

import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.service.AdminService;
import com.blog.business.web.domain.WebConfig;
import com.blog.business.web.service.WebConfigService;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/9/1 16:24
 */
@RestController
@RequestMapping(value = "/about")
@Api(value = "关于我相关接口", tags = "关于我相关接口")
public class AboutMeController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private WebConfigService webConfigService;

    /**
     * 获取关于我的信息
     *
     * @return 关于我的信息
     * @author yujunhong
     * @date 2021/9/1 16:25
     */
    @GetMapping(value = "/getMe")
    @ApiOperation(value = "获取关于我的信息")
    public ResultBody getAboutMe() {
        Admin adminByUserName = adminService.getAdminByUserName(BaseSysConf.ADMIN);
        return ResultBody.success(adminByUserName);
    }

    /**
     * 获取联系方式
     *
     * @return 联系方式
     * @author yujunhong
     * @date 2021/9/1 16:38
     */
    @ApiOperation(value = "获取联系方式")
    @GetMapping(value = "/getContact")
    public ResultBody getContact() {
        WebConfig webConfigByShowList = webConfigService.getWebConfigByShowList();
        return ResultBody.success(webConfigByShowList);
    }
}
