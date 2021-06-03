package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.CategoryMenuMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.CategoryMenuService;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class CategoryMenuServiceImpl implements CategoryMenuService{

    @Resource
    private CategoryMenuMapper categoryMenuMapper;

}
