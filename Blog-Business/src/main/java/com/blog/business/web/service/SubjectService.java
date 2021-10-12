package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.vo.SubjectVO;
import com.blog.exception.ResultBody;

import java.util.List;

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


    /**
     * 增加专题
     *
     * @param subjectVO 实体对象
     * @return 增加专题
     * @author yujunhong
     * @date 2021/10/12 14:59
     */
    ResultBody add(SubjectVO subjectVO);

    /**
     * 编辑专题
     *
     * @param subjectVO 实体对象
     * @return 编辑专题
     * @author yujunhong
     * @date 2021/10/12 14:59
     */
    ResultBody edit(SubjectVO subjectVO);

    /**
     * 批量删除专题
     *
     * @param subjectVOList 删除实体集合
     * @return 批量删除专题
     * @author yujunhong
     * @date 2021/10/12 15:11
     */
    ResultBody deleteBatch(List<SubjectVO> subjectVOList);
}
