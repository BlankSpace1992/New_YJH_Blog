package com.blog.business.search.service.impl;

import com.blog.business.search.domain.ElasticSearchVO;
import com.blog.business.search.helper.HighlightResultHelper;
import com.blog.business.search.service.ElasticSearchService;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.Tag;
import com.blog.constants.BaseSysConf;
import com.blog.utils.StringUtils;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/8/31 10:47
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private HighlightResultHelper highlightResultHelper;
    @Override
    public ElasticSearchVO buildElasticSearchVO(Blog blog) {
        ElasticSearchVO elasticSearchVO = new ElasticSearchVO();
        // 复制数据
        BeanUtils.copyProperties(blog, elasticSearchVO);
        elasticSearchVO.setId(blog.getUid());
        // 判断博客分类是否为空
        if (StringUtils.isNotNull(blog.getBlogSort())) {
            elasticSearchVO.setBlogSortName(blog.getBlogSort().getSortName());
            elasticSearchVO.setBlogSortUid(blog.getBlogSort().getUid());
        }
        // 判断标签是否为空
        if (StringUtils.isNotEmpty(blog.getTagList())) {
            List<Tag> tagList = blog.getTagList();
            List<String> tagUidList = new ArrayList<>();
            List<String> tagNameList = new ArrayList<>();
            tagList.forEach(item -> {
                if (StringUtils.isNotNull(item)) {
                    tagUidList.add(item.getUid());
                    tagNameList.add(item.getContent());
                }
            });
            elasticSearchVO.setTagNameList(tagNameList);
            elasticSearchVO.setTagUidList(tagUidList);
        }
        // 判断图片是否为空
        if (StringUtils.isNotEmpty(blog.getPhotoList())) {
            elasticSearchVO.setPhotoUrl(blog.getPhotoList().get(0));
        } else {
            elasticSearchVO.setPhotoUrl("");
        }
        return elasticSearchVO;
    }

    @Override
    public Map<String, Object> search(String keywords, Integer currentPage, Integer pageSize) {
        // currentPage判断当前是否未负数
        currentPage = Math.max(currentPage - 1, 0);
        // 高亮显示字段
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>();
          // 处理标题
        HighlightBuilder.Field titleField = new HighlightBuilder.Field(BaseSysConf.TITLE).preTags("<span style='color:red'>").postTags("</span>");
        // 处理简介
        HighlightBuilder.Field summaryField = new HighlightBuilder.Field(BaseSysConf.SUMMARY).preTags("<span style='color:red'>").postTags("</span>");
        highlightFields.add(titleField);
        highlightFields.add(summaryField);
        // 集合转换为数组
        HighlightBuilder.Field[] highlightFieldsArray = highlightFields.toArray(new HighlightBuilder.Field[0]);
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 注入分页
        queryBuilder.withPageable(PageRequest.of(currentPage, pageSize));
        //过滤
        QueryStringQueryBuilder queryStrBuilder = new QueryStringQueryBuilder(keywords);
        queryStrBuilder.field("title", 0.75F).field("summary", 0.75F).field("content", 0.1F);
        // 注入过滤字段
         queryBuilder.withQuery(queryStrBuilder);
         // 注入高亮字段
        queryBuilder.withHighlightFields(highlightFieldsArray);
        //查询
        AggregatedPage<ElasticSearchVO> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), ElasticSearchVO
                .class, highlightResultHelper);
        //解析结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<ElasticSearchVO> blogList = result.getContent();
        Map<String, Object> map = new HashMap<>();
        map.put(BaseSysConf.TOTAL, total);
        map.put(BaseSysConf.TOTAL_PAGE, totalPage);
        map.put(BaseSysConf.PAGE_SIZE, pageSize);
        map.put(BaseSysConf.CURRENT_PAGE, currentPage + 1);
        map.put(BaseSysConf.BLOG_LIST, blogList);
        return map;
    }
}
