package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.web.domain.PictureSort;
import com.blog.exception.ResultBody;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface PictureSortService extends IService<PictureSort> {

    /**
     * 获取图片分类列表
     *
     * @param pictureSortVO 查询条件
     * @return 图片分类列表
     * @author yujunhong
     * @date 2021/9/24 11:22
     */
    IPage<PictureSort> getPageList(PictureSortVO pictureSortVO);

    /**
     * 增加图片分类
     *
     * @param pictureSortVO 增加图片分类实体
     * @return 增加图片分类
     * @author yujunhong
     * @date 2021/10/8 16:23
     */
    ResultBody add(PictureSortVO pictureSortVO);

    /**
     * 编辑图片分类
     *
     * @param pictureSortVO 编辑图片分类实体
     * @return 编辑图片分类
     * @author yujunhong
     * @date 2021/10/8 16:23
     */
    ResultBody edit(PictureSortVO pictureSortVO);

    /**
     * 删除图片分类
     *
     * @param pictureSortVO 删除图片分类实体
     * @return 删除图片分类
     * @author yujunhong
     * @date 2021/10/8 16:23
     */
    ResultBody delete(PictureSortVO pictureSortVO);

    /**
     * 置顶分类
     *
     * @param pictureSortVO 置顶分类实体
     * @return 置顶分类
     * @author yujunhong
     * @date 2021/10/8 16:23
     */
    ResultBody stick(PictureSortVO pictureSortVO);

    /**
     * 通过Uid获取分类
     *
     * @param pictureSortVO 通过Uid获取分类实体
     * @return 通过Uid获取分类
     * @author yujunhong
     * @date 2021/10/8 16:23
     */
    ResultBody getPictureSortByUid(PictureSortVO pictureSortVO);
}
