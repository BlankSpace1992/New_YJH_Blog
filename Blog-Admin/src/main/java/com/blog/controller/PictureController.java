package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;
import com.blog.business.web.service.PictureService;
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
 * @date 2021/9/24 11:51
 */
@RestController
@RequestMapping("/picture")
@Api(value = "图片相关接口", tags = {"图片相关接口"})
public class PictureController {

    @Autowired
    private PictureService pictureService;

    /**
     * 获取图片列表
     *
     * @param pictureVO 查询条件
     * @return 图片列表
     * @author yujunhong
     * @date 2021/9/24 11:53
     */
    @ApiOperation(value = "获取图片列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody PictureVO pictureVO) {
        IPage<Picture> pageList =
                pictureService.getPageList(pictureVO);
        return ResultBody.success(pageList);
    }
}
