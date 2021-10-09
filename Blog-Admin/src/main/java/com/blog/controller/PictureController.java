package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;
import com.blog.business.web.service.PictureService;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 增加图片
     *
     * @param pictureVOList 增加图片集合
     * @return 增加图片
     * @author yujunhong
     * @date 2021/10/8 16:38
     */
    @ApiOperation(value = "增加图片")
    @PostMapping("/add")
    public ResultBody add(@RequestBody List<PictureVO> pictureVOList) {
        if (StringUtils.isEmpty(pictureVOList)) {
            return ResultBody.error("请选择图片");
        }
        return pictureService.add(pictureVOList);
    }

    /**
     * 编辑图片
     *
     * @param pictureVO 编辑图片实体
     * @return 编辑图片
     * @author yujunhong
     * @date 2021/10/8 16:49
     */
    @ApiOperation(value = "编辑图片")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody PictureVO pictureVO) {
        return pictureService.edit(pictureVO);
    }

    /**
     * 删除图片
     *
     * @param pictureVO 删除图片实体
     * @return 删除图片
     * @author yujunhong
     * @date 2021/10/8 16:49
     */
    @ApiOperation(value = "删除图片")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody PictureVO pictureVO) {
        return pictureService.delete(pictureVO);
    }

    /**
     * 通过图片Uid将图片设为封面
     *
     * @param pictureVO 通过图片Uid将图片设为封面实体
     * @return 通过图片Uid将图片设为封面
     * @author yujunhong
     * @date 2021/10/8 16:49
     */
    @ApiOperation(value = "通过图片Uid将图片设为封面")
    @PostMapping("/setCover")
    public ResultBody setCover(@RequestBody PictureVO pictureVO) {
        return pictureService.setCover(pictureVO);
    }
}
