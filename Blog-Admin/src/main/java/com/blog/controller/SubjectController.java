package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.vo.SubjectVO;
import com.blog.business.web.service.SubjectService;
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
 * @date 2021/9/24 09:59
 */
@Api(value = "专题相关接口", tags = {"专题相关接口"})
@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 获取专题列表
     *
     * @param subjectVO 查询条件
     * @return 专题列表
     * @author yujunhong
     * @date 2021/9/3 17:01
     */
    @ApiOperation(value = "获取专题列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody SubjectVO subjectVO) {
        IPage<Subject> list = subjectService.getList(subjectVO);
        return ResultBody.success(list);
    }

    /**
     * 增加专题
     *
     * @param subjectVO 实体对象
     * @return 增加专题
     * @author yujunhong
     * @date 2021/10/12 14:58
     */
    @ApiOperation(value = "增加专题")
    @PostMapping("/add")
    public ResultBody add(@RequestBody SubjectVO subjectVO) {
        return subjectService.add(subjectVO);
    }

    /**
     * 编辑专题
     *
     * @param subjectVO 实体对象
     * @return 编辑专题
     * @author yujunhong
     * @date 2021/10/12 14:58
     */
    @ApiOperation(value = "编辑专题")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody SubjectVO subjectVO) {
        return subjectService.edit(subjectVO);
    }

    /**
     * 批量删除专题
     *
     * @param subjectVOList 删除实体集合
     * @return 批量删除专题
     * @author yujunhong
     * @date 2021/10/12 15:09
     */
    @ApiOperation(value = "批量删除专题")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<SubjectVO> subjectVOList) {
        if (StringUtils.isEmpty(subjectVOList)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return subjectService.deleteBatch(subjectVOList);
    }

}
