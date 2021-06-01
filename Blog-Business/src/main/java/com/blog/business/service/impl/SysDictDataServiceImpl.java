package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.SysDictDataMapper;
import com.blog.business.service.SysDictDataService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class SysDictDataServiceImpl implements SysDictDataService{

    @Resource
    private SysDictDataMapper sysDictDataMapper;

}
