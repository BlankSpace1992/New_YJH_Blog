package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.Link;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface LinkMapper extends BaseMapper<Link> {

    /**
     * 获取友情连接
     *
     * @param page       分页参数
     * @param status     状态
     * @param linkStatus 友情链接状态
     * @return 获取友情连接
     * @author yujunhong
     * @date 2021/6/1 16:39
     */
    IPage<Link> getLink(IPage<Link> page,
                        @Param(value = "status") Integer status,
                        @Param(value = "linkStatus") String linkStatus);
}
