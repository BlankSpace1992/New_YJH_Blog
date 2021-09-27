package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface PictureService extends IService<Picture> {

    /**
     * 获取图片列表
     *
     * @param pictureVO 查询条件
     * @return 图片列表
     * @author yujunhong
     * @date 2021/9/24 11:54
     */
    IPage<Picture> getPageList(PictureVO pictureVO);
}
