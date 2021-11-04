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
import com.blog.constants.BaseMessageConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
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

    @Override
    public ResultBody add(List<SubjectItemVO> subjectItemVOList) {
        // 获取博客id集合
        List<String> blogUidList = new ArrayList<>();
        // 获取主题id
        String subjectUid = "";
        for (SubjectItemVO subjectItemVO : subjectItemVOList) {
            blogUidList.add(subjectItemVO.getBlogUid());
            if (StringUtils.isEmpty(subjectUid) && StringUtils.isNotEmpty(subjectItemVO.getSubjectUid())) {
                subjectUid = subjectItemVO.getSubjectUid();
            }
        }
        // 查询item中是否包含重复的博客
        LambdaQueryWrapper<SubjectItem> subjectItemWrapper = new LambdaQueryWrapper<>();
        subjectItemWrapper.eq(SubjectItem::getSubjectUid, subjectUid);
        subjectItemWrapper.in(SubjectItem::getBlogUid, blogUidList);
        List<SubjectItem> subjectItems = this.list(subjectItemWrapper);
        // 找出重复的博客UID
        List<String> repeatBlogList =
                subjectItems.stream().map(SubjectItem::getBlogUid).collect(Collectors.toList());
        List<SubjectItem> subjectItemList = new ArrayList<>();
        for (SubjectItemVO subjectItemVO : subjectItemVOList) {
            if (StringUtils.isEmpty(subjectItemVO.getSubjectUid()) || StringUtils.isEmpty(subjectItemVO.getBlogUid())) {
                return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
            }
            // 判断是否重复添加
            if (repeatBlogList.contains(subjectItemVO.getBlogUid())) {
                continue;
            }
            SubjectItem subjectItem = new SubjectItem();
            subjectItem.setSubjectUid(subjectItemVO.getSubjectUid());
            subjectItem.setBlogUid(subjectItemVO.getBlogUid());
            subjectItem.setStatus(EnumsStatus.ENABLE);
            subjectItemList.add(subjectItem);
        }
        if (subjectItemList.size() <= 0) {
            if (repeatBlogList.size() == 0) {
                return ResultBody.error(BaseMessageConf.INSERT_FAIL);
            } else {
                return ResultBody.error(BaseMessageConf.INSERT_FAIL + "，已跳过" + repeatBlogList.size() + "个重复数据");
            }
        } else {
            this.saveBatch(subjectItemList);
            if (repeatBlogList.size() == 0) {
                return ResultBody.success(BaseMessageConf.INSERT_SUCCESS);
            } else {
                return ResultBody.success(BaseMessageConf.INSERT_SUCCESS + "，已跳过" + repeatBlogList.size() +
                        "个重复数据，成功插入" + (subjectItemVOList.size() - repeatBlogList.size()) + "条数据");
            }
        }
    }

    @Override
    public ResultBody edit(List<SubjectItemVO> subjectItemVOList) {
        // 获取所有的item uid
        List<String> subjectItemUidList =
                subjectItemVOList.stream().map(SubjectItemVO::getUid).collect(Collectors.toList());
        List<SubjectItem> subjectItemUpdateList = new ArrayList<>();
        if (subjectItemUidList.size() > 0) {
            subjectItemUpdateList = this.listByIds(subjectItemUidList);
            if (subjectItemUpdateList.size() > 0) {
                HashMap<String, SubjectItemVO> subjectItemVoHashMap = new HashMap<>();
                subjectItemVOList.forEach(item -> {
                    subjectItemVoHashMap.put(item.getUid(), item);
                });
                // 修改排序字段
                List<SubjectItem> subjectItemList = new ArrayList<>();
                subjectItemUpdateList.forEach(item -> {
                    SubjectItemVO subjectItemVO = subjectItemVoHashMap.get(item.getUid());
                    item.setSubjectUid(subjectItemVO.getSubjectUid());
                    item.setBlogUid(subjectItemVO.getBlogUid());
                    item.setStatus(EnumsStatus.ENABLE);
                    item.setSort(subjectItemVO.getSort());
                    item.setUpdateTime(new Date());
                    subjectItemList.add(item);
                });
                this.updateBatchById(subjectItemList);
            }
        }
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<SubjectItemVO> subjectItemVOList) {
        // 获取所有的item uid
        List<String> subjectItemUidList =
                subjectItemVOList.stream().map(SubjectItemVO::getUid).collect(Collectors.toList());
        this.removeByIds(subjectItemUidList);
        return ResultBody.success();
    }

    @Override
    public ResultBody sortByCreateTime(String subjectUid, Boolean isDesc) {
        // 查询当前主题下的item
        LambdaQueryWrapper<SubjectItem> subjectItemWrapper = new LambdaQueryWrapper<>();
        subjectItemWrapper.eq(SubjectItem::getStatus, EnumsStatus.ENABLE);
        subjectItemWrapper.eq(SubjectItem::getSubjectUid, subjectUid);
        List<SubjectItem> subjectItemList = this.list(subjectItemWrapper);
        // 获取专题中的博客信息
        List<String> blogUidList = subjectItemList.stream().map(SubjectItem::getBlogUid).collect(Collectors.toList());
        if (blogUidList.isEmpty()) {
            return ResultBody.error(BaseMessageConf.UPDATE_DEFAULT_ERROR);
        }
        // 获取博客信息
        List<Blog> blogList = blogService.listByIds(blogUidList);
        // 获取排序后新的顺序
        List<Blog> tempBlogList;
        if (isDesc) {
            tempBlogList =
                    blogList.stream().sorted(Comparator.comparing(Blog::getCreateTime).reversed()).collect(Collectors.toList());
        } else {
            tempBlogList =
                    blogList.stream().sorted(Comparator.comparing(Blog::getCreateTime)).collect(Collectors.toList());
        }
        // 设置初始化最大的sort值
        int maxSort = tempBlogList.size();
        Map<String, Integer> subjectItemSortMap = new HashMap<>();
        for (Blog item : tempBlogList) {
            subjectItemSortMap.put(item.getUid(), maxSort--);
        }
        // 设置更新后的排序值
        for (SubjectItem item : subjectItemList) {
            item.setSort(subjectItemSortMap.get(item.getBlogUid()));
        }
        this.updateBatchById(subjectItemList);
        return ResultBody.success();
    }

    @Override
    public void deleteBatchSubjectItemByBlogUid(List<String> blogUid) {
        LambdaQueryWrapper<SubjectItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SubjectItem::getBlogUid, blogUid);
        List<SubjectItem> subjectItems = this.list(wrapper);
        subjectItems.forEach(item -> item.setStatus(EnumsStatus.DISABLED));
        this.updateBatchById(subjectItems);
    }
}
