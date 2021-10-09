package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;
import com.blog.exception.ResultBody;

import java.util.List;

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

    /**
     * 增加图片
     *
     * @param pictureVOList 增加图片集合
     * @return 增加图片
     * @author yujunhong
     * @date 2021/10/8 16:47
     */
    ResultBody add(List<PictureVO> pictureVOList);

    /**
     * 编辑图片
     *
     * @param pictureVO 编辑图片实体
     * @return 编辑图片
     * @author yujunhong
     * @date 2021/10/8 16:47
     */
    ResultBody edit(PictureVO pictureVO);

    /**
     * 删除图片
     *
     * @param pictureVO 删除图片实体
     * @return 删除图片
     * @author yujunhong
     * @date 2021/10/8 16:47
     */
    ResultBody delete(PictureVO pictureVO);

    /**
     * 通过图片Uid将图片设为封面
     *
     * @param pictureVO 通过图片Uid将图片设为封面实体
     * @return 通过图片Uid将图片设为封面
     * @author yujunhong
     * @date 2021/10/8 16:47
     */
    ResultBody setCover(PictureVO pictureVO);
}
