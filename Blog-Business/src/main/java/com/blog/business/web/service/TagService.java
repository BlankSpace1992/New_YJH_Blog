package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Tag;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取最热标签
     *
     * @return 获取最热标签
     * @author yujunhong
     * @date 2021/6/1 16:25
     */
    IPage<Tag> getHotTag();

    /**
     * 获取最热标签
     *
     * @return 获取最热标签
     * @author yujunhong
     * @date 2021/6/1 16:25
     */
    List<Tag> getList();
}
