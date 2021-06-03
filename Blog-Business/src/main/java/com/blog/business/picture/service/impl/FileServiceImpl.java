package com.blog.business.picture.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.picture.mapper.FileMapper;
import com.blog.business.picture.service.FileService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/3 11:57
 * 
 */
@Service
public class FileServiceImpl implements FileService{

    @Resource
    private FileMapper fileMapper;

}
