package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.CommentMapper;
import com.blog.business.web.service.CommentService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

}
