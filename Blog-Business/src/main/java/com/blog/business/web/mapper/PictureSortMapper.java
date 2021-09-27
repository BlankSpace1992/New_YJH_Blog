package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.web.domain.PictureSort;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface PictureSortMapper extends BaseMapper<PictureSort> {
    /**
     * 获取图片分类列表
     *
     * @param pictureSortVO 查询条件
     * @param page          分页参数
     * @return 图片分类列表
     * @author yujunhong
     * @date 2021/9/24 11:22
     */
    IPage<PictureSort> getPageList(@Param("page") IPage<PictureSort> page,
                                   @Param("pictureSortVO") PictureSortVO pictureSortVO);
}
