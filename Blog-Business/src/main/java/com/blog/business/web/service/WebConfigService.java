package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.WebConfig;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebConfigService extends IService<WebConfig> {

    /**
     * 通过显示列表获取配置信息
     *
     * @return 配置信息
     * @author yujunhong
     * @date 2021/6/2 13:58
     */
    WebConfig getWebConfigByShowList();
}
