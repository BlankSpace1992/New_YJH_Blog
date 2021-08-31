package com.blog.business.search.service;

import com.blog.business.search.domain.ElasticSearchVO;
import com.blog.business.web.domain.Blog;

import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/8/31 10:47
 */
public interface ElasticSearchService {

    /**
     * 构造ElasticSearch 实体对象
     *
     * @param blog 博客实体对象
     * @return ElasticSearch 实体对象
     * @author yujunhong
     * @date 2021/8/31 11:05
     */
    ElasticSearchVO buildElasticSearchVO(Blog blog);

    /**
     * 根据ElasticSearch 查询数据
     *
     * @param keywords    主题
     * @param pageSize    页行数
     * @param currentPage 当前页数
     * @return 查询数据
     * @author yujunhong
     * @date 2021/8/31 11:06
     */
    Map<String, Object> search(String keywords, Integer currentPage, Integer pageSize);
}
