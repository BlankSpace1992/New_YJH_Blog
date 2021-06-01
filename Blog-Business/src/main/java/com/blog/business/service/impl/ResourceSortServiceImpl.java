package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.ResourceSortMapper;
import com.blog.business.service.ResourceSortService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class ResourceSortServiceImpl implements ResourceSortService{

    @Resource
    private ResourceSortMapper resourceSortMapper;

}
