package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.PictureSortMapper;
import com.blog.business.service.PictureSortService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class PictureSortServiceImpl implements PictureSortService{

    @Resource
    private PictureSortMapper pictureSortMapper;

}
