package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.TagVO;
import com.blog.business.web.domain.Tag;
import com.blog.exception.ResultBody;

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

    /**
     * 获取标签列表
     *
     * @param tagVO 查询条件
     * @return 标签列表
     * @author yujunhong
     * @date 2021/9/23 15:54
     */
    IPage<Tag> getPageList(TagVO tagVO);


    /**
     * 增加标签
     *
     * @param tagVO 新增实体对象
     * @return 增加标签
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody add(TagVO tagVO);

    /**
     * 编辑标签
     *
     * @param tagVO 编辑标签实体对象
     * @return 增加标签
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody edit(TagVO tagVO);

    /**
     * 批量删除标签
     *
     * @param tagVoList 删除集合
     * @return 批量删除标签
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody deleteBatch(List<TagVO> tagVoList);

    /**
     * 置顶标签
     *
     * @param tagVO 置顶实体对象
     * @return 置顶标签
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody stick(TagVO tagVO);

    /**
     * 通过点击量排序标签
     *
     * @return 置顶标签
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody tagSortByClickCount();

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 16:01
     */
    ResultBody tagSortByCite();
}
