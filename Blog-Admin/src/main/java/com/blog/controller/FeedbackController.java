package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.FeedbackAdminVO;
import com.blog.business.web.domain.Feedback;
import com.blog.business.web.service.FeedbackService;
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
 * @date 2021/10/8 15:17
 */
@RestController
@Api(value = "用户反馈相关接口", tags = {"用户反馈相关接口"})
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    /**
     * 获取反馈列表
     *
     * @param feedbackAdminVO 查询条件
     * @return 获取反馈列表
     * @author yujunhong
     * @date 2021/10/8 15:17
     */
    @ApiOperation(value = "获取反馈列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody FeedbackAdminVO feedbackAdminVO) {
        IPage<Feedback> pageList = feedbackService.getPageList(feedbackAdminVO);
        return ResultBody.success(pageList);
    }

    /**
     * 编辑反馈
     *
     * @param feedbackAdminVO 编辑反馈实体
     * @return 编辑反馈
     * @author yujunhong
     * @date 2021/10/8 15:26
     */
    @ApiOperation(value = "编辑反馈")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody FeedbackAdminVO feedbackAdminVO) {
        return feedbackService.edit(feedbackAdminVO);
    }

    /**
     * 批量删除反馈
     *
     * @param feedbackVOList 批量删除反馈集合
     * @return 批量删除反馈
     * @author yujunhong
     * @date 2021/10/8 15:31
     */
    @ApiOperation(value = "批量删除反馈")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<FeedbackAdminVO> feedbackVOList) {
        if (StringUtils.isEmpty(feedbackVOList)) {
            return ResultBody.error("请选择需要删除的反馈");
        }
        return feedbackService.deleteBatch(feedbackVOList);
    }
}
