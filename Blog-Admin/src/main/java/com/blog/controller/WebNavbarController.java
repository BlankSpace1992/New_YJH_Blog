package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.WebNavbarVO;
import com.blog.business.web.domain.WebNavbar;
import com.blog.business.web.service.WebNavbarService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/10/11 15:35
 */
@RestController
@RequestMapping("/webNavbar")
@Api(value = "门户导航栏管理", tags = {"门户导航栏相关接口"})
public class WebNavbarController {
    @Autowired
    private WebNavbarService webNavbarService;

    /**
     * 获取门户导航栏列表
     *
     * @param webNavbarVO 查询条件
     * @return 获取门户导航栏列表
     * @author yujunhong
     * @date 2021/10/11 15:54
     */
    @ApiOperation(value = "获取门户导航栏列表")
    @GetMapping("/getList")
    public ResultBody getList(@RequestBody WebNavbarVO webNavbarVO) {
        IPage<WebNavbar> pageList =
                webNavbarService.getPageList(webNavbarVO);
        return ResultBody.success();
    }

    /**
     * 获取门户导航栏所有列表
     *
     * @return 获取门户导航栏所有列表
     * @author yujunhong
     * @date 2021/10/11 16:02
     */
    @ApiOperation(value = "获取门户导航栏所有列表", notes = "获取门户导航栏所有列表", response = String.class)
    @GetMapping("/getAllList")
    public ResultBody getAllList() {
        List<WebNavbar> allList =
                webNavbarService.getAllList();
        return ResultBody.success(allList);
    }

    /**
     * 增加门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 增加门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:03
     */
    @ApiOperation(value = "增加门户导航栏")
    @PostMapping("/add")
    public ResultBody add(@RequestBody WebNavbarVO webNavbarVO) {
        return webNavbarService.add(webNavbarVO);
    }

    /**
     * 编辑门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 编辑门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:03
     */
    @ApiOperation(value = "编辑门户导航栏")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody WebNavbarVO webNavbarVO) {
        return webNavbarService.edit(webNavbarVO);
    }

    /**
     * 删除门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 删除门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:03
     */
    @ApiOperation(value = "删除门户导航栏")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody WebNavbarVO webNavbarVO) {
        return webNavbarService.delete(webNavbarVO);
    }
}
