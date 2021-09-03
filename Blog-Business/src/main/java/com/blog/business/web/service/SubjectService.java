package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.vo.SubjectVO;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface SubjectService extends IService<Subject> {

    /**
     * 获取专题列表
     *
     * @param subjectVO 查询条件
     * @return 专题列表
     * @author yujunhong
     * @date 2021/9/3 17:04
     */
     IPage<Subject> getList(SubjectVO subjectVO);
}
