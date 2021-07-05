package com.blog.business.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 获取配置信息
     *
     * @return 获取配置信息
     * @author yujunhong
     * @date 2021/6/3 14:26
     */
    SystemConfig getsSystemConfig();
}
