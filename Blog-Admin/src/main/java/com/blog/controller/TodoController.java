package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.TodoVO;
import com.blog.business.web.domain.Todo;
import com.blog.business.web.service.TodoService;
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
 * @date 2021/9/23 14:28
 */
@RestController
@RequestMapping(value = "/todo")
@Api(value = "待办事项相关接口", tags = {"待办事项相关接口"})
public class TodoController {
    @Autowired
    private TodoService todoService;

    /**
     * 获取代办事项列表
     *
     * @param todoVO 查询条件vo
     * @return 代办事项列表
     * @author yujunhong
     * @date 2021/9/23 14:31
     */
    @ApiOperation(value = "获取代办事项列表")
    @PostMapping(value = "/getList")
    public ResultBody getList(@RequestBody TodoVO todoVO) {
        IPage<Todo> list =
                todoService.getList(todoVO);
        return ResultBody.success(list);
    }

    /**
     * 增加代办事项
     *
     * @param todoVO 增加事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:43
     */
    @ApiOperation(value = "增加代办事项")
    @PostMapping(value = "/add")
    public ResultBody add(@RequestBody TodoVO todoVO) {
        return todoService.add(todoVO);
    }

    /**
     * 编辑代办事项
     *
     * @param todoVO 编辑代办事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:43
     */
    @ApiOperation(value = "编辑代办事项")
    @PostMapping(value = "/edit")
    public ResultBody edit(@RequestBody TodoVO todoVO) {
        return todoService.edit(todoVO);
    }

    /**
     * 删除代办事项
     *
     * @param todoVO 删除代办事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:43
     */
    @ApiOperation(value = "删除代办事项")
    @PostMapping(value = "/delete")
    public ResultBody delete(@RequestBody TodoVO todoVO) {
        return todoService.delete(todoVO);
    }
}
