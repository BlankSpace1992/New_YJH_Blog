package com.blog.business.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.blog.business.mapper.WebConfigMapper;
import com.blog.business.service.WebConfigService;
/**
 * 
 * @author yujunhong
 * @date 2021/6/1 11:05
 * 
 */
@Service
public class WebConfigServiceImpl implements WebConfigService{

    @Resource
    private WebConfigMapper webConfigMapper;

}
