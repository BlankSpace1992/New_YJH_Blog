package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.BlogSortMapper;
import com.blog.business.web.service.BlogSortService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class BlogSortServiceImpl implements BlogSortService {

    @Resource
    private BlogSortMapper blogSortMapper;

}
