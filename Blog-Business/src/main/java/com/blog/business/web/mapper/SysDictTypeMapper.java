package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeVO 查询条件
     * @param page          分页参数
     * @return 获取字典类型列表
     * @author yujunhong
     * @date 2021/9/27 16:21
     */
    IPage<SysDictType> getPageList(@Param("page") IPage<SysDictType> page, @Param("sysDictTypeVO") SysDictTypeVO sysDictTypeVO);
}
