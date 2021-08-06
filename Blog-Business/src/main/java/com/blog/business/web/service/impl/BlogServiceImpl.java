package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.BlogSort;
import com.blog.business.web.domain.Tag;
import com.blog.business.web.mapper.BlogMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.BlogSortService;
import com.blog.business.web.service.SysParamsService;
import com.blog.business.web.service.TagService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.feign.PictureFeignClient;
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
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private BlogSortService blogSortService;

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
        // 设置标签以及分类
        setBlog(records);
        if (StringUtils.isNotEmpty(records)) {
            redisUtil.set(redisKey, JSON.toJSONString(records), 3600);
        }
        blogByLevel.setRecords(records);
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

    @Override
    public List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list) {
        // 分类集合id
        List<String> sortIds = new ArrayList<>();
        // 标签集合id
        List<String> tagIds = new ArrayList<>();
        // 文件集合id-避免重复
        Set<String> fileIds = new HashSet<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileIds.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortIds.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                // tagId有多个按照","区分
                List<String> string = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, item.getTagUid());
                tagIds.addAll(string);
            }
        });
        List<Map<String, Object>> picList = new ArrayList<>();
        // 拼接文件id
        StringBuilder fileIdBuilder = new StringBuilder();
        // 手动分页,按照十张图片查一次
        int count = 1;
        // 根据文件id,查询图片数据
        for (String fileId : fileIds) {
            fileIdBuilder.append(fileId).append(",");
            if (count % 10 == 0) {
                picList.addAll(pictureFeignClient.getPicture(fileIdBuilder.toString(), ","));
                fileIdBuilder = new StringBuilder();
            }
            count++;
        }
        // 判断是否需要图片需要获取
        if (fileIdBuilder.length() > Constants.NUM_32) {
            picList.addAll(pictureFeignClient.getPicture(fileIdBuilder.toString(), ","));
        }

        // 获取博客分类
        List<BlogSort> blogSortList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sortIds)) {
            blogSortList = blogSortService.listByIds(sortIds);
        }
        // 获取标签
        List<Tag> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tagIds)) {
            tagList = tagService.listByIds(tagIds);
        }

        Map<String, String> pictureMap = new HashMap<>();
        // 按照uid进行分组
        Map<String, List<BlogSort>> blogSortMap =
                blogSortList.stream().collect(Collectors.groupingBy(BlogSort::getUid));
        Map<String, List<Tag>> tagMap = tagList.stream().collect(Collectors.groupingBy(Tag::getUid));
        picList.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        // 注入分类/标签/图片
        for (Blog blog : list) {
            // 设置分类
            if (StringUtils.isNotEmpty(blog.getBlogSortUid())) {
                if (blogSortMap.containsKey(blog.getBlogSortUid())) {
                    blog.setBlogSort(blogSortMap.get(blog.getBlogSortUid()).get(0));
                }
            }
            // 设置标签页
            if (StringUtils.isNotEmpty(blog.getTagUid())) {
                List<String> tagBlogList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, blog.getTagUid());
                List<Tag> tagListTemp = new ArrayList<Tag>();
                tagBlogList.forEach(tag -> {
                    if (tagMap.containsKey(tag)) {
                        tagListTemp.add(tagMap.get(tag).get(0));
                    }
                });
                blog.setTagList(tagListTemp);
            }
            // 获取图片
            if (StringUtils.isNotEmpty(blog.getFileUid())) {
                List<String> fileBlogList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, blog.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();
                fileBlogList.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                blog.setPhotoList(pictureListTemp);
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    blog.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    blog.setPhotoUrl("");
                }
            }
        }
        return list;
    }

    /**
     * 设置博客的标签以及分类,图片
     *
     * @param list 查询的博客集合
     * @author yujunhong
     * @date 2021/8/6 15:34
     */
    private void setBlog(List<Blog> list) {
        // 图片id
        StringBuilder fileIds = new StringBuilder();
        // 分类集合
        List<String> sortIdList = new ArrayList<>();
        // 标签集合
        List<String> tagIdList = new ArrayList<>();
        // 循环处理每一个博客
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileIds.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortIdList.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                tagIdList.add(item.getTagUid());
            }
        });
        // 图片地址
        List<Map<String, Object>> pictureList = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileIds.toString())) {
            pictureList = this.pictureFeignClient.getPicture(fileIds.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }
        // 获取对应分类信息
        List<BlogSort> blogSortList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sortIdList)) {
            blogSortList = blogSortService.listByIds(sortIdList);
        }
        // 获取对应的标签信息
        List<Tag> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tagIdList)) {
            tagList = tagService.listByIds(tagIdList);
        }

        // 分类按照id进行分类
        Map<String, List<BlogSort>> blogSortMap =
                blogSortList.stream().collect(Collectors.groupingBy(BlogSort::getUid));
        // 标签按照id进行分类
        Map<String, List<Tag>> tagMap = tagList.stream().collect(Collectors.groupingBy(Tag::getUid));
        // 图片按照id进行分类
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(BaseSQLConf.UID).toString(), item.get(BaseSQLConf.URL).toString());
        });

        // 循环遍历博客
        for (Blog blog : list) {
            //设置分类
            if (StringUtils.isNotEmpty(blog.getBlogSortUid())) {
                blog.setBlogSort(blogSortMap.get(blog.getBlogSortUid()).get(0));
            }
            //获取标签
            if (StringUtils.isNotEmpty(blog.getTagUid())) {
                List<String> tagIdsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,blog.getTagUid());
                List<Tag> tagListTemp = new ArrayList<>();

                tagIdsTemp.forEach(tag -> {
                    if (tagMap.get(tag) != null) {
                        tagListTemp.add(tagMap.get(tag).get(0));
                    }
                });
                blog.setTagList(tagListTemp);
            }
            //获取图片
            if (StringUtils.isNotEmpty(blog.getFileUid())) {
                List<String> pictureIdsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,blog.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();

                pictureIdsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                blog.setPhotoList(pictureListTemp);
            }
        }
    }
}
