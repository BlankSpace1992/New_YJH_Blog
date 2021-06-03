package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.SystemConfigMapper;
import com.blog.business.web.service.SystemConfigService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

}
