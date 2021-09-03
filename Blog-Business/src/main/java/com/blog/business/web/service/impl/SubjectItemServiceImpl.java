package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.SubjectItem;
import com.blog.business.web.domain.vo.SubjectItemVO;
import com.blog.business.web.mapper.SubjectItemMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.SubjectItemService;
import com.blog.constants.EnumsStatus;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class SubjectItemServiceImpl extends ServiceImpl<SubjectItemMapper, SubjectItem> implements SubjectItemService {
    @Autowired
    private BlogService blogService;

    @Override
    public IPage<SubjectItem> getList(SubjectItemVO subjectItemVO) {
        LambdaQueryWrapper<SubjectItem> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(subjectItemVO.getSubjectUid())) {
            queryWrapper.eq(SubjectItem::getSubjectUid, subjectItemVO.getSubjectUid());
        }
        queryWrapper.eq(SubjectItem::getStatus, EnumsStatus.ENABLE);
        queryWrapper.orderByDesc(SubjectItem::getSort);
        // 注入分页参数
        Page<SubjectItem> page = new Page<>();
        page.setCurrent(subjectItemVO.getCurrentPage());
        page.setSize(subjectItemVO.getPageSize());
        IPage<SubjectItem> pageList = this.page(page, queryWrapper);
        List<SubjectItem> subjectItemList = pageList.getRecords();
        // 获取所有的blogUid
        List<String> blogIdList = subjectItemList.stream().map(SubjectItem::getBlogUid).collect(Collectors.toList());
        List<Blog> blogCollection = null;
        if (blogIdList.size() > 0) {
            blogCollection = blogService.listByIds(blogIdList);
            if (blogCollection.size() > 0) {
                List<Blog> blogTempList = new ArrayList<>(blogCollection);
                blogService.setTagAndSortAndPictureByBlogList(blogTempList);
                Map<String, Blog> blogMap = new HashMap<>();
                blogTempList.forEach(item -> {
                    blogMap.put(item.getUid(), item);
                });
                subjectItemList.forEach(item -> {
                    item.setBlog(blogMap.get(item.getBlogUid()));
                });
                pageList.setRecords(subjectItemList);
            }
        }
        return pageList;
    }
}
