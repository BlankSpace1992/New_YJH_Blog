package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.utils.WebUtils;
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
import com.blog.exception.CommonErrorException;
import com.blog.feign.PictureFeignClient;
import com.blog.holder.RequestHolder;
import com.blog.utils.IpUtils;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
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
    private WebUtils webUtils;
    @Autowired
    private BlogSortService blogSortService;
    @Value(value = "${BLOG.ORIGINAL_TEMPLATE}")
    private String originalTemplate;
    @Value(value = "${BLOG.REPRINTED_TEMPLATE}")
    private String printedTemplate;

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
        baseMapper.getBlogByLevel(page, level, EnumsStatus.ENABLE, EnumsStatus.PUBLISH);
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
        // 获取对应标签/分类/图片
        setBlog(blogList);
        // 将数据缓存金redis
        if (StringUtils.isNotEmpty(blogList)) {
            redisUtil.set(BaseRedisConf.HOT_BLOG, JSON.toJSONString(blogList), 3600);
        }
        page.setRecords(blogList);
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
        setBlog(blogList);
        page.setRecords(blogList);
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
        setBlog(blogList);
        page.setRecords(blogList);
        return page;
    }

    @Override
    public void setTagAndSortAndPictureByBlogList(List<Blog> list) {
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
    }

    @Override
    public Blog getBlogContentByUid(String uid, Integer oid) {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR,
                        "获取用户IP失败"));
        // 获取用户ip
        String ipAddr = IpUtils.getIpAddr(request);
        // 判断uid和oid是否不存在
        if (StringUtils.isEmpty(uid) && oid == 0) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.PARAM_INCORRECT);
        }
        Blog blog = null;
        // 首先根据uid进行查询
        if (StringUtils.isNotEmpty(uid)) {
            blog = this.getById(uid);
        } else {
            LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Blog::getOid, oid);
            blog = this.getOne(wrapper);
        }
        // 判断博客是否为空/博客状态/是否发布
        if (blog == null || Integer.parseInt(blog.getStatus()) == EnumsStatus.DISABLED || EnumsStatus.NO_PUBLISH.equals(blog.getIsPublish())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.BLOG_IS_DELETE);
        }
        // 设置版权说明
        setBlogCopyRight(blog);
        // 设置博客标签
        setTagByBlog(blog);
        // 设置分类
        setSortByBlog(blog);
        // 设置博客标题图
        setPhotoListByBlog(blog);
        // 从redis中获取数据判断是否已经点击
        String key = BaseRedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ipAddr + Constants.SYMBOL_WELL + blog.getUid();
        Integer blogClockResult =
                (Integer) redisUtil.get(key);
        // 如果为空则并未点击
        if (StringUtils.isNull(blogClockResult)) {
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            this.updateById(blog);

            // 保存进redis中,过期时间为24小时
            redisUtil.set(key, blog.getClickCount(), 86400);
        }
        return blog;
    }

    @Override
    public IPage<Blog> getSameBlogByBlogUid(String blogUid) {
        // 获取当前博客信息
        Blog blog = this.getById(blogUid);
        // 获取博客分类信息
        String blogSortUid = blog.getBlogSortUid();
        // 默认查询十个数据
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        // 查询blogSortUid相同的博客
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        wrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        wrapper.eq(Blog::getBlogSortUid, blogSortUid);
        // 过滤掉当前的博客
        wrapper.ne(Blog::getUid, blogUid);
        wrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, wrapper);
        // 设置标签,分类,以及图片
        List<Blog> blogList = blogPage.getRecords();
        this.setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public List<Blog> getBlogBySearch(Long currentPage, Long pageSize) {
        // 分页查询
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT));
        // 查询数据
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // 获取数据
        List<Blog> blogList = blogPage.getRecords();
        this.setBlog(blogList);
        return blogList;
    }

    @Override
    public Map<String, Object> searchBlog(String keywords, Long currentPage, Long pageSize) {
        // 去除空格
        final String keyword = keywords.trim();
        // 注入分页参数
        Page<Blog> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(currentPage);
        // 查询数据
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.and(wrapper -> wrapper.like(Blog::getTitle, keyword).or().like(Blog::getSummary, keyword));
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 查询字段不包含内容
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getClickCount);
        IPage<Blog> blogPage = this.page(page, blogWrapper);
        // 获取数据
        List<Blog> blogList = blogPage.getRecords();
        // 博客分类id集合
        List<String> blogSortUidList = new ArrayList<>();
        // 拼接图片地址
        final StringBuffer fileUidBuilder = new StringBuffer();
        blogList.forEach(item -> {
            // 获取图片uid
            blogSortUidList.add(item.getBlogSortUid());
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidBuilder.append(item.getFileUid() + BaseSysConf.FILE_SEGMENTATION);
            }
            // 给标题和简介设置高亮
            item.setTitle(getHighlighted(item.getTitle(), keyword));
            item.setSummary(getHighlighted(item.getSummary(), keyword));
        });
        // 获取图片数据
        List<Map<String, Object>> picture = pictureFeignClient.getPicture(fileUidBuilder.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        // 根据图片数据uid分组
        Map<String, String> pictureMap = new HashMap<>();
        picture.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));
        // 处理博客分类情况
        List<BlogSort> blogSortList = new ArrayList<>();
        if (blogSortUidList.size() > 0) {
            blogSortList = blogSortService.listByIds(blogSortUidList);
        }
        // 按照分类uid进行分组
        Map<String, String> blogSortMap = blogSortList.stream().collect(Collectors.toMap(BlogSort::getUid,
                BlogSort::getSortName));
        // 设置分类名 和 图片
        blogList.forEach(item -> {
            if (blogSortMap.get(item.getBlogSortUid()) != null) {
                item.setBlogSortName(blogSortMap.get(item.getBlogSortUid()));
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.stringToList(item.getFileUid(),
                        BaseSysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(pictureUid -> {
                    pictureListTemp.add(pictureMap.get(pictureUid));
                });
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        });

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(BaseSysConf.TOTAL, blogPage.getTotal());
        // 返回总页数
        map.put(BaseSysConf.TOTAL_PAGE, blogPage.getPages());
        // 返回当前页大小
        map.put(BaseSysConf.PAGE_SIZE, pageSize);
        // 返回当前页
        map.put(BaseSysConf.CURRENT_PAGE, blogPage.getCurrent());
        // 返回数据
        map.put(BaseSysConf.BLOG_LIST, blogList);
        return map;
    }

    @Override
    public IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize) {
        // 获取标签信息
        Tag tag = tagService.getById(tagUid);
        // 判断标签是否为空
        if (StringUtils.isNotNull(tag)) {
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取博客信息失败"));
            // 获取ip地址
            String ipAddr = IpUtils.getIpAddr(request);
            //从Redis取出数据，判断该用户24小时内，是否点击过该标签
            String jsonResult =
                    (String) redisUtil.get(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + "#" + tagUid);
            // 判空
            if (StringUtils.isEmpty(jsonResult)) {
                //给标签点击数增加
                int clickCount = tag.getClickCount() + 1;
                tag.setClickCount(clickCount);
                tagService.updateById(tag);
                //将该用户点击记录存储到redis中, 24小时后过期
                redisUtil.set(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + tagUid, clickCount + "",
                        24 * 60 * 60);
            }
        }
        // 注入分页参数
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // 查询相同标签的博客
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getTagUid, tagUid);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 查询字段不包含内容
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // 获取数据
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public IPage<Blog> searchBlogBySort(String blogSortUid, Long currentPage, Long pageSize) {
        // 获取分类信息
        BlogSort blogSort = blogSortService.getById(blogSortUid);
        // 判空
        if (StringUtils.isNotNull(blogSort)) {
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取博客信息失败"));
            // 获取ip地址
            String ipAddr = IpUtils.getIpAddr(request);
            //从Redis取出数据，判断该用户24小时内，是否点击过该分类
            String jsonResult =
                    (String) redisUtil.get(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + blogSortUid);
            // 判空
            if (StringUtils.isEmpty(jsonResult)) {
                //给标签点击数增加
                int clickCount = blogSort.getClickCount() + 1;
                blogSort.setClickCount(clickCount);
                blogSortService.updateById(blogSort);
                //将该用户点击记录存储到redis中, 24小时后过期
                redisUtil.set(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + blogSortUid, clickCount + "",
                        24 * 60 * 60);
            }
        }
        // 注入分页参数
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // 查询相同分类的博客
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getBlogSortUid, blogSortUid);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 查询字段不包含内容
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // 获取数据
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize) {
        // 注入分页参数
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // 查询相同作者的博客
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getAuthor, author);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // 查询字段不包含内容
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // 获取数据
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public Set<String> getBlogTimeSortList() {
        //从Redis中获取内容
        String monthResult = (String) redisUtil.get(BaseSysConf.MONTH_SET);
        //判断redis中时候包含归档的内容
        if (StringUtils.isNotEmpty(monthResult)) {
            return JSON.parseObject(monthResult, new TypeReference<Set<String>>() {
            });
        }
        // 从数据库中查询
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        blogWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> list = this.list(blogWrapper);
        //给博客增加标签、分类、图片
        setTagAndSortAndPictureByBlogList(list);
        // 按照月份进行分组
        Map<String, List<Blog>> map = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat(
                "yyyy年MM月").format(item.getCreateTime())));
        // 保存归档日期
        Set<String> monthSet = map.keySet();
        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + key,
                    JSON.toJSONString(value));
        });
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(BaseSysConf.MONTH_SET, JSON.toJSONString(monthSet));
        return monthSet;
    }

    @Override
    public List<Blog> getArticleByMonth(String monthDate) {
        if (StringUtils.isEmpty(monthDate)) {
            throw new CommonErrorException(BaseMessageConf.PARAM_INCORRECT);
        }
        //从Redis中获取内容
        String contentResult =
                (String) redisUtil.get(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + monthDate);
        //判断redis中时候包含该日期下的文章
        if (StringUtils.isNotEmpty(contentResult)) {
            return JSON.parseArray(contentResult, Blog.class);
        }
        // 从数据库中查询
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        blogWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> list = this.list(blogWrapper);
        //给博客增加标签、分类、图片
        setTagAndSortAndPictureByBlogList(list);
        // 按照月份进行分组
        Map<String, List<Blog>> map = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat(
                "yyyy年MM月").format(item.getCreateTime())));
        // 保存归档日期
        Set<String> monthSet = map.keySet();
        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + key,
                    JSON.toJSONString(value));
        });
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(BaseSysConf.MONTH_SET, JSON.toJSONString(monthSet));
        return map.get(monthDate);
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
                List<String> tagIdsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, blog.getTagUid());
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
                List<String> pictureIdsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,
                        blog.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();

                pictureIdsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                blog.setPhotoList(pictureListTemp);
            }
        }
    }

    /**
     * 设置博客版权
     *
     * @param blog 博客实体
     * @author yujunhong
     * @date 2021/8/9 15:03
     */
    private void setBlogCopyRight(Blog blog) {
        // 判断是否原创
        if (Constants.STR_ONE.equals(blog.getIsOriginal())) {
            blog.setCopyright(originalTemplate);
        } else {
            String reprintedTemplate = printedTemplate;
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, (Object) variable);
            blog.setCopyright(str);
        }
    }

    /**
     * 为博客设置标签
     *
     * @param blog 博客实体对象
     * @author yujunhong
     * @date 2021/8/9 15:11
     */
    private void setTagByBlog(Blog blog) {
        String tagUid = blog.getTagUid();
        if (!StringUtils.isEmpty(tagUid)) {
            String[] uids = tagUid.split(BaseSysConf.FILE_SEGMENTATION);
            List<Tag> tagList = new ArrayList<>();
            for (String uid : uids) {
                Tag tag = tagService.getById(uid);
                if (tag != null && tag.getStatus() != EnumsStatus.DISABLED) {
                    tagList.add(tag);
                }
            }
            blog.setTagList(tagList);
        }
    }

    /**
     * 为博客设置分类
     *
     * @param blog 博客实体对象
     * @author yujunhong
     * @date 2021/8/9 15:14
     */
    private void setSortByBlog(Blog blog) {
        if (blog != null && !StringUtils.isEmpty(blog.getBlogSortUid())) {
            BlogSort blogSort = blogSortService.getById(blog.getBlogSortUid());
            blog.setBlogSort(blogSort);
        }
    }

    /**
     * 设置博客标题图
     *
     * @param blog 博客实体对象
     * @author yujunhong
     * @date 2021/8/9 15:16
     */
    private void setPhotoListByBlog(Blog blog) {
        //获取标题图片
        if (blog != null && !StringUtils.isEmpty(blog.getFileUid())) {
            List<Map<String, Object>> pictureListMap = this.pictureFeignClient.getPicture(blog.getFileUid(),
                    Constants.SYMBOL_COMMA);
            List<String> picList = webUtils.getPicture(pictureListMap);
            if (picList != null && picList.size() > 0) {
                blog.setPhotoList(picList);
            }
        }
    }

    /**
     * 为关键字设置高亮显示
     *
     * @param str     文字内容
     * @param keyword 关键字
     * @return 设置高亮后的文字内容
     * @author yujunhong
     * @date 2021/8/31 14:42
     */
    private String getHighlighted(String str, String keyword) {
        if (StringUtils.isEmpty(keyword) || StringUtils.isEmpty(str)) {
            return str;
        }
        String startStr = "<span style = 'color:red'>";
        String endStr = "</span>";
        // 判断关键字是否直接是搜索的内容，否者直接返回
        if (str.equals(keyword)) {
            return startStr + str + endStr;
        }
        String lowerCaseStr = str.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        String[] lowerCaseArray = lowerCaseStr.split(lowerKeyword);
        boolean isEndWith = lowerCaseStr.endsWith(lowerKeyword);

        // 计算分割后的字符串位置
        int count = 0;
        List<Map<String, Integer>> list = new ArrayList<>();
        List<Map<String, Integer>> keyList = new ArrayList<>();
        for (int a = 0; a < lowerCaseArray.length; a++) {
            // 将切割出来的存储map
            Map<String, Integer> map = new HashMap<>();
            Map<String, Integer> keyMap = new HashMap<>();
            map.put("startIndex", count);
            int len = lowerCaseArray[a].length();
            count += len;
            map.put("endIndex", count);
            list.add(map);
            if (a < lowerCaseArray.length - 1 || isEndWith) {
                // 将keyword存储map
                keyMap.put("startIndex", count);
                count += keyword.length();
                keyMap.put("endIndex", count);
                keyList.add(keyMap);
            }
        }
        // 截取切割对象
        List<String> arrayList = new ArrayList<>();
        for (Map<String, Integer> item : list) {
            Integer start = item.get("startIndex");
            Integer end = item.get("endIndex");
            String itemStr = str.substring(start, end);
            arrayList.add(itemStr);
        }
        // 截取关键字
        List<String> keyArrayList = new ArrayList<>();
        for (Map<String, Integer> item : keyList) {
            Integer start = item.get("startIndex");
            Integer end = item.get("endIndex");
            String itemStr = str.substring(start, end);
            keyArrayList.add(itemStr);
        }

        StringBuffer sb = new StringBuffer();
        for (int a = 0; a < arrayList.size(); a++) {
            sb.append(arrayList.get(a));
            if (a < arrayList.size() - 1 || isEndWith) {
                sb.append(startStr);
                sb.append(keyArrayList.get(a));
                sb.append(endStr);
            }
        }
        return sb.toString();
    }
}
