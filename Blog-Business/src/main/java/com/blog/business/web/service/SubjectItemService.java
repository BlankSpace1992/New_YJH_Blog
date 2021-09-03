package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.SubjectItem;
import com.blog.business.web.domain.vo.SubjectItemVO;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
public interface SubjectItemService extends IService<SubjectItem> {


    /**
     * 获取专题Item列表
     *
     * @param subjectItemVO 查询条件
     * @return 获取专题Item列表
     * @author yujunhong
     * @date 2021/9/3 17:21
     */
    IPage<SubjectItem> getList(SubjectItemVO subjectItemVO);
}
