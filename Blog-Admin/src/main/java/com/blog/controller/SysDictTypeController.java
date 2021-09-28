package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;
import com.blog.business.web.service.SysDictTypeService;
import com.blog.constants.BaseMessageConf;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 增加字典类型
     *
     * @param sysDictTypeVO 增加字典类型实体
     * @return 增加字典类型
     * @author yujunhong
     * @date 2021/9/28 10:46
     */
    @ApiOperation(value = "增加字典类型")
    @PostMapping("/add")
    public ResultBody add(@RequestBody SysDictTypeVO sysDictTypeVO) {
        return sysDictTypeService.add(sysDictTypeVO);
    }

    /**
     * 编辑字典类型
     *
     * @param sysDictTypeVO 编辑字典类型实体
     * @return 编辑字典类型
     * @author yujunhong
     * @date 2021/9/28 10:46
     */
    @ApiOperation(value = "编辑字典类型")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody SysDictTypeVO sysDictTypeVO) {
        return sysDictTypeService.edit(sysDictTypeVO);
    }

    /**
     * 批量删除字典类型
     *
     * @param sysDictTypeVoList 批量删除字典类型实体集合
     * @return 批量删除字典类型
     * @author yujunhong
     * @date 2021/9/28 10:46
     */
    @ApiOperation(value = "批量删除字典类型")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<SysDictTypeVO> sysDictTypeVoList) {
        if (StringUtils.isEmpty(sysDictTypeVoList)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return sysDictTypeService.deleteBatch(sysDictTypeVoList);
    }
}
