package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.domain.vo.BlogVO;
import com.blog.business.admin.service.AdminService;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.*;
import com.blog.business.web.mapper.BlogMapper;
import com.blog.business.web.service.*;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.enums.EnumCommentSource;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.holder.RequestHolder;
import com.blog.utils.DateUtils;
import com.blog.utils.FileUtils;
import com.blog.utils.IpUtils;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SubjectItemService subjectItemService;
    @Value(value = "${BLOG.ORIGINAL_TEMPLATE}")
    private String originalTemplate;
    @Value(value = "${BLOG.REPRINTED_TEMPLATE}")
    private String printedTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public IPage<Blog> getBlogByLevel(Integer level, Integer currentPage, Integer useSort) {
        // ?????????redis???????????????
        String redisKey = BaseRedisConf.BLOG_LEVEL + BaseRedisConf.SEGMENTATION + level;
        String result = (String) redisUtil.get(redisKey);
        IPage<Blog> page = new Page<>();
        // ??????redis??????????????????
        if (StringUtils.isNotEmpty(result)) {
            List<Blog> blogs = JSON.parseArray(result, Blog.class);
            page.setRecords(blogs);
            return page;
        }
        // ??????????????????
        page.setCurrent(currentPage);
        // ????????????
        Integer blogCount = 0;
        // ???????????????????????????,?????????????????????sys_param_value
        switch (level) {
            case EnumsLevel.NORMAL:
                blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT));
                break;
            case EnumsLevel.FIRST:
                blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_FIRST_COUNT));
                break;
            case EnumsLevel.SECOND:
                blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_SECOND_COUNT));
                break;
            case EnumsLevel.THIRD:
                blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_THIRD_COUNT));
                break;
            case EnumsLevel.FOURTH:
                blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_FOURTH_COUNT));
                break;
            default:
                break;
        }
        // ??????????????????
        page.setSize(blogCount);
        // ????????????????????????
        IPage<Blog> blogByLevel = this.getBlogByLevel(page, level);
        // ?????????????????????redis???
        List<Blog> records = blogByLevel.getRecords();
        // ????????????????????????
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
        // ?????????redis?????????????????????
        String result = (String) redisUtil.get(BaseRedisConf.HOT_BLOG);
        IPage<Blog> page = new Page<>();
        // ??????redis?????????????????????
        if (StringUtils.isNotEmpty(result)) {
            List<Blog> blogs = JSON.parseArray(result, Blog.class);
            page.setRecords(blogs);
            return page;
        }
        // ??????????????????
        page.setCurrent(0);
        // ??????????????????????????????????????????
        Integer hotBlogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_HOT_COUNT));
        // ??????????????????
        page.setSize(hotBlogCount);
        // ????????????????????????
        baseMapper.getHotBlog(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // ??????????????????
        List<Blog> blogList = page.getRecords();
        // ??????????????????/??????/??????
        setBlog(blogList);
        // ??????????????????redis
        if (StringUtils.isNotEmpty(blogList)) {
            redisUtil.set(BaseRedisConf.HOT_BLOG, JSON.toJSONString(blogList), 3600);
        }
        page.setRecords(blogList);
        return page;
    }

    @Override
    public IPage<Blog> getNewBlog(Integer currentPage) {
        IPage<Blog> page = new Page<>();
        // ???????????????????????????????????????
        int newBlogCount = Integer.parseInt(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT));
        // ??????????????????
        page.setCurrent(currentPage);
        // ??????????????????
        page.setSize(newBlogCount);
        // ????????????
        baseMapper.getNewBlog(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // ??????????????????
        List<Blog> blogList = page.getRecords();
        setBlog(blogList);
        page.setRecords(blogList);
        return page;
    }

    @Override
    public IPage<Blog> getBlogByTime(Integer currentPage) {
        IPage<Blog> page = new Page<>();
        // ??????????????????
        page.setCurrent(currentPage);
        // ???????????????????????????
        Integer blogCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT));
        page.setSize(blogCount);
        // ????????????
        baseMapper.getBlogByTime(page, EnumsStatus.PUBLISH, EnumsStatus.ENABLE);
        // ??????????????????
        List<Blog> blogList = page.getRecords();
        setBlog(blogList);
        page.setRecords(blogList);
        return page;
    }

    @Override
    public void setTagAndSortAndPictureByBlogList(List<Blog> list) {
        // ????????????id
        List<String> sortIds = new ArrayList<>();
        // ????????????id
        List<String> tagIds = new ArrayList<>();
        // ????????????id-????????????
        Set<String> fileIds = new HashSet<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileIds.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortIds.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                // tagId???????????????","??????
                List<String> string = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, item.getTagUid());
                tagIds.addAll(string);
            }
        });
        List<Map<String, Object>> picList = new ArrayList<>();
        // ????????????id
        StringBuilder fileIdBuilder = new StringBuilder();
        // ????????????,???????????????????????????
        int count = 1;
        // ????????????id,??????????????????
        for (String fileId : fileIds) {
            fileIdBuilder.append(fileId).append(",");
            if (count % 10 == 0) {
                picList.addAll(pictureFeignClient.getPicture(fileIdBuilder.toString(), ","));
                fileIdBuilder = new StringBuilder();
            }
            count++;
        }
        // ????????????????????????????????????
        if (fileIdBuilder.length() > Constants.NUM_32) {
            picList.addAll(pictureFeignClient.getPicture(fileIdBuilder.toString(), ","));
        }

        // ??????????????????
        List<BlogSort> blogSortList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sortIds)) {
            blogSortList = blogSortService.listByIds(sortIds);
        }
        // ????????????
        List<Tag> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tagIds)) {
            tagList = tagService.listByIds(tagIds);
        }

        Map<String, String> pictureMap = new HashMap<>();
        // ??????uid????????????
        Map<String, List<BlogSort>> blogSortMap =
                blogSortList.stream().collect(Collectors.groupingBy(BlogSort::getUid));
        Map<String, List<Tag>> tagMap = tagList.stream().collect(Collectors.groupingBy(Tag::getUid));
        picList = webUtils.getPictureMap(picList);
        picList.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        // ????????????/??????/??????
        for (Blog blog : list) {
            // ????????????
            if (StringUtils.isNotEmpty(blog.getBlogSortUid())) {
                if (blogSortMap.containsKey(blog.getBlogSortUid())) {
                    blog.setBlogSort(blogSortMap.get(blog.getBlogSortUid()).get(0));
                }
            }
            // ???????????????
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
            // ????????????
            if (StringUtils.isNotEmpty(blog.getFileUid())) {
                List<String> fileBlogList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, blog.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();
                fileBlogList.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                blog.setPhotoList(pictureListTemp);
                // ????????????????????????
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
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR,
                        "????????????IP??????"));
        // ????????????ip
        String ipAddr = IpUtils.getIpAddr(request);
        // ??????uid???oid???????????????
        if (StringUtils.isEmpty(uid) && oid == 0) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.PARAM_INCORRECT);
        }
        Blog blog = null;
        // ????????????uid????????????
        if (StringUtils.isNotEmpty(uid)) {
            blog = this.getById(uid);
        } else {
            LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Blog::getOid, oid);
            blog = this.getOne(wrapper);
        }
        // ????????????????????????/????????????/????????????
        if (blog == null || Integer.parseInt(blog.getStatus()) == EnumsStatus.DISABLED || EnumsStatus.NO_PUBLISH.equals(blog.getIsPublish())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.BLOG_IS_DELETE);
        }
        // ??????????????????
        setBlogCopyRight(blog);
        // ??????????????????
        setTagByBlog(blog);
        // ????????????
        setSortByBlog(blog);
        // ?????????????????????
        setPhotoListByBlog(blog);
        // ???redis???????????????????????????????????????
        String key = BaseRedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ipAddr + Constants.SYMBOL_WELL + blog.getUid();
        Integer blogClockResult =
                (Integer) redisUtil.get(key);
        // ???????????????????????????
        if (StringUtils.isNull(blogClockResult)) {
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            this.updateById(blog);

            // ?????????redis???,???????????????24??????
            redisUtil.set(key, blog.getClickCount(), 86400);
        }
        return blog;
    }

    @Override
    public IPage<Blog> getSameBlogByBlogUid(String blogUid) {
        // ????????????????????????
        Blog blog = this.getById(blogUid);
        // ????????????????????????
        String blogSortUid = blog.getBlogSortUid();
        // ????????????????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        // ??????blogSortUid???????????????
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        wrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        wrapper.eq(Blog::getBlogSortUid, blogSortUid);
        // ????????????????????????
        wrapper.ne(Blog::getUid, blogUid);
        wrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, wrapper);
        // ????????????,??????,????????????
        List<Blog> blogList = blogPage.getRecords();
        this.setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public List<Blog> getBlogBySearch(Long currentPage, Long pageSize) {
        // ????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(Long.parseLong(sysParamsService.getSysParamsValueByKey(BaseSysConf.BLOG_NEW_COUNT)));
        // ????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        List<Blog> blogPage = this.list(blogWrapper);
        this.setBlog(blogPage);
        return blogPage;
    }

    @Override
    public Map<String, Object> searchBlog(String keywords, Long currentPage, Long pageSize) {
        // ????????????
        final String keyword = keywords.trim();
        // ??????????????????
        Page<Blog> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(currentPage);
        // ????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.and(wrapper -> wrapper.like(Blog::getTitle, keyword).or().like(Blog::getSummary, keyword));
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // ???????????????????????????
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getClickCount);
        IPage<Blog> blogPage = this.page(page, blogWrapper);
        // ????????????
        List<Blog> blogList = blogPage.getRecords();
        // ????????????id??????
        List<String> blogSortUidList = new ArrayList<>();
        // ??????????????????
        final StringBuffer fileUidBuilder = new StringBuffer();
        blogList.forEach(item -> {
            // ????????????uid
            blogSortUidList.add(item.getBlogSortUid());
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidBuilder.append(item.getFileUid() + BaseSysConf.FILE_SEGMENTATION);
            }
            // ??????????????????????????????
            item.setTitle(getHighlighted(item.getTitle(), keyword));
            item.setSummary(getHighlighted(item.getSummary(), keyword));
        });
        // ??????????????????
        List<Map<String, Object>> picture = pictureFeignClient.getPicture(fileUidBuilder.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        // ??????????????????uid??????
        Map<String, String> pictureMap = new HashMap<>();
        picture.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));
        // ????????????????????????
        List<BlogSort> blogSortList = new ArrayList<>();
        if (blogSortUidList.size() > 0) {
            blogSortList = blogSortService.listByIds(blogSortUidList);
        }
        // ????????????uid????????????
        Map<String, String> blogSortMap = blogSortList.stream().collect(Collectors.toMap(BlogSort::getUid,
                BlogSort::getSortName));
        // ??????????????? ??? ??????
        blogList.forEach(item -> {
            if (blogSortMap.get(item.getBlogSortUid()) != null) {
                item.setBlogSortName(blogSortMap.get(item.getBlogSortUid()));
            }

            //????????????
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.stringToList(item.getFileUid(),
                        BaseSysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(pictureUid -> {
                    pictureListTemp.add(pictureMap.get(pictureUid));
                });
                // ????????????????????????
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        });

        Map<String, Object> map = new HashMap<>();
        // ??????????????????
        map.put(BaseSysConf.TOTAL, blogPage.getTotal());
        // ???????????????
        map.put(BaseSysConf.TOTAL_PAGE, blogPage.getPages());
        // ?????????????????????
        map.put(BaseSysConf.PAGE_SIZE, pageSize);
        // ???????????????
        map.put(BaseSysConf.CURRENT_PAGE, blogPage.getCurrent());
        // ????????????
        map.put(BaseSysConf.BLOG_LIST, blogList);
        return map;
    }

    @Override
    public IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize) {
        // ??????????????????
        Tag tag = tagService.getById(tagUid);
        // ????????????????????????
        if (StringUtils.isNotNull(tag)) {
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "????????????????????????"));
            // ??????ip??????
            String ipAddr = IpUtils.getIpAddr(request);
            //???Redis??????????????????????????????24????????????????????????????????????
            String jsonResult =
                    (String) redisUtil.get(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + "#" + tagUid);
            // ??????
            if (StringUtils.isEmpty(jsonResult)) {
                //????????????????????????
                int clickCount = tag.getClickCount() + 1;
                tag.setClickCount(clickCount);
                tagService.updateById(tag);
                //?????????????????????????????????redis???, 24???????????????
                redisUtil.set(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + tagUid, clickCount + "",
                        24 * 60 * 60);
            }
        }
        // ??????????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // ???????????????????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getTagUid, tagUid);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // ???????????????????????????
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // ????????????
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public IPage<Blog> searchBlogBySort(String blogSortUid, Long currentPage, Long pageSize) {
        // ??????????????????
        BlogSort blogSort = blogSortService.getById(blogSortUid);
        // ??????
        if (StringUtils.isNotNull(blogSort)) {
            HttpServletRequest request =
                    Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "????????????????????????"));
            // ??????ip??????
            String ipAddr = IpUtils.getIpAddr(request);
            //???Redis??????????????????????????????24????????????????????????????????????
            String jsonResult =
                    (String) redisUtil.get(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + blogSortUid);
            // ??????
            if (StringUtils.isEmpty(jsonResult)) {
                //????????????????????????
                int clickCount = blogSort.getClickCount() + 1;
                blogSort.setClickCount(clickCount);
                blogSortService.updateById(blogSort);
                //?????????????????????????????????redis???, 24???????????????
                redisUtil.set(BaseRedisConf.TAG_CLICK + BaseRedisConf.SEGMENTATION + ipAddr + BaseRedisConf.WELL_NUMBER + blogSortUid, clickCount + "",
                        24 * 60 * 60);
            }
        }
        // ??????????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // ???????????????????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getBlogSortUid, blogSortUid);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // ???????????????????????????
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // ????????????
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize) {
        // ??????????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        // ???????????????????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getAuthor, author);
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        // ???????????????????????????
        blogWrapper.select(Blog.class, item -> !item.getProperty().equals(BaseSQLConf.CONTENT));
        blogWrapper.orderByDesc(Blog::getCreateTime);
        Page<Blog> blogPage = this.page(page, blogWrapper);
        // ????????????
        List<Blog> blogList = blogPage.getRecords();
        setTagAndSortAndPictureByBlogList(blogList);
        blogPage.setRecords(blogList);
        return blogPage;
    }

    @Override
    public Set<String> getBlogTimeSortList() {
        //???Redis???????????????
        String monthResult = (String) redisUtil.get(BaseSysConf.MONTH_SET);
        //??????redis??????????????????????????????
        if (StringUtils.isNotEmpty(monthResult)) {
            return JSON.parseObject(monthResult, new TypeReference<Set<String>>() {
            });
        }
        // ?????????????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        //????????????????????????????????????????????????????????????????????????
        blogWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> list = this.list(blogWrapper);
        //???????????????????????????????????????
        setTagAndSortAndPictureByBlogList(list);
        // ????????????????????????
        Map<String, List<Blog>> map = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat(
                "yyyy???MM???").format(item.getCreateTime())));
        // ??????????????????
        Set<String> monthSet = map.keySet();
        // ?????????????????????????????????  key: ??????   value???????????????????????????
        map.forEach((key, value) -> {
            redisUtil.set(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + key,
                    JSON.toJSONString(value));
        });
        //???????????????????????????????????????redis???
        redisUtil.set(BaseSysConf.MONTH_SET, JSON.toJSONString(monthSet));
        return monthSet;
    }

    @Override
    public List<Blog> getArticleByMonth(String monthDate) {
        if (StringUtils.isEmpty(monthDate)) {
            throw new CommonErrorException(BaseMessageConf.PARAM_INCORRECT);
        }
        //???Redis???????????????
        String contentResult =
                (String) redisUtil.get(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + monthDate);
        //??????redis????????????????????????????????????
        if (StringUtils.isNotEmpty(contentResult)) {
            return JSON.parseArray(contentResult, Blog.class);
        }
        // ?????????????????????
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getIsPublish, EnumsStatus.PUBLISH);
        blogWrapper.orderByDesc(Blog::getCreateTime);
        //????????????????????????????????????????????????????????????????????????
        blogWrapper.select(Blog.class, i -> !i.getProperty().equals(BaseSQLConf.CONTENT));
        List<Blog> list = this.list(blogWrapper);
        //???????????????????????????????????????
        setTagAndSortAndPictureByBlogList(list);
        // ????????????????????????
        Map<String, List<Blog>> map = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat(
                "yyyy???MM???").format(item.getCreateTime())));
        // ??????????????????
        Set<String> monthSet = map.keySet();
        // ?????????????????????????????????  key: ??????   value???????????????????????????
        map.forEach((key, value) -> {
            redisUtil.set(BaseSysConf.BLOG_SORT_BY_MONTH + BaseSysConf.REDIS_SEGMENTATION + key,
                    JSON.toJSONString(value));
        });
        //???????????????????????????????????????redis???
        redisUtil.set(BaseSysConf.MONTH_SET, JSON.toJSONString(monthSet));
        return map.get(monthDate);
    }

    @Override
    public ResultBody getBlogList(BlogVO blogVO) {
        // ??????????????????
        Page<Blog> page = new Page<>();
        page.setCurrent(blogVO.getCurrentPage());
        page.setSize(blogVO.getPageSize());
        // ????????????????????????????????????
        IPage<Blog> blogList = baseMapper.getBlogList(page, blogVO);
        List<Blog> list = blogList.getRecords();
        // ?????????????????????????????????
        if (StringUtils.isEmpty(list)) {
            return ResultBody.success(blogList);
        }
        // ??????????????????
        StringBuilder fileUid = new StringBuilder();
        // ????????????uid??????
        List<String> sortUidList = new ArrayList<>();
        // ??????????????????
        List<String> tagUidList = new ArrayList<>();
        list.forEach(item -> {
            // ????????????
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUid.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUidList.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidListTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, item.getTagUid());
                tagUidList.addAll(tagUidListTemp);
            }
        });
        // ????????????????????????
        List<Map<String, Object>> pictureList = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUid)) {
            pictureList = this.pictureFeignClient.getPicture(fileUid.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }
        pictureList = webUtils.getPictureMap(pictureList);
        // ?????????????????????????????????
        List<BlogSort> sortList = new ArrayList<>();
        List<Tag> tagList = new ArrayList<>();
        if (sortUidList.size() > 0) {
            sortList = blogSortService.listByIds(sortUidList);
        }
        if (tagUidList.size() > 0) {
            tagList = tagService.listByIds(tagUidList);
        }
        // ????????????id????????????
        Map<String, List<BlogSort>> blogSortMap = sortList.stream().collect(Collectors.groupingBy(BlogSort::getUid));
        // ????????????id????????????
        Map<String, List<Tag>> tagUidMap = tagList.stream().collect(Collectors.groupingBy(Tag::getUid));
        // ????????????uid????????????
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(BaseSQLConf.UID).toString(), item.get(BaseSQLConf.URL).toString());
        });
        for (Blog item : list) {
            //????????????
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(blogSortMap.get(item.getBlogSortUid()).get(0));
            }
            //????????????
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidListTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, item.getTagUid());
                List<Tag> tagListTemp = new ArrayList<Tag>();
                tagUidListTemp.forEach(tag -> {
                    tagListTemp.add(tagUidMap.get(tag).get(0));
                });
                item.setTagList(tagListTemp);
            }
            //????????????
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidListTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,
                        item.getFileUid()
                );
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidListTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        blogList.setRecords(list);
        return ResultBody.success(blogList);
    }

    @Override
    public ResultBody add(BlogVO blogVO) {
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("??????????????????"));
        // ??????????????????
        String projectName = sysParamsService.getSysParamsValueByKey(BaseSysConf.PROJECT_NAME_);
        Blog blog = new Blog();
        // ???????????????,?????????????????????
        if (EnumsStatus.ORIGINAL.equals(blogVO.getIsOriginal())) {
            // ????????????????????????
            Admin admin = adminService.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
            if (StringUtils.isNotNull(admin)) {
                if (StringUtils.isNotEmpty(admin.getNickName())) {
                    blog.setAuthor(admin.getNickName());
                } else {
                    blog.setAuthor(admin.getUserName());
                }
                blog.setAdminUid(admin.getUid());
            }
            blog.setArticlesPart(projectName);
        } else {
            blog.setAuthor(blogVO.getAuthor());
            blog.setArticlesPart(blogVO.getArticlesPart());
        }
        blog.setTitle(blogVO.getTitle());
        blog.setSummary(blogVO.getSummary());
        blog.setContent(blogVO.getContent());
        blog.setTagUid(blogVO.getTagUid());
        blog.setBlogSortUid(blogVO.getBlogSortUid());
        blog.setFileUid(blogVO.getFileUid());
        blog.setLevel(String.valueOf(blogVO.getLevel()));
        blog.setIsOriginal(blogVO.getIsOriginal());
        blog.setIsPublish(blogVO.getIsPublish());
        blog.setType(blogVO.getType());
        blog.setOutsideLink(blogVO.getOutsideLink());
        blog.setStatus(String.valueOf(EnumsStatus.ENABLE));
        blog.setOpenComment(blogVO.getOpenComment());
        boolean save = this.save(blog);
        // ??????rabbitmq????????????Redis??????elasticSearch
        updateElasticSearchAndRedis(save, blog);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(BlogVO blogVO) {
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("??????????????????"));
        // ??????????????????????????????
        Blog blog = this.getById(blogVO.getUid());
        if (StringUtils.isNull(blog)) {
            return ResultBody.error("???????????????????????????");
        }
        String projectName = sysParamsService.getSysParamsValueByKey(BaseSysConf.PROJECT_NAME_);
        // ???????????????,?????????????????????
        if (EnumsStatus.ORIGINAL.equals(blogVO.getIsOriginal())) {
            // ????????????????????????
            Admin admin = adminService.getById(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
            if (StringUtils.isNotNull(admin)) {
                if (StringUtils.isNotEmpty(admin.getNickName())) {
                    blog.setAuthor(admin.getNickName());
                } else {
                    blog.setAuthor(admin.getUserName());
                }
                blog.setAdminUid(admin.getUid());
            }
            blog.setArticlesPart(projectName);
        } else {
            blog.setAuthor(blogVO.getAuthor());
            blog.setArticlesPart(blogVO.getArticlesPart());
        }
        blog.setTitle(blogVO.getTitle());
        blog.setSummary(blogVO.getSummary());
        blog.setContent(blogVO.getContent());
        blog.setTagUid(blogVO.getTagUid());
        blog.setBlogSortUid(blogVO.getBlogSortUid());
        blog.setFileUid(blogVO.getFileUid());
        blog.setLevel(String.valueOf(blogVO.getLevel()));
        blog.setIsOriginal(blogVO.getIsOriginal());
        blog.setIsPublish(blogVO.getIsPublish());
        blog.setOpenComment(blogVO.getOpenComment());
        blog.setUpdateTime(new Date());
        blog.setType(blogVO.getType());
        blog.setOutsideLink(blogVO.getOutsideLink());
        blog.setStatus(String.valueOf(EnumsStatus.ENABLE));
        boolean update = this.updateById(blog);
        // ??????rabbitmq????????????Redis??????elasticSearch
        updateElasticSearchAndRedis(update, blog);
        return ResultBody.success();
    }

    @Override
    public ResultBody uploadLocalBlog(List<MultipartFile> filedatas) {
        if (filedatas.size() == 0) {
            return ResultBody.error("??????????????????????????????");
        }
        // ??????????????????
        SystemConfig systemConfig = systemConfigService.getSystemConfig();
        if (systemConfig == null) {
            return ResultBody.error(BaseMessageConf.SYSTEM_CONFIG_NOT_EXIST);
        } else {
            if (EnumsStatus.OPEN.equals(systemConfig.getUploadQiNiu()) && (StringUtils.isEmpty(systemConfig.getQiNiuPictureBaseUrl()) || StringUtils.isEmpty(systemConfig.getQiNiuAccessKey())
                    || StringUtils.isEmpty(systemConfig.getQiNiuSecretKey()) || StringUtils.isEmpty(systemConfig.getQiNiuBucket()) || StringUtils.isEmpty(systemConfig.getQiNiuArea()))) {
                return ResultBody.error(BaseMessageConf.PLEASE_SET_QI_NIU);
            }

            if (EnumsStatus.OPEN.equals(systemConfig.getUploadLocal()) && StringUtils.isEmpty(systemConfig.getLocalPictureBaseUrl())) {
                return ResultBody.error(BaseMessageConf.PLEASE_SET_LOCAL);
            }
        }

        List<MultipartFile> fileList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        // ????????????,?????????markdown??????
        for (MultipartFile file : filedatas) {
            String fileOriginalName = Optional.ofNullable(file.getOriginalFilename()).orElse(StringUtils.EMPTY);
            if (FileUtils.isMarkdown(fileOriginalName)) {
                fileList.add(file);
                // ???????????????
                fileNameList.add(FileUtils.getFileName(fileOriginalName));
            } else {
                return ResultBody.error("???????????????Markdown??????");
            }
        }
        // ????????????-??????????????????
        List<String> fileContentList = new ArrayList<>();
        for (MultipartFile multipartFile : fileList) {
            try {
                Reader reader = new InputStreamReader(multipartFile.getInputStream(), "utf-8");
                BufferedReader br = new BufferedReader(reader);
                String line;
                String content = "";
                while ((line = br.readLine()) != null) {
                    content += line + "\n";
                }
                // ???Markdown?????????html
                String blogContent = FileUtils.markdownToHtml(content);
                fileContentList.add(blogContent);
            } catch (Exception e) {
                log.error("??????????????????");
                log.error(e.getMessage());
            }
        }
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("????????????????????????"));
        // ??????????????????
        String pictureList = request.getParameter(BaseSysConf.PICTURE_LIST);
        // JSON??????
        List<Map> list = JSONObject.parseArray(pictureList, Map.class);
        Map<String, String> pictureMap = new HashMap<>();
        for (Map<String, String> item : list) {
            if (FilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                // ???????????????????????????
                pictureMap.put(item.get(BaseSysConf.FILE_OLD_NAME), item.get(BaseSysConf.QI_NIU_URL));
            } else if (FilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                // ?????????????????????
                pictureMap.put(item.get(BaseSysConf.FILE_OLD_NAME), item.get(BaseSysConf.PIC_URL));
            } else if (FilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                // ??????MINIO?????????
                pictureMap.put(item.get(BaseSysConf.FILE_OLD_NAME), item.get(BaseSysConf.MINIO_URL));
            }
        }
        // ?????????????????????Map
        Map<String, String> matchUrlMap = new HashMap<>();
        for (String blogContent : fileContentList) {
            List<String> matchList = StringUtils.match(blogContent, "<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)>");
            for (String matchStr : matchList) {
                String[] splitList = matchStr.split("\"");
                // ?????????????????????
                if (splitList.length >= 5) {
                    // alt ??? src???????????????
                    // ???????????????????????????
                    String pictureUrl = "";
                    if (matchStr.indexOf("alt") > matchStr.indexOf("src")) {
                        pictureUrl = splitList[1];
                    } else {
                        pictureUrl = splitList[3];
                    }

                    // ???????????????????????????????????????
                    if (!pictureUrl.startsWith(BaseSysConf.HTTP)) {
                        // ??????????????????????????????map????????????
                        for (Map.Entry<String, String> map : pictureMap.entrySet()) {
                            // ??????Map????????????????????????????????????key???
                            if (pictureUrl.contains(map.getKey())) {
                                if (FilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                                    // ???????????????????????????
                                    matchUrlMap.put(pictureUrl, systemConfig.getQiNiuPictureBaseUrl() + map.getValue());
                                } else if (FilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                                    // ?????????????????????
                                    matchUrlMap.put(pictureUrl, systemConfig.getLocalPictureBaseUrl() + map.getValue());
                                } else if (FilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                                    // ??????MINIO?????????
                                    matchUrlMap.put(pictureUrl, systemConfig.getMinioPictureBaseUrl() + map.getValue());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        // ????????????????????????????????????????????????
        LambdaQueryWrapper<BlogSort> blogSortWrapper = new LambdaQueryWrapper<>();
        blogSortWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        blogSortWrapper.last(BaseSysConf.LIMIT_ONE);
        blogSortWrapper.orderByDesc(BlogSort::getSort);
        BlogSort blogSort = blogSortService.getOne(blogSortWrapper);
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(Tag::getStatus, EnumsStatus.ENABLE);
        tagWrapper.last(BaseSysConf.LIMIT_ONE);
        tagWrapper.orderByDesc(Tag::getSort);
        Tag tag = tagService.getOne(tagWrapper);

        // ????????????????????????
        LambdaQueryWrapper<Picture> pictureWrapper = new LambdaQueryWrapper<>();
        pictureWrapper.eq(Picture::getStatus, EnumsStatus.ENABLE);
        pictureWrapper.last(BaseSysConf.LIMIT_ONE);
        pictureWrapper.orderByDesc(Picture::getCreateTime);
        Picture picture = pictureService.getOne(pictureWrapper);
        if (blogSort == null || tag == null || picture == null) {
            return ResultBody.error("??????????????????????????????????????????????????????????????????????????????????????????");
        }

        // ?????????????????????
        Admin admin = adminService.getCurrentAdmin();
        // ???????????????????????????
        List<Blog> blogList = new ArrayList<>();
        // ??????????????????????????????
        int count = 0;
        // ???????????????
        String projectName = sysParamsService.getSysParamsValueByKey(BaseSysConf.PROJECT_NAME_);
        for (String content : fileContentList) {
            // ???????????????????????????
            for (Map.Entry<String, String> map : matchUrlMap.entrySet()) {
                content = content.replace(map.getKey(), map.getValue());
            }
            Blog blog = new Blog();
            blog.setBlogSortUid(blogSort.getUid());
            blog.setTagUid(tag.getUid());
            blog.setAdminUid(admin.getUid());
            blog.setAuthor(admin.getNickName());
            blog.setArticlesPart(projectName);
            blog.setLevel(String.valueOf(EnumsLevel.NORMAL));
            blog.setTitle(fileNameList.get(count));
            blog.setSummary(fileNameList.get(count));
            blog.setContent(content);
            blog.setFileUid(picture.getFileUid());
            blog.setIsOriginal(EnumsStatus.ORIGINAL);
            blog.setIsPublish(EnumsStatus.NO_PUBLISH);
            blog.setOpenComment(EnumsStatus.OPEN);
            blog.setType(Constants.STR_ZERO);
            blogList.add(blog);
            count++;
        }
        // ??????????????????
        this.saveBatch(blogList);
        return ResultBody.success();
    }

    @Override
    public ResultBody editBatch(List<BlogVO> blogVOList) {
        // ????????????uid
        List<String> blogUidList = new ArrayList<>();
        // ????????????uid???????????????map
        Map<String, BlogVO> blogVoMap = new HashMap<>();
        blogVOList.forEach(item -> {
            blogUidList.add(item.getUid());
            blogVoMap.put(item.getUid(), item);
        });
        // ????????????????????????
        List<Blog> blogs = this.listByIds(blogUidList);
        blogs.forEach(blog -> {
            BlogVO blogVO = blogVoMap.get(blog.getUid());
            if (blogVO != null) {
                blog.setAuthor(blogVO.getAuthor());
                blog.setArticlesPart(blogVO.getArticlesPart());
                blog.setTitle(blogVO.getTitle());
                blog.setSummary(blogVO.getSummary());
                blog.setContent(blogVO.getContent());
                blog.setTagUid(blogVO.getTagUid());
                blog.setBlogSortUid(blogVO.getBlogSortUid());
                blog.setFileUid(blogVO.getFileUid());
                blog.setLevel(String.valueOf(blogVO.getLevel()));
                blog.setIsOriginal(blogVO.getIsOriginal());
                blog.setIsPublish(blogVO.getIsPublish());
                blog.setSort(blogVO.getSort());
                blog.setType(blogVO.getType());
                blog.setOutsideLink(blogVO.getOutsideLink());
                blog.setStatus(String.valueOf(EnumsStatus.ENABLE));
            }
        });
        this.updateBatchById(blogs);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(BlogVO blogVO) {
        // ??????????????????
        Blog blog = this.getById(blogVO.getUid());
        blog.setStatus(String.valueOf(EnumsStatus.DISABLED));
        boolean delete = this.updateById(blog);
        // ??????????????????elasticSearch
        if (delete) {
            Map<String, Object> map = new HashMap<>();

            map.put(BaseSysConf.COMMAND, BaseSysConf.DELETE);
            map.put(BaseSysConf.BLOG_UID, blog.getUid());
            map.put(BaseSysConf.LEVEL, blog.getLevel());
            map.put(BaseSysConf.CREATE_TIME, blog.getCreateTime());
            //?????????RabbitMq
            rabbitTemplate.convertAndSend(BaseSysConf.EXCHANGE_DIRECT, BaseSysConf.CLOUD_BLOG, JSON.toJSONString(map));

            // ????????????????????????????????????Item
            List<String> blogUidList = new ArrayList<>(Constants.NUM_ONE);
            blogUidList.add(blogVO.getUid());
            subjectItemService.deleteBatchSubjectItemByBlogUid(blogUidList);
            // ??????????????????????????????
            commentService.batchDeleteCommentByBlogUid(blogUidList);
        }
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<BlogVO> blogVoList) {
        List<String> uidList = new ArrayList<>();
        StringBuffer uidSbf = new StringBuffer();
        blogVoList.forEach(item -> {
            uidList.add(item.getUid());
            uidSbf.append(item.getUid()).append(BaseSysConf.FILE_SEGMENTATION);
        });
        List<Blog> blogList = this.listByIds(uidList);
        blogList.forEach(item -> {
            item.setStatus(String.valueOf(EnumsStatus.DISABLED));
        });
        boolean delete = this.updateBatchById(blogList);
        if (delete) {
            Map<String, Object> map = new HashMap<>();
            map.put(BaseSysConf.COMMAND, BaseSysConf.DELETE_BATCH);
            map.put(BaseSysConf.UID, uidSbf);
            //?????????RabbitMq
            rabbitTemplate.convertAndSend(BaseSysConf.EXCHANGE_DIRECT, BaseSysConf.CLOUD_BLOG, JSON.toJSONString(map));
            // ????????????????????????????????????Item
            subjectItemService.deleteBatchSubjectItemByBlogUid(uidList);
            // ??????????????????????????????
            commentService.batchDeleteCommentByBlogUid(uidList);
        }
        return ResultBody.success();
    }

    @Override
    public Integer getBlogCount(int enableFlag) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getStatus, enableFlag);
        return this.count(wrapper);
    }

    @Override
    public List<Map<String, Object>> getBlogCountByTag() {
        // ?????????redis???????????????????????????????????????
        String resultJson =
                (String) redisUtil.get(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_TAG);
        if (StringUtils.isNotEmpty(resultJson)) {
            List<Object> list = JSON.parseArray(resultJson, Object.class);
            List<Map<String, Object>> map = new ArrayList<>();
            list.forEach(item -> map.add((Map<String, Object>) item));
            return map;
        }
        List<Map<String, Object>> blogCountByTag = baseMapper.getBlogCountByTag();
        // ??? ??????????????????????????? ?????????Redis???????????????2?????????
        if (blogCountByTag.size() > 0) {
            redisUtil.set(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_TAG,
                    JSON.toJSONString(blogCountByTag), 2 * 3600);
        }
        return blogCountByTag;
    }

    @Override
    public List<Map<String, Object>> getBlogCountByBlogSort() {
        // ?????????redis???????????????????????????????????????
        String resultJson =
                (String) redisUtil.get(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_SORT);
        if (StringUtils.isNotEmpty(resultJson)) {
            List<Object> list = JSON.parseArray(resultJson, Object.class);
            List<Map<String, Object>> map = new ArrayList<>();
            list.forEach(item -> map.add((Map<String, Object>) item));
            return map;
        }
        List<Map<String, Object>> blogCountByBlogSort = baseMapper.getBlogCountByBlogSort();
        // ??? ??????????????????????????? ?????????Redis???????????????2?????????
        if (blogCountByBlogSort.size() > 0) {
            redisUtil.set(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_SORT,
                    JSON.toJSONString(blogCountByBlogSort), 2 * 3600);
        }
        return blogCountByBlogSort;
    }

    @Override
    public Map<String, Object> getBlogContributeCount() {
        // ???Redis?????????????????????????????????????????????
        String jsonMap =
                (String) redisUtil.get(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_CONTRIBUTE_COUNT);
        if (StringUtils.isNotEmpty(jsonMap)) {
            return JSON.parseObject(jsonMap, new TypeReference<Map<String, Object>>() {
            });
        }
        // ?????????????????????
        Date nowDate = DateUtils.getNowDate();
        // ??????365???????????????
        Date startDate = DateUtils.getDate(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", nowDate), -365);
        List<Map<String, Object>> blogContributeCount = baseMapper.getBlogContributeCount(startDate, nowDate);
        // ????????????????????????
        List<String> dayBetweenDates = DateUtils.getDayBetweenDates(startDate, nowDate);

        Map<String, Object> dateMap = new HashMap<>();
        for (Map<String, Object> itemMap : blogContributeCount) {
            dateMap.put(itemMap.get("date").toString(), itemMap.get("count"));
        }
        List<List<Object>> resultList = new ArrayList<>();
        for (String item : dayBetweenDates) {
            int count = 0;
            if (dateMap.get(item) != null) {
                count = Integer.parseInt(dateMap.get(item).toString());
            }
            List<Object> objectList = new ArrayList<>();
            objectList.add(item);
            objectList.add(count);
            resultList.add(objectList);
        }
        Map<String, Object> resultMap = new HashMap<>(Constants.NUM_TWO);
        List<String> contributeDateList = new ArrayList<>();
        contributeDateList.add(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", startDate));
        contributeDateList.add(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", nowDate));
        resultMap.put(BaseSysConf.CONTRIBUTE_DATE, contributeDateList);
        resultMap.put(BaseSysConf.BLOG_CONTRIBUTE_COUNT, resultList);
        return resultMap;
    }

    @Override
    public ResultBody praiseBlogByUid(String uid) {
        // ????????????
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "????????????"));
        // ????????????uid
        String userId = request.getAttribute(BaseSysConf.USER_UID).toString();
        if (StringUtils.isEmpty(userId)) {
            return ResultBody.error(BaseMessageConf.PLEASE_LOGIN_TO_PRISE);
        }
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getUserUid, userId);
        commentWrapper.eq(Comment::getBlogUid, uid);
        commentWrapper.eq(Comment::getType, EnumsStatus.PRAISE);
        commentWrapper.last(BaseSysConf.LIMIT_ONE);
        Comment comment = commentService.getOne(commentWrapper);
        if (StringUtils.isNotNull(comment)) {
            return ResultBody.error(BaseMessageConf.YOU_HAVE_BEEN_PRISE);
        }
        // ????????????????????????
        Blog blog = this.getById(uid);
        if (StringUtils.isNull(blog)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // ?????????redis??????????????????
        String praiseJsonResult = (String) redisUtil.get(BaseRedisConf.BLOG_PRAISE + BaseRedisConf.SEGMENTATION + uid);
        int praiseCount = 1;
        // ?????????????????????
        if (StringUtils.isNotEmpty(praiseJsonResult)) {
            praiseCount = blog.getCollectCount() + 1;
        }
        //?????????????????? +1
        redisUtil.set(BaseRedisConf.BLOG_PRAISE + BaseRedisConf.SEGMENTATION + uid, String.valueOf(praiseCount));
        blog.setCollectCount(praiseCount);
        this.updateById(blog);
        // ????????????????????????
        Comment newComment = new Comment();
        newComment.setUserUid(userId);
        newComment.setBlogUid(uid);
        newComment.setSource(EnumCommentSource.BLOG_INFO.getCode());
        newComment.setType(EnumsStatus.PRAISE);
        commentService.save(newComment);
        return ResultBody.success(praiseCount);
    }

    @Override
    public Integer getBlogPraiseCountByUid(String uid) {
        // ??????????????????0
        int praiseCount = 0;
        if (StringUtils.isEmpty(uid)) {
            return praiseCount;
        }
        //???Redis????????????????????????
        String praiseJsonResult = (String) redisUtil.get(BaseRedisConf.BLOG_PRAISE + BaseRedisConf.SEGMENTATION + uid);
        if (!StringUtils.isEmpty(praiseJsonResult)) {
            praiseCount = Integer.parseInt(praiseJsonResult);
        }
        return praiseCount;
    }

    /**
     * ?????????????????????????????????,??????
     *
     * @param list ?????????????????????
     * @author yujunhong
     * @date 2021/8/6 15:34
     */
    private void setBlog(List<Blog> list) {
        // ??????id
        StringBuilder fileIds = new StringBuilder();
        // ????????????
        List<String> sortIdList = new ArrayList<>();
        // ????????????
        List<String> tagIdList = new ArrayList<>();
        // ???????????????????????????
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
        // ????????????
        List<Map<String, Object>> pictureList = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileIds.toString())) {
            pictureList = this.pictureFeignClient.getPicture(fileIds.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }
        pictureList = webUtils.getPictureMap(pictureList);
        // ????????????????????????
        List<BlogSort> blogSortList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sortIdList)) {
            blogSortList = blogSortService.listByIds(sortIdList);
        }
        // ???????????????????????????
        List<Tag> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tagIdList)) {
            tagList = tagService.listByIds(tagIdList);
        }

        // ????????????id????????????
        Map<String, List<BlogSort>> blogSortMap =
                blogSortList.stream().collect(Collectors.groupingBy(BlogSort::getUid));
        // ????????????id????????????
        Map<String, List<Tag>> tagMap = tagList.stream().collect(Collectors.groupingBy(Tag::getUid));
        // ????????????id????????????
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(BaseSQLConf.UID).toString(), item.get(BaseSQLConf.URL).toString());
        });

        // ??????????????????
        for (Blog blog : list) {
            //????????????
            if (StringUtils.isNotEmpty(blog.getBlogSortUid())) {
                blog.setBlogSort(blogSortMap.get(blog.getBlogSortUid()).get(0));
            }
            //????????????
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
            //????????????
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
     * ??????????????????
     *
     * @param blog ????????????
     * @author yujunhong
     * @date 2021/8/9 15:03
     */
    private void setBlogCopyRight(Blog blog) {
        // ??????????????????
        if (Constants.STR_ONE.equals(blog.getIsOriginal())) {
            blog.setCopyright(originalTemplate);
        } else {
            String reprintedTemplate = printedTemplate;
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, variable[0], variable[1]);
            blog.setCopyright(str);
        }
    }

    /**
     * ?????????????????????
     *
     * @param blog ??????????????????
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
     * ?????????????????????
     *
     * @param blog ??????????????????
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
     * ?????????????????????
     *
     * @param blog ??????????????????
     * @author yujunhong
     * @date 2021/8/9 15:16
     */
    private void setPhotoListByBlog(Blog blog) {
        //??????????????????
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
     * ??????????????????????????????
     *
     * @param str     ????????????
     * @param keyword ?????????
     * @return ??????????????????????????????
     * @author yujunhong
     * @date 2021/8/31 14:42
     */
    private String getHighlighted(String str, String keyword) {
        if (StringUtils.isEmpty(keyword) || StringUtils.isEmpty(str)) {
            return str;
        }
        String startStr = "<span style = 'color:red'>";
        String endStr = "</span>";
        // ??????????????????????????????????????????????????????????????????
        if (str.equals(keyword)) {
            return startStr + str + endStr;
        }
        String lowerCaseStr = str.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        String[] lowerCaseArray = lowerCaseStr.split(lowerKeyword);
        boolean isEndWith = lowerCaseStr.endsWith(lowerKeyword);

        // ?????????????????????????????????
        int count = 0;
        List<Map<String, Integer>> list = new ArrayList<>();
        List<Map<String, Integer>> keyList = new ArrayList<>();
        for (int a = 0; a < lowerCaseArray.length; a++) {
            // ????????????????????????map
            Map<String, Integer> map = new HashMap<>();
            Map<String, Integer> keyMap = new HashMap<>();
            map.put("startIndex", count);
            int len = lowerCaseArray[a].length();
            count += len;
            map.put("endIndex", count);
            list.add(map);
            if (a < lowerCaseArray.length - 1 || isEndWith) {
                // ???keyword??????map
                keyMap.put("startIndex", count);
                count += keyword.length();
                keyMap.put("endIndex", count);
                keyList.add(keyMap);
            }
        }
        // ??????????????????
        List<String> arrayList = new ArrayList<>();
        for (Map<String, Integer> item : list) {
            Integer start = item.get("startIndex");
            Integer end = item.get("endIndex");
            String itemStr = str.substring(start, end);
            arrayList.add(itemStr);
        }
        // ???????????????
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

    /**
     * ???????????????????????????????????????elasticSearch ??? redis
     *
     * @param isSave ???????????????
     * @param blog   ????????????
     * @author yujunhong
     * @date 2021/11/4 14:04
     */
    private void updateElasticSearchAndRedis(boolean isSave, Blog blog) {
        Map<String, Object> map = new HashMap<>();
        map.put(BaseSysConf.BLOG_UID, blog.getUid());
        map.put(BaseSysConf.LEVEL, blog.getLevel());
        map.put(BaseSysConf.CREATE_TIME, blog.getCreateTime());
        // ????????????,?????????????????????????????????
        if (isSave && EnumsStatus.PUBLISH.equals(blog.getIsPublish())) {
            map.put(BaseSysConf.COMMAND, BaseSysConf.ADD);
        }
        // ???????????????redis???????????????????????????
        else if (EnumsStatus.NO_PUBLISH.equals(blog.getIsPublish())) {
            map.put(BaseSysConf.COMMAND, BaseSysConf.EDIT);
        }
        rabbitTemplate.convertAndSend(BaseSysConf.EXCHANGE_DIRECT, BaseSysConf.CLOUD_BLOG, JSON.toJSONString(map));
    }
}
