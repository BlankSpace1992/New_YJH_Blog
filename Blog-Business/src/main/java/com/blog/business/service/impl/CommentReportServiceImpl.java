package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.CommentReportMapper;
import com.blog.business.service.CommentReportService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class CommentReportServiceImpl implements CommentReportService{

    @Resource
    private CommentReportMapper commentReportMapper;

}
