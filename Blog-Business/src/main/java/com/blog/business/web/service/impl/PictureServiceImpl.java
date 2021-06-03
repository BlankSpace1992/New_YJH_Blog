package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.PictureMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.blog.business.web.service.PictureService;
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
