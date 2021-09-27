package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.LinkVO;
import com.blog.business.web.domain.Link;
import com.blog.exception.ResultBody;

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

    /**
     * 获取友链列表
     *
     * @param linkVO 查询条件
     * @return 获取友链列表
     * @author yujunhong
     * @date 2021/9/27 15:02
     */
    IPage<Link> getLinkList(LinkVO linkVO);

    /**
     * 增加友链
     *
     * @param linkVO 增加友链实体
     * @return 增加友链
     * @author yujunhong
     * @date 2021/9/27 15:16
     */
    ResultBody add(LinkVO linkVO);

    /**
     * 编辑友链
     *
     * @param linkVO 编辑友链实体
     * @return 编辑友链
     * @author yujunhong
     * @date 2021/9/27 15:16
     */
    ResultBody edit(LinkVO linkVO);

    /**
     * 删除友链
     *
     * @param linkVO 删除友链实体
     * @return 删除友链
     * @author yujunhong
     * @date 2021/9/27 15:16
     */
    ResultBody delete(LinkVO linkVO);

    /**
     * 置顶友链
     *
     * @param linkVO 置顶友链实体
     * @return 置顶友链
     * @author yujunhong
     * @date 2021/9/27 15:16
     */
    ResultBody stick(LinkVO linkVO);
}
