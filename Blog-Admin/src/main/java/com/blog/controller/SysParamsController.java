package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.SysParamsVO;
import com.blog.business.web.domain.SysParams;
import com.blog.business.web.service.SysParamsService;
import com.blog.exception.ResultBody;
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
 * @date 2021/9/27 14:19
 */
@RestController
@RequestMapping("/sysParams")
@Api(value = "参数配置相关接口", tags = {"参数配置相关接口"})
public class SysParamsController {
    @Autowired
    private SysParamsService sysParamsService;

    /**
     * 获取参数配置列表
     *
     * @param sysParamsVO 查询条件
     * @return 参数配置列表
     * @author yujunhong
     * @date 2021/9/27 14:27
     */
    @ApiOperation(value = "获取参数配置列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody SysParamsVO sysParamsVO) {
        IPage<SysParams> sysParamsPageList =
                sysParamsService.getSysParamsPageList(sysParamsVO);
        return ResultBody.success(sysParamsPageList);
    }

    /**
     * 增加参数配置
     *
     * @param sysParamsVO 增加实体
     * @return 增加参数配置
     * @author yujunhong
     * @date 2021/9/27 14:35
     */
    @ApiOperation(value = "增加参数配置")
    @PostMapping("/add")
    public ResultBody add(@RequestBody SysParamsVO sysParamsVO) {
        return sysParamsService.add(sysParamsVO);
    }

    /**
     * 编辑参数配置
     *
     * @param sysParamsVO 编辑实体
     * @return 编辑参数配置
     * @author yujunhong
     * @date 2021/9/27 14:35
     */
    @ApiOperation(value = "编辑参数配置")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody SysParamsVO sysParamsVO) {
        return sysParamsService.edit(sysParamsVO);
    }

    /**
     * 批量删除参数配置
     *
     * @param sysParamsVoList 需要删除参数集合
     * @return 批量删除参数配置
     * @author yujunhong
     * @date 2021/9/27 14:43
     */
    @ApiOperation(value = "批量删除参数配置")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<SysParamsVO> sysParamsVoList) {
        return sysParamsService.deleteBatch(sysParamsVoList);
    }
}
