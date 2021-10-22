package com.blog.business.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.domain.vo.SystemConfigVO;
import com.blog.exception.ResultBody;

import java.util.List;

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
    SystemConfig getSystemConfig();


    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param keyList 键集合
     * @return 通过Key前缀清空Redis缓存
     * @author yujunhong
     * @date 2021/10/22 13:47
     */
    ResultBody cleanRedisByKey(List<String> keyList);

    /**
     * 修改系统配置
     *
     * @param systemConfigVO 编辑实体对象
     * @return 修改系统配置
     * @author yujunhong
     * @date 2021/10/22 13:52
     */
    ResultBody editSystemConfig(SystemConfigVO systemConfigVO);
}
