package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.web.domain.SubjectItem;
import com.blog.business.web.domain.vo.SubjectItemVO;
import com.blog.business.web.service.SubjectItemService;
import com.blog.constants.BaseMessageConf;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/10/15 16:42
 */
@Api(value = "专题Item相关接口", tags = {"专题Item相关接口"})
@RestController
@RequestMapping("/subjectItem")
public class SubjectItemController {
    @Autowired
    private SubjectItemService subjectItemService;

    /**
     * 获取专题Item列表
     *
     * @param subjectItemVO 查询条件实体
     * @return 获取专题Item列表
     * @author yujunhong
     * @date 2021/10/15 16:43
     */
    @ApiOperation(value = "获取专题Item列表")
    @PostMapping("/getList")
    public ResultBody getList(@RequestBody SubjectItemVO subjectItemVO) {
        IPage<SubjectItem> list = subjectItemService.getList(subjectItemVO);
        return ResultBody.success(list);
    }

    /**
     * 增加专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 增加专题Item
     * @author yujunhong
     * @date 2021/10/15 16:46
     */
    @ApiOperation(value = "增加专题Item")
    @PostMapping("/add")
    public ResultBody add(@RequestBody List<SubjectItemVO> subjectItemVOList) {
        return subjectItemService.add(subjectItemVOList);
    }

    /**
     * 编辑专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 编辑专题Item
     * @author yujunhong
     * @date 2021/10/15 16:46
     */
    @ApiOperation(value = "编辑专题Item")
    @PostMapping("/edit")
    public ResultBody edit(@RequestBody List<SubjectItemVO> subjectItemVOList) {
        return subjectItemService.edit(subjectItemVOList);
    }

    /**
     * 批量删除专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 批量删除专题Item
     * @author yujunhong
     * @date 2021/10/15 16:46
     */
    @ApiOperation(value = "批量删除专题Item")
    @PostMapping("/deleteBatch")
    public ResultBody deleteBatch(@RequestBody List<SubjectItemVO> subjectItemVOList) {
        if (StringUtils.isEmpty(subjectItemVOList)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return subjectItemService.deleteBatch(subjectItemVOList);
    }

    /**
     * 通过创建时间排序专题列表
     *
     * @param subjectUid 专题uid
     * @param isDesc     是否从大到小排列
     * @return 通过创建时间排序专题列表
     * @author yujunhong
     * @date 2021/10/18 11:04
     */
    @ApiOperation(value = "通过创建时间排序专题列表")
    @PostMapping("/sortByCreateTime")
    public ResultBody sortByCreateTime(@ApiParam(name = "subjectUid", value = "专题uid") @RequestParam(name =
            "subjectUid", required = true) String subjectUid,
                                       @ApiParam(name = "isDesc", value = "是否从大到小排列") @RequestParam(name = "isDesc",
                                               required = false, defaultValue = "false") Boolean isDesc) {
        return subjectItemService.sortByCreateTime(subjectUid, isDesc);
    }
}
