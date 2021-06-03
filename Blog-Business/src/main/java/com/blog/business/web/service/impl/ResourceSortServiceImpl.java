package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.ResourceSortMapper;
import com.blog.business.web.service.ResourceSortService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class ResourceSortServiceImpl implements ResourceSortService {

    @Resource
    private ResourceSortMapper resourceSortMapper;

}
