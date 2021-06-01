package com.blog.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.domain.Link;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface LinkService extends IService<Link> {

    /**
     * 获取友情连接
     *
     * @return 获取友情连接
     * @author yujunhong
     * @date 2021/6/1 16:39
     */
    IPage<Link> getLink();

    /**
     * 增加友情连接点击数
     *
     * @param uid 友情链接Id
     * @author yujunhong
     * @date 2021/6/1 17:04
     */
    void addLinkCount(String uid);
}
