package com.blog.business.picture.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.picture.mapper.NetworkDiskMapper;
import com.blog.business.picture.service.NetworkDiskService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/3 11:57
 * 
 */
@Service
public class NetworkDiskServiceImpl implements NetworkDiskService{

    @Resource
    private NetworkDiskMapper networkDiskMapper;

}
