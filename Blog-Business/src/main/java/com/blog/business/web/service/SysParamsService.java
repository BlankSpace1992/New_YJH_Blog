package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.SysParams;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SysParamsService extends IService<SysParams> {

    /**
     * 根据param_key获取对应的param_vlaue
     *
     * @param paramKey 参数的key
     * @return 对应的param_value
     * @author yujunhong
     * @date 2021/6/1 14:08
     */
    String getSysParamsValueByKey(String paramKey);

    /**
     * 根据param_key获取对应的param实体对象
     *
     * @param paramKey 参数的key
     * @return 对应的param实体对象
     * @author yujunhong
     * @date 2021/6/1 14:08
     */
    SysParams getSysParamsByKey(String paramKey);
}
