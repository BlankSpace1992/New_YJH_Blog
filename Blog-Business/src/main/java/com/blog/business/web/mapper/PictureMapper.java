package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface PictureMapper extends BaseMapper<Picture> {

    /**
     * 获取图片列表
     *
     * @param pictureVO 查询条件
     * @param page      分页参数
     * @return 图片列表
     * @author yujunhong
     * @date 2021/9/24 11:54
     */
    IPage<Picture> getPageList(@Param("page") IPage<Picture> page, @Param("pictureVO") PictureVO pictureVO);
}
