package com.blog.business.picture.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.picture.mapper.StorageMapper;
import com.blog.business.picture.service.StorageService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/3 11:57
 * 
 */
@Service
public class StorageServiceImpl implements StorageService{

    @Resource
    private StorageMapper storageMapper;

}
