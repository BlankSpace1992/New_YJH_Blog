package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.PictureMapper;
import com.blog.business.service.PictureService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class PictureServiceImpl implements PictureService{

    @Resource
    private PictureMapper pictureMapper;

}
