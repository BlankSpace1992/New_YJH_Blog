package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.SysParamsVO;
import com.blog.business.web.domain.SysParams;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SysParamsMapper extends BaseMapper<SysParams> {
    /**
     * 获取参数配置列表
     *
     * @param sysParamsVO 查询条件
     * @param page        分页参数
     * @return 参数配置列表
     * @author yujunhong
     * @date 2021/9/27 14:28
     */
    IPage<SysParams> getSysParamsPageList(@Param("page") IPage<SysParams> page,
                                          @Param("sysParamsVO") SysParamsVO sysParamsVO);
}
