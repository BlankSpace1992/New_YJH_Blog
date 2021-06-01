package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.SysDictTypeMapper;
import com.blog.business.service.SysDictTypeService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService{

    @Resource
    private SysDictTypeMapper sysDictTypeMapper;

}
