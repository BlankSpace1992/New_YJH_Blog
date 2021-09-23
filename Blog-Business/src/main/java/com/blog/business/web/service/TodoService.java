package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.TodoVO;
import com.blog.business.web.domain.Todo;
import com.blog.exception.ResultBody;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface TodoService extends IService<Todo> {

    /**
     * 获取代办事项列表
     *
     * @param todoVO 查询条件vo
     * @return 代办事项列表
     * @author yujunhong
     * @date 2021/9/23 14:34
     */
    IPage<Todo> getList(TodoVO todoVO);

    /**
     * 增加代办事项
     *
     * @param todoVO 增加事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:34
     */
    ResultBody add(TodoVO todoVO);

    /**
     * 编辑代办事项
     *
     * @param todoVO 编辑代办事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:34
     */
    ResultBody edit(TodoVO todoVO);

    /**
     * 删除代办事项
     *
     * @param todoVO 删除代办事项实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/23 14:34
     */
    ResultBody delete(TodoVO todoVO);
}
