package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.web.domain.PictureSort;

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
}
