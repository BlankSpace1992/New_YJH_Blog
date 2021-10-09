package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.FeedbackAdminVO;
import com.blog.business.web.domain.Feedback;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {

    /**
     * 获取反馈列表
     *
     * @param feedbackAdminVO 查询条件
     * @param page            分页参数
     * @return 获取反馈列表
     * @author yujunhong
     * @date 2021/10/8 15:19
     */
    IPage<Feedback> getPageList(@Param("page") IPage<Feedback> page,
                                @Param("feedbackAdminVO") FeedbackAdminVO feedbackAdminVO);
}
