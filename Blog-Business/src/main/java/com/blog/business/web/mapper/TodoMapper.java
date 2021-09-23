package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.TodoVO;
import com.blog.business.web.domain.Todo;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface TodoMapper extends BaseMapper<Todo> {

    /**
     * 获取代办事项列表
     *
     * @param todoVO   查询条件vo
     * @param page     分页参数
     * @param adminUid 用户uid
     * @return 代办事项列表
     * @author yujunhong
     * @date 2021/9/23 14:34
     */
    IPage<Todo> getList(@Param("page") IPage<Todo> page, @Param("todoVO") TodoVO todoVO, @Param("adminUid") String adminUid);
}
