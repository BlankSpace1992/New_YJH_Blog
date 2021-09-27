package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeVO 查询条件
     * @return 获取字典类型列表
     * @author yujunhong
     * @date 2021/9/27 16:21
     */
    IPage<SysDictType> getPageList(SysDictTypeVO sysDictTypeVO);
}
