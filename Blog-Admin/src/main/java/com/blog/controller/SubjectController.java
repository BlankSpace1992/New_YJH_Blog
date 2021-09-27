package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.vo.SubjectVO;
import com.blog.business.web.service.SubjectService;
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
}
