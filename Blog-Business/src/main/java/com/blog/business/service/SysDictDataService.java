package com.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.domain.SysDictData;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 根据字典类型数组获取字典数据
     *
     * @param dictTypeList 字典类型
     * @return 字典数据
     * @author yujunhong
     * @date 2021/6/2 16:16
     */
    Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);
}
