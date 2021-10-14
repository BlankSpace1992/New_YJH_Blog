package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.BlogSortVO;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.BlogSort;
import com.blog.business.web.mapper.BlogSortMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.BlogSortService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class BlogSortServiceImpl extends ServiceImpl<BlogSortMapper, BlogSort> implements BlogSortService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private BlogService blogService;

    @Override
    public List<BlogSort> getBlogListByClassify() {
        LambdaQueryWrapper<BlogSort> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        blogWrapper.orderByDesc(BlogSort::getSort);
        return this.list(blogWrapper);
    }

    @Override
    public IPage<BlogSort> getList(BlogSortVO blogSortVO) {
        // 注入参数
        IPage<BlogSort> page = new Page<>();
        page.setSize(blogSortVO.getPageSize());
        page.setCurrent(blogSortVO.getCurrentPage());
        return baseMapper.getList(page, blogSortVO);
    }

    @Override
    public ResultBody add(BlogSortVO blogSortVO) {
        // 判断当前分类名称是否已经存在
        LambdaQueryWrapper<BlogSort> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BlogSort::getSortName, blogSortVO.getSortName());
        lambdaQueryWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        // 新增实体
        BlogSort blogSort = new BlogSort();
        blogSort.setContent(blogSortVO.getContent());
        blogSort.setSortName(blogSortVO.getSortName());
        blogSort.setSort(blogSortVO.getSort());
        blogSort.setStatus(EnumsStatus.ENABLE);
        this.save(blogSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(BlogSortVO blogSortVO) {
        // 根据uid获取当前分类
        BlogSort blogSort = this.getById(blogSortVO.getUid());
        // 判断是否已不存在
        if (StringUtils.isNull(blogSort)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        if (!blogSortVO.getSortName().equals(blogSort.getSortName())) {
            // 判断当前分类名称是否已经存在
            LambdaQueryWrapper<BlogSort> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BlogSort::getSortName, blogSortVO.getSortName());
            lambdaQueryWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
            int count = this.count(lambdaQueryWrapper);
            if (count > 0) {
                return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
            }
        }
        blogSort.setContent(blogSortVO.getContent());
        blogSort.setSortName(blogSortVO.getSortName());
        blogSort.setSort(blogSortVO.getSort());
        blogSort.setStatus(EnumsStatus.ENABLE);
        blogSort.setUpdateTime(new Date());
        this.updateById(blogSort);
        // 删除相关的缓存已经博客信息
        deleteRedisByBlogSort();
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<BlogSortVO> blogSortVoList) {
        if (blogSortVoList.size() <= 0) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        // 获取所有的分类uid
        List<String> uidList = new ArrayList<>();
        blogSortVoList.forEach(item -> {
            uidList.add(item.getUid());
        });
        // 判断要删除的分类，是否有博客
        LambdaQueryWrapper<Blog> blogQuery = new LambdaQueryWrapper<>();
        blogQuery.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogQuery.in(Blog::getBlogSortUid, uidList);
        int count = blogService.count(blogQuery);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.BLOG_UNDER_THIS_SORT);
        }
        // 获取对应的分类信息
        List<BlogSort> blogSortList = this.listByIds(uidList);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EnumsStatus.DISABLED);
        });
        this.updateBatchById(blogSortList);
        deleteRedisByBlogSort();
        return ResultBody.success();
    }

    @Override
    public ResultBody stick(BlogSortVO blogSortVO) {
        // 获取分类信息
        BlogSort blogSort = this.getById(blogSortVO.getUid());
        if (StringUtils.isNull(blogSort)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        //查找出最大的那一个
        LambdaQueryWrapper<BlogSort> blogSortWrapper = new LambdaQueryWrapper<>();
        blogSortWrapper.orderByDesc(BlogSort::getSort);
        blogSortWrapper.last(BaseSysConf.LIMIT_ONE);
        BlogSort maxBlogSort = this.getOne(blogSortWrapper);
        if (StringUtils.isEmpty(maxBlogSort.getUid())) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        if (maxBlogSort.getUid().equals(blogSort.getUid())) {
            return ResultBody.error(BaseMessageConf.THIS_SORT_IS_TOP);
        }
        Integer sortCount = maxBlogSort.getSort() + 1;
        blogSort.setSort(sortCount);
        blogSort.setUpdateTime(new Date());
        this.updateById(blogSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody blogSortByClickCount() {
        LambdaQueryWrapper<BlogSort> blogSortWrapper = new LambdaQueryWrapper<>();
        blogSortWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        blogSortWrapper.orderByDesc(BlogSort::getClickCount);
        List<BlogSort> blogSortList = this.list(blogSortWrapper);
        // 设置初始化最大的sort值
        Integer maxSort = blogSortList.size();
        for (BlogSort item : blogSortList) {
            item.setSort(item.getClickCount());
            item.setUpdateTime(new Date());
        }
        this.updateBatchById(blogSortList);
        return ResultBody.success();
    }

    @Override
    public ResultBody blogSortByCite() {
        // 定义Map   key：tagUid,  value: 引用量
        Map<String, Integer> map = new HashMap<>();
        // 查询数据
        LambdaQueryWrapper<BlogSort> blogSortWrapper = new LambdaQueryWrapper<>();
        blogSortWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        List<BlogSort> blogSortList = this.list(blogSortWrapper);
        // 初始化所有标签的引用量
        blogSortList.forEach(item -> {
            map.put(item.getUid(), 0);
        });
        // 查询所有博客信息
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 过滤content字段
        blogWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> blogList = blogService.list(blogWrapper);
        blogList.forEach(item -> {
            String blogSortUid = item.getBlogSortUid();
            if (map.get(blogSortUid) != null) {
                Integer count = map.get(blogSortUid) + 1;
                map.put(blogSortUid, count);
            } else {
                map.put(blogSortUid, 0);
            }
        });
        blogSortList.forEach(item -> {
            item.setSort(map.get(item.getUid()));
            item.setUpdateTime(new Date());
        });
        this.updateBatchById(blogSortList);
        return ResultBody.success();
    }

    /**
     * 删除相关的缓存已经博客信息
     *
     * @author yujunhong
     * @date 2021/9/23 11:27
     */
    private void deleteRedisByBlogSort() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_SORT);
        // 删除博客相关缓存
        redisUtil.delete(BaseRedisConf.NEW_BLOG);
        redisUtil.delete(BaseRedisConf.HOT_BLOG);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.FIRST);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.SECOND);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.THIRD);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.FOURTH);
    }
}
