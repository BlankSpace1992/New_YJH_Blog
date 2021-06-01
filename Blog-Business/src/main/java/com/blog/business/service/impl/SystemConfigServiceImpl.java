package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.SystemConfigMapper;
import com.blog.business.service.SystemConfigService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService{

    @Resource
    private SystemConfigMapper systemConfigMapper;

}
