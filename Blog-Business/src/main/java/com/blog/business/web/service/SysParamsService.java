package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.SysParamsVO;
import com.blog.business.web.domain.SysParams;
import com.blog.exception.ResultBody;

import java.util.List;

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


    /**
     * 获取参数配置列表
     *
     * @param sysParamsVO 查询条件
     * @return 参数配置列表
     * @author yujunhong
     * @date 2021/9/27 14:28
     */
    IPage<SysParams> getSysParamsPageList(SysParamsVO sysParamsVO);

    /**
     * 增加参数配置
     *
     * @param sysParamsVO 增加实体
     * @return 增加参数配置
     * @author yujunhong
     * @date 2021/9/27 14:35
     */
    ResultBody add(SysParamsVO sysParamsVO);


    /**
     * 编辑参数配置
     *
     * @param sysParamsVO 编辑实体
     * @return 编辑参数配置
     * @author yujunhong
     * @date 2021/9/27 14:35
     */
    ResultBody edit(SysParamsVO sysParamsVO);

    /**
     * 批量删除参数配置
     *
     * @param sysParamsVoList 需要删除参数集合
     * @return 批量删除参数配置
     * @author yujunhong
     * @date 2021/9/27 14:44
     */
    ResultBody deleteBatch(List<SysParamsVO> sysParamsVoList);
}
