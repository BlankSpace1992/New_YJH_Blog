package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Tag;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface TagMapper extends BaseMapper<Tag> {


    /**
     * 获取最热标签
     *
     * @param page   分页参数
     * @param status 状态
     * @return 获取最热标签
     * @author yujunhong
     * @date 2021/6/1 16:25
     */
    IPage<Tag> getHotTag(IPage<Tag> page,
                         @Param(value = "status") Integer status);
}
