package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.web.domain.PictureSort;
import com.blog.business.web.service.PictureSortService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/9/24 11:18
 */
@RestController
@RequestMapping(value = "/pictureSort")
@Api(value = "图片分类相关接口", tags = {"图片分类相关接口"})
public class PictureSortController {

    @Autowired
    private PictureSortService pictureSortService;

    /**
     * 获取图片分类列表
     *
     * @param pictureSortVO 查询条件
     * @return 图片分类列表
     * @author yujunhong
     * @date 2021/9/24 11:20
     */
    @ApiOperation(value = "获取图片分类列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody PictureSortVO pictureSortVO) {
        IPage<PictureSort> pageList =
                pictureSortService.getPageList(pictureSortVO);
        return ResultBody.success(pageList);
    }
}
