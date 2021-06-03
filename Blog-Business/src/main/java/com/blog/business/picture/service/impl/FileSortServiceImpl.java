package com.blog.business.picture.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.picture.mapper.FileSortMapper;
import com.blog.business.picture.service.FileSortService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/3 11:57
 * 
 */
@Service
public class FileSortServiceImpl implements FileSortService{

    @Resource
    private FileSortMapper fileSortMapper;

}
