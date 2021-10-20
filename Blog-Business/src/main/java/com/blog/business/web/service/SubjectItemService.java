package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.SubjectItem;
import com.blog.business.web.domain.vo.SubjectItemVO;
import com.blog.exception.ResultBody;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
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

    /**
     * 增加专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 增加专题Item
     * @author yujunhong
     * @date 2021/10/15 16:47
     */
    ResultBody add(List<SubjectItemVO> subjectItemVOList);

    /**
     * 编辑专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 编辑专题Item
     * @author yujunhong
     * @date 2021/10/15 16:47
     */
    ResultBody edit(List<SubjectItemVO> subjectItemVOList);


    /**
     * 批量删除专题Item
     *
     * @param subjectItemVOList 主题item集合
     * @return 批量删除专题Item
     * @author yujunhong
     * @date 2021/10/15 16:47
     */
    ResultBody deleteBatch(List<SubjectItemVO> subjectItemVOList);

    /**
     * 通过创建时间排序专题列表
     *
     * @param subjectUid 专题uid
     * @param isDesc     是否从大到小排列
     * @return 通过创建时间排序专题列表
     * @author yujunhong
     * @date 2021/10/18 11:05
     */
    ResultBody sortByCreateTime(String subjectUid, Boolean isDesc);
}
