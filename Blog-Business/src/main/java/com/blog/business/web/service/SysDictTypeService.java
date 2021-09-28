package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;
import com.blog.exception.ResultBody;

import java.util.List;

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


    /**
     * 增加字典类型
     *
     * @param sysDictTypeVO 增加字典类型实体
     * @return 增加字典类型
     * @author yujunhong
     * @date 2021/9/28 10:52
     */
    ResultBody add(SysDictTypeVO sysDictTypeVO);

    /**
     * 编辑字典类型
     *
     * @param sysDictTypeVO 编辑字典类型实体
     * @return 编辑字典类型
     * @author yujunhong
     * @date 2021/9/28 10:52
     */
    ResultBody edit(SysDictTypeVO sysDictTypeVO);


    /**
     * 批量删除字典类型
     *
     * @param sysDictTypeVoList 批量删除字典类型实体集合
     * @return 批量删除字典类型
     * @author yujunhong
     * @date 2021/9/28 10:52
     */
    ResultBody deleteBatch(List<SysDictTypeVO> sysDictTypeVoList);
}
