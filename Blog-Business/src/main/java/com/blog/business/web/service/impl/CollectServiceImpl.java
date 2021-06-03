package com.blog.business.web.service.impl;

import com.blog.business.web.mapper.CollectMapper;
import com.blog.business.web.service.CollectService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class CollectServiceImpl implements CollectService {

    @Resource
    private CollectMapper collectMapper;

}
