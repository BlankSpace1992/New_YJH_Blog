package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;
import com.blog.business.web.service.SysDictTypeService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/9/27 15:36
 */
@RestController
@RequestMapping("/sysDictType")
@Api(value = "字典类型相关接口", tags = {"字典类型相关接口"})
public class SysDictTypeController {
    @Autowired
    private SysDictTypeService sysDictTypeService;

    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeVO 查询条件
     * @return 获取字典类型列表
     * @author yujunhong
     * @date 2021/9/27 16:13
     */
    @ApiOperation(value = "获取字典类型列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody SysDictTypeVO sysDictTypeVO) {
        IPage<SysDictType> pageList =
                sysDictTypeService.getPageList(sysDictTypeVO);
        return ResultBody.success(pageList);
    }
}
