package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.Tag;
import com.blog.business.web.mapper.BlogMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.SysParamsService;
import com.blog.business.web.service.TagService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsLevel;
import com.blog.constants.EnumsStatus;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private TagService tagService;

    @Override
    public IPage<Blog> getBlogByLevel(Integer level, Integer currentPage, Integer useSort) {
        // 优先从redis中提取内容
        String redisKey = BaseRedisConf.BLOG_LEVEL + BaseRedisConf.SEGMENTATION + level;
        String result = (String) redisUtil.get(redisKey);
        IPage<Blog> page = new Page<>();
        // 判断redis中是否有数据
        if (StringUtils.isNotEmpty(result)) {
            List<Blog> blogs = JSON.parseArray(result, Blog.class);
            page.setRecords(blogs);
            return page;
        }
        // 设置当前页数
        page.setCurrent(currentPage);
        // 博客数量
        Integer blogCount = 0;
        // 判断属于哪一个级别,获取对应不同的sys_param_value
        switch (level) {
            case EnumsLevel.NORMAL:
                blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT);
                break;
            case EnumsLevel.FIRST:
                blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_FIRST_COUNT);
                break;
            case EnumsLevel.SECOND:
                blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_SECOND_COUNT);
                break;
            case EnumsLevel.THIRD:
                blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_THIRD_COUNT);
                break;
            case EnumsLevel.FOURTH:
                blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_FOURTH_COUNT);
                break;
            default:
                break;
        }
        // 设置查询条数
        page.setSize(blogCount);
        // 实际查询博客数据
        IPage<Blog> blogByLevel = this.getBlogByLevel(page, level);
        // 获取数据缓存到redis中
        List<Blog> records = blogByLevel.getRecords();
        if (StringUtils.isNotEmpty(records)) {
            redisUtil.set(redisKey, JSON.toJSONString(records), 3600);
        }
        return blogByLevel;
    }

    @Override
    public IPage<Blog> getBlogByLevel(IPage<Blog> page, Integer level) {
        baseMapper.getBlogByLevel(page, level, EnumsStatus.ENABLE);
        return page;
    }

    @Override
    public IPage<Blog> getHotBlog() {
        // 优先从redis中获取博客信息
        String result = (String) redisUtil.get(BaseRedisConf.HOT_BLOG);
        IPage<Blog> page = new Page<>();
        // 判断redis中是否缓存数据
        if (StringUtils.isNotEmpty(result)) {
            List<Blog> blogs = JSON.parseArray(result, Blog.class);
            page.setRecords(blogs);
            return page;
        }
        // 设置当前页数
        page.setCurrent(0);
        // 获取系统参数中排行博客的数量
        Integer hotBlogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_HOT_COUNT);
        // 设置查询条数
        page.setSize(hotBlogCount);
        // 查询实际博客内容
        baseMapper.getHotBlog(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // 获取博客列表
        List<Blog> blogList = page.getRecords();
        // 获取对应标签数据
        blogList.forEach(item -> {
            // 获取标签id
            String tagUid = item.getTagUid();
            // 按照逗号进行拆分
            String[] tagUids = tagUid.split(BaseSysConf.FILE_SEGMENTATION);
            List<Tag> tags = new ArrayList<>();
            for (String uid : tagUids) {
                Tag tag = tagService.getById(uid);
                if (StringUtils.isNull(tag)) {
                    continue;
                }
                tags.add(tag);
            }
            item.setTagList(tags);
        });
        // 将数据缓存金redis
        if (StringUtils.isNotEmpty(blogList)) {
            redisUtil.set(BaseRedisConf.HOT_BLOG, JSON.toJSONString(blogList), 3600);
        }
        // TODO: 2021/6/1 图片问题未解决
        return page;
    }

    @Override
    public IPage<Blog> getNewBlog(Integer currentPage) {
        // 获取系统参数中最新博客数量
        Integer newBlogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT);
        IPage<Blog> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 设置查询条数
        page.setSize(newBlogCount);
        // 查询数据
        baseMapper.getNewBlog(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // 获取博客数据
        List<Blog> blogList = page.getRecords();
        // 获取对应标签数据
        blogList.forEach(item -> {
            // 获取标签id
            String tagUid = item.getTagUid();
            // 按照逗号进行拆分
            String[] tagUids = tagUid.split(BaseSysConf.FILE_SEGMENTATION);
            List<Tag> tags = new ArrayList<>();
            for (String uid : tagUids) {
                Tag tag = tagService.getById(uid);
                if (StringUtils.isNull(tag)) {
                    continue;
                }
                tags.add(tag);
            }
            item.setTagList(tags);
        });
        // TODO: 2021/6/1 图片问题未解决
        return page;
    }

    @Override
    public IPage<Blog> getBlogByTime(Integer currentPage) {
        IPage<Blog> page = new Page<>();
        // 设置当前页数
        page.setCurrent(currentPage);
        // 查询系统参数中数量
        Integer blogCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT);
        page.setSize(blogCount);
        // 查询数据
        baseMapper.getBlogByTime(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // 获取博客数据
        List<Blog> blogList = page.getRecords();
        // 获取对应标签数据
        blogList.forEach(item -> {
            // 获取标签id
            String tagUid = item.getTagUid();
            // 按照逗号进行拆分
            String[] tagUids = tagUid.split(BaseSysConf.FILE_SEGMENTATION);
            List<Tag> tags = new ArrayList<>();
            for (String uid : tagUids) {
                Tag tag = tagService.getById(uid);
                if (StringUtils.isNull(tag)) {
                    continue;
                }
                tags.add(tag);
            }
            item.setTagList(tags);
        });
        // TODO: 2021/6/1 图片问题未解决
        return page;
    }
}
