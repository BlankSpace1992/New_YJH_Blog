package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.TagVO;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.Tag;
import com.blog.business.web.mapper.TagMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.SysParamsService;
import com.blog.business.web.service.TagService;
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
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private BlogService blogService;

    @Override
    public IPage<Tag> getHotTag() {
        // 获取系统参数中配置得标签数量
        int hotTagCount = Integer.parseInt(sysParamsService.getSysParamsValueByKey(BaseSysConf.HOT_TAG_COUNT));
        // 优先从redis中获取数据
        String redisKey = BaseRedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount;
        String result = (String) redisUtil.get(redisKey);
        IPage<Tag> page = new Page<>();
        // 判断redis中是否有值
        if (StringUtils.isNotEmpty(result)) {
            List<Tag> tags = JSON.parseArray(result, Tag.class);
            page.setRecords(tags);
            return page;
        }
        // 从数据库中获取数据
        page.setCurrent(1);
        page.setSize(hotTagCount);
        baseMapper.getHotTag(page, EnumsStatus.ENABLE);
        // 获取标签列表
        List<Tag> tagList = page.getRecords();
        if (StringUtils.isNotEmpty(tagList)) {
            redisUtil.set(redisKey, JSON.toJSONString(tagList), 3600);
        }
        return page;
    }

    @Override
    public List<Tag> getList() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getStatus, EnumsStatus.ENABLE);
        queryWrapper.orderByDesc(Tag::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<Tag> getPageList(TagVO tagVO) {
        // 注入分页参数
        IPage<Tag> page = new Page<>();
        page.setCurrent(tagVO.getCurrentPage());
        page.setSize(tagVO.getPageSize());
        return baseMapper.getPageList(page, tagVO);
    }

    @Override
    public ResultBody add(TagVO tagVO) {
        // 查询当前标签名称是否已经存在
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(Tag::getStatus, EnumsStatus.ENABLE);
        tagWrapper.eq(Tag::getContent, tagVO.getContent());
        int count = this.count(tagWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Tag tag = new Tag();
        tag.setContent(tagVO.getContent());
        tag.setClickCount(0);
        tag.setStatus(EnumsStatus.ENABLE);
        tag.setSort(tagVO.getSort());
        this.save(tag);
        // 删除Redis中的友链列表
        deleteRedisBlogTagList();
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(TagVO tagVO) {
        // 获取当前标签信息
        Tag tag = this.getById(tagVO.getUid());
        if (StringUtils.isNull(tag)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        } else {
            if (!tagVO.getContent().equals(tag.getContent())) {
                LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
                tagWrapper.eq(Tag::getStatus, EnumsStatus.ENABLE);
                tagWrapper.eq(Tag::getContent, tagVO.getContent());
                int count = this.count(tagWrapper);
                if (count > 0) {
                    return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
                }
            }
        }
        tag.setContent(tagVO.getContent());
        tag.setStatus(EnumsStatus.ENABLE);
        tag.setSort(tagVO.getSort());
        tag.setUpdateTime(new Date());
        this.updateById(tag);
        // 删除和博客标签有关的Redis缓存
        deleteRedisByBlogTag();
        // 删除Redis中的友链列表
        deleteRedisBlogTagList();
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<TagVO> tagVoList) {
        if (tagVoList.size() <= 0) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        tagVoList.forEach(item -> {
            uids.add(item.getUid());
        });
        // 判断是否有博客关联当前标签
        LambdaQueryWrapper<Blog> blogQueryWrapper = new LambdaQueryWrapper<>();
        blogQueryWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogQueryWrapper.in(Blog::getTagUid, uids);
        Integer blogCount = blogService.count(blogQueryWrapper);
        if (blogCount > 0) {
            return ResultBody.error(BaseMessageConf.BLOG_UNDER_THIS_TAG);
        }
        List<Tag> tagList = this.listByIds(uids);

        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EnumsStatus.DISABLED);
        });
        this.updateBatchById(tagList);
        // 删除和标签相关的博客缓存
       deleteRedisByBlogTag();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultBody.success();
    }

    @Override
    public ResultBody stick(TagVO tagVO) {
        // 获取当前标签信息
        Tag tag = this.getById(tagVO.getUid());
        if (StringUtils.isNull(tag)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // 获取最大的排序
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.orderByDesc(Tag::getSort);
        tagWrapper.last(BaseSysConf.LIMIT_ONE);
        Tag maxSortTag = this.getOne(tagWrapper);
        if (StringUtils.isEmpty(maxSortTag.getUid())) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        if (maxSortTag.getUid().equals(tag.getUid())) {
            return ResultBody.error(BaseMessageConf.THIS_TAG_IS_TOP);
        }

        int sortCount = maxSortTag.getSort() + 1;
        tag.setSort(sortCount);
        tag.setUpdateTime(new Date());
        this.updateById(tag);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultBody.success();
    }

    @Override
    public ResultBody tagSortByClickCount() {
        return null;
    }

    @Override
    public ResultBody tagSortByCite() {
        // 定义Map   key：tagUid,  value: 引用量
        Map<String, Integer> map = new HashMap<>();
        // 查询标签信息
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(Tag::getStatus, EnumsStatus.ENABLE);
        List<Tag> list = this.list(tagWrapper);
        // 初始化所有标签的引用量
        list.forEach(item -> {
            map.put(item.getUid(), 0);
        });
        // 查询博客信息
        LambdaQueryWrapper<Blog> blogQueryWrapper = new LambdaQueryWrapper<>();
        blogQueryWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogQueryWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 过滤content字段
        blogQueryWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> blogList = blogService.list(blogQueryWrapper);
        blogList.forEach(item -> {
            String tagUidString = item.getTagUid();
            List<String> tagUidList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,tagUidString);
            for (String tagUid : tagUidList) {
                if (map.get(tagUid) != null) {
                    Integer count = map.get(tagUid) + 1;
                    map.put(tagUid, count);
                } else {
                    map.put(tagUid, 0);
                }
            }
        });

        list.forEach(item -> {
            item.setSort(map.get(item.getUid()));
            item.setUpdateTime(new Date());
        });
        this.updateBatchById(list);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultBody.success();
    }

    /**
     * 删除Redis中的友链列表
     *
     * @author yujunhong
     * @date 2021/9/23 16:05
     */
    private void deleteRedisBlogTagList() {
        // 删除Redis中的BLOG_LINK
        Set<Object> keys = redisUtil.keys(BaseRedisConf.BLOG_TAG + Constants.SYMBOL_COLON + "*");
        redisUtil.delete(keys);
    }

    /**
     * 删除和博客标签有关的Redis缓存
     *
     * @author yujunhong
     * @date 2021/9/23 16:13
     */
    private void deleteRedisByBlogTag() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_TAG);
        // 删除博客相关缓存
        redisUtil.delete(BaseRedisConf.NEW_BLOG);
        redisUtil.delete(BaseRedisConf.HOT_BLOG);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.FIRST);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.SECOND);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.THIRD);
        redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + EnumsLevel.FOURTH);
    }
}
