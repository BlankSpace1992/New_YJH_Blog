package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.CommentVO;
import com.blog.business.web.domain.Comment;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 获取评论列表
     *
     * @param commentVO 查询条件
     * @param page      分页参数
     * @return 获取评论列表
     * @author yujunhong
     * @date 2021/10/8 14:15
     */
    IPage<Comment> getPageList(@Param("page") IPage<Comment> page, @Param("commentVO") CommentVO commentVO);
}
