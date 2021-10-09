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

    /**
     * 增加图片分类
     *
     * @param pictureSortVO 增加图片分类实体
     * @return 增加图片分类
     * @author yujunhong
     * @date 2021/10/8 16:22
     */
    @ApiOperation(value = "增加图片分类")
    @PostMapping("/add")
    public ResultBody add(@RequestBody PictureSortVO pictureSortVO) {
        return pictureSortService.add(pictureSortVO);
    }

    /**
     * 编辑图片分类
     *
     * @param pictureSortVO 编辑图片分类实体
     * @return 编辑图片分类
     * @author yujunhong
     * @date 2021/10/8 16:22
     */
    @ApiOperation(value = "编辑图片分类")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody PictureSortVO pictureSortVO) {
        return pictureSortService.edit(pictureSortVO);
    }

    /**
     * 删除图片分类
     *
     * @param pictureSortVO 删除图片分类实体
     * @return 删除图片分类
     * @author yujunhong
     * @date 2021/10/8 16:22
     */
    @ApiOperation(value = "删除图片分类")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody PictureSortVO pictureSortVO) {
        return pictureSortService.delete(pictureSortVO);
    }

    /**
     * 置顶分类
     *
     * @param pictureSortVO 置顶分类实体
     * @return 置顶分类
     * @author yujunhong
     * @date 2021/10/8 16:22
     */
    @ApiOperation(value = "置顶分类")
    @PostMapping("/stick")
    public ResultBody stick(@RequestBody PictureSortVO pictureSortVO) {
        return pictureSortService.stick(pictureSortVO);
    }

    /**
     * 通过Uid获取分类
     *
     * @param pictureSortVO 通过Uid获取分类实体
     * @return 通过Uid获取分类
     * @author yujunhong
     * @date 2021/10/8 16:22
     */
    @ApiOperation(value = "通过Uid获取分类")
    @PostMapping("/getPictureSortByUid")
    public ResultBody getPictureSortByUid(@RequestBody PictureSortVO pictureSortVO) {
        return pictureSortService.getPictureSortByUid(pictureSortVO);
    }


}
