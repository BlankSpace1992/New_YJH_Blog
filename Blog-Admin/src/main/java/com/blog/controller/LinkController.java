package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.LinkVO;
import com.blog.business.web.domain.Link;
import com.blog.business.web.service.LinkService;
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
 * @date 2021/9/27 15:00
 */
@RestController
@Api(value = "友情链接相关接口", tags = {"友情链接相关接口"})
@RequestMapping("/link")
public class LinkController {
    @Autowired
    LinkService linkService;

    /**
     * 获取友链列表
     *
     * @param linkVO 查询条件
     * @return 获取友链列表
     * @author yujunhong
     * @date 2021/9/27 15:01
     */
    @ApiOperation(value = "获取友链列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody LinkVO linkVO) {
        IPage<Link> linkList = linkService.getLinkList(linkVO);
        return ResultBody.success(linkList);
    }

    /**
     * 增加友链
     *
     * @param linkVO 增加友链实体
     * @return 增加友链
     * @author yujunhong
     * @date 2021/9/27 15:14
     */
    @ApiOperation(value = "增加友链")
    @PostMapping("/add")
    public ResultBody add(@RequestBody LinkVO linkVO) {
        return linkService.add(linkVO);
    }

    /**
     * 编辑友链
     *
     * @param linkVO 编辑友链实体
     * @return 编辑友链
     * @author yujunhong
     * @date 2021/9/27 15:14
     */
    @ApiOperation(value = "编辑友链")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody LinkVO linkVO) {
        return linkService.edit(linkVO);
    }

    /**
     * 删除友链
     *
     * @param linkVO 删除友链实体
     * @return 删除友链
     * @author yujunhong
     * @date 2021/9/27 15:14
     */
    @ApiOperation(value = "删除友链")
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody LinkVO linkVO) {
        return linkService.delete(linkVO);
    }

    /**
     * 置顶友链
     *
     * @param linkVO 置顶友链实体
     * @return 置顶友链
     * @author yujunhong
     * @date 2021/9/27 15:14
     */
    @ApiOperation(value = "置顶友链")
    @PostMapping("/stick")
    public ResultBody stick(@RequestBody LinkVO linkVO) {
        return linkService.stick(linkVO);
    }
}
