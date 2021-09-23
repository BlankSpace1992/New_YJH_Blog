package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.BlogVO;
import com.blog.business.web.domain.Blog;
import com.blog.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface BlogService extends IService<Blog> {

    /**
     * 通过推荐等级获取博客列表
     *
     * @param level       推荐等级
     * @param currentPage 当前页数
     * @param useSort     使用排序
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(Integer level, Integer currentPage, Integer useSort);

    /**
     * 通过推荐等级获取博客列表
     *
     * @param page  分页数据
     * @param level 等级
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 13:51
     */
    IPage<Blog> getBlogByLevel(IPage<Blog> page, Integer level);

    /**
     * 获取首页排行博客
     *
     * @return 博客信息
     * @author yujunhong
     * @date 2021/6/1 15:01
     */
    IPage<Blog> getHotBlog();

    /**
     * 获取最新博客
     *
     * @param currentPage 当前页数
     * @return 获取最新博客
     * @author yujunhong
     * @date 2021/6/1 15:59
     */
    IPage<Blog> getNewBlog(Integer currentPage);

    /**
     * 按时间戳获取博客
     *
     * @param currentPage 当前页数
     * @return 按时间戳获取博客
     * @author yujunhong
     * @date 2021/6/1 16:17
     */
    IPage<Blog> getBlogByTime(Integer currentPage);

    /**
     * 给博客列表设置分类,标签,图片
     *
     * @param list 博客列表
     * @author yujunhong
     * @date 2021/8/2 15:03
     */
    void setTagAndSortAndPictureByBlogList(List<Blog> list);

    /**
     * 通过点击博客获取详情信息
     *
     * @param uid 博客UID
     * @param oid 博客OID
     * @return 博客内容
     * @author yujunhong
     * @date 2021/8/9 14:46
     */
    Blog getBlogContentByUid(String uid, Integer oid);

    /**
     * 根据blogId获取相关博客
     *
     * @param blogUid 博客id
     * @return 相关博客信息
     * @author yujunhong
     * @date 2021/8/12 14:19
     */
    IPage<Blog> getSameBlogByBlogUid(String blogUid);

    /**
     * 博客查询
     *
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @return 博客查询数据
     * @author yujunhong
     * @date 2021/8/31 14:08
     */
    List<Blog> getBlogBySearch(Long currentPage, Long pageSize);

    /**
     * 搜索博客，如需ElasticSearch 需要启动 blog-search
     *
     * @param keywords    关键字
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    Map<String, Object> searchBlog(String keywords, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param tagUid      标签id
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param blogSortUid 博客分类UID
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogBySort(String blogSortUid, Long currentPage, Long pageSize);

    /**
     * 根据标签获取相关的博客
     *
     * @param author      作者名称
     * @param pageSize    每页显示数目
     * @param currentPage 当前页数
     * @return 博客信息
     * @author yujunhong
     * @date 2021/8/31 14:29
     */
    IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize);

    /**
     * 获取博客的归档日期
     *
     * @return 获取博客的归档日期
     * @author yujunhong
     * @date 2021/9/1 17:01
     */
    Set<String> getBlogTimeSortList();

    /**
     * 通过月份获取日期
     *
     * @param monthDate 月份
     * @return 当月文章
     * @author yujunhong
     * @date 2021/9/1 17:16
     */
    List<Blog> getArticleByMonth(String monthDate);

    /**
     * 获取博客信息
     *
     * @param blogVO 查询条件vo
     * @return 获取博客信息
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody getBlogList(BlogVO blogVO);

    /**
     * 新增博客
     *
     * @param blogVO 博客新增实体对象
     * @return 新增博客
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody add(BlogVO blogVO);

    /**
     * 编辑博客
     *
     * @param blogVO 编辑博客实体对象
     * @return 编辑博客
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody edit(BlogVO blogVO);

    /**
     * 本地博客上传
     *
     * @param filedatas 本地上传文件
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody uploadLocalBlog(List<MultipartFile> filedatas);

    /**
     * 推荐博客排序调整
     *
     * @param blogVOList 博客修改集合实体
     * @return 推荐博客排序调整
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody editBatch(List<BlogVO> blogVOList);

    /**
     * 删除博客信息
     *
     * @param blogVO 删除博客实体对象
     * @return 删除博客信息
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody delete(BlogVO blogVO);

    /**
     * 删除选中博客
     *
     * @param blogVoList 博客集合
     * @return 删除选中博客
     * @author yujunhong
     * @date 2021/9/17 11:39
     */
    ResultBody deleteBatch(List<BlogVO> blogVoList);

    /**
     * 获取博客数量
     *
     * @param enableFlag 可用标志
     * @return 获取博客数量
     * @author yujunhong
     * @date 2021/9/22 16:33
     */
    Integer getBlogCount(int enableFlag);

    /**
     * 获取每个标签下文章数目
     *
     * @return 获取每个标签下文章数目
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 获取每个分类下文章数量
     *
     * @return 每个分类下文章数量
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
    List<Map<String, Object>> getBlogCountByBlogSort();

    /**
     * 获取一年内的文章贡献数
     *
     * @return 一年内的文章贡献数
     * @author yujunhong
     * @date 2021/9/22 17:29
     */
 Map<String, Object> getBlogContributeCount();

}
