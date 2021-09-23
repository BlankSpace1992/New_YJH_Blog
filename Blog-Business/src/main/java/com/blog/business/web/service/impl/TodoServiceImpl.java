package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.TodoVO;
import com.blog.business.web.domain.Todo;
import com.blog.business.web.mapper.TodoMapper;
import com.blog.business.web.service.TodoService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {


    @Override
    public IPage<Todo> getList(TodoVO todoVO) {
        // 获取用户uid
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("获取待办事项失败"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        }
        // 注入分页参数
        IPage<Todo> page = new Page<>();
        page.setSize(todoVO.getPageSize());
        page.setCurrent(todoVO.getCurrentPage());
        // 查询数据
        return baseMapper.getList(page, todoVO, adminUid);
    }

    @Override
    public ResultBody add(TodoVO todoVO) {
        // 获取用户uid
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("新增待办事项失败"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        }
        Todo todo = new Todo();
        todo.setText(todoVO.getText());
        //默认未完成
        todo.setDone(false);
        todo.setAdminUid(adminUid);
        this.save(todo);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(TodoVO todoVO) {
        // 获取用户uid
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("编辑待办事项失败"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        }
        Todo todo = this.getById(todoVO.getUid());
        if (StringUtils.isNull(todo)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }else {
            if (!todo.getAdminUid().equals(adminUid)) {
                return ResultBody.error(BaseMessageConf.ACCESS_NO_PRIVILEGE);
            }
        }
        todo.setText(todoVO.getText());
        todo.setDone(todoVO.getDone());
        todo.setUpdateTime(new Date());
        this.updateById(todo);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(TodoVO todoVO) {
        // 获取用户uid
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("删除待办事项失败"));
        String adminUid = StringUtils.EMPTY;
        if (StringUtils.isNotNull(request.getAttribute(BaseSysConf.ADMIN_UID))) {
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        }
        Todo todo = this.getById(todoVO.getUid());
        if (StringUtils.isNull(todo)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }else {
            if (!todo.getAdminUid().equals(adminUid)) {
                return ResultBody.error(BaseMessageConf.ACCESS_NO_PRIVILEGE);
            }
        }
        todo.setStatus(EnumsStatus.DISABLED);
        todo.setUpdateTime(new Date());
        this.updateById(todo);
        return ResultBody.success();
    }
}
