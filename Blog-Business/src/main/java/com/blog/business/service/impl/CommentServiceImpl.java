package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.CommentMapper;
import com.blog.business.service.CommentService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class CommentServiceImpl implements CommentService{

    @Resource
    private CommentMapper commentMapper;

}
