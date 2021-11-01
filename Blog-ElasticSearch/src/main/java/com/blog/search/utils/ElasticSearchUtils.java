package com.blog.search.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blog.business.web.domain.Blog;
import com.blog.constants.BaseSysConf;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/11/1 11:00
 */
@Component
@Slf4j
public class ElasticSearchUtils {

    private final RestHighLevelClient restHighLevelClient;
    /**
     * ElasticSearch 类型
     */
    private static final String ES_TYPE = "blog";
    /**
     * ElasticSearch 索引
     */
    private static final String ES_INDEX = "cloud";

    @Autowired
    public ElasticSearchUtils(@Qualifier("client") RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 初始化创建ElasticSearch索引
     *
     * @author yujunhong
     * @date 2021/11/1 11:02
     */
    @PostConstruct
    public void init() throws IOException {
        if (!existsIndex()) {
            CreateIndexRequest request = new CreateIndexRequest("cloud");
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request,
                    RequestOptions.DEFAULT);
            log.info("createIndex: " + JSON.toJSONString(createIndexResponse));
        }
    }

    /**
     * 查询是否存在索引,若不存在则初始化创建
     *
     * @author yujunhong
     * @date 2021/11/1 11:05
     */
    private boolean existsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(ES_INDEX);
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        log.info("existsIndex: " + exists);
        return exists;
    }

    /**
     * 新增ElasticSearch数据
     *
     * @param blog 博客信息
     * @param uid  主键id
     * @author yujunhong
     * @date 2021/11/1 11:08
     */
    public void addData(Blog blog, String uid) {
        IndexRequest indexRequest = new IndexRequest(ES_INDEX, ES_TYPE, uid).source(JSONObject.toJSONString(blog),
                XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("ElasticSearch初始化索引失败");
        }
        log.info("add: " + JSON.toJSONString(indexResponse));
    }

    /**
     * 根据id更新数据
     *
     * @param blog 博客信息
     * @param uid  主键id
     * @author yujunhong
     * @date 2021/11/1 11:15
     */
    public void updateDataById(Blog blog, String uid) throws IOException {
        UpdateRequest request = new UpdateRequest(ES_INDEX, uid);
        request.doc(JSONObject.toJSONString(blog), XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        log.info("update: " + JSON.toJSONString(updateResponse));
    }

    /**
     * 根据id删除数据
     *
     * @param uid 主键id
     * @author yujunhong
     * @date 2021/11/1 11:16
     */
    public void deleteDataById(String uid) {
        DeleteRequest request = new DeleteRequest(ES_INDEX, uid);
        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除ElasticSearch索引失败");
        }
        log.info("delete: " + JSON.toJSONString(deleteResponse));
    }

    /**
     * 进行分页查询数据
     *
     * @param startRow     起始页数
     * @param size         查询条数
     * @param queryBuilder 查询条件builder
     * @return 博客数据
     * @author yujunhong
     * @date 2021/11/1 11:18
     */
    public Map<String, Object> searchDataPage(int startRow, int size, BoolQueryBuilder queryBuilder) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(startRow-1);
        // 获取记录数，默认10
        sourceBuilder.size(size);

        //设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        SearchRequest searchRequest = new SearchRequest(ES_INDEX);
        searchRequest.source(sourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("ElasticSearch查询失败");
        }

        //遍历结果
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            //处理高亮片段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField nameField = highlightFields.get("title");
            if (nameField != null) {
                Text[] fragments = nameField.fragments();
                StringBuilder nameTmp = new StringBuilder();
                for (Text text : fragments) {
                    nameTmp.append(text);
                }
                //将高亮片段组装到结果中去
                source.put("title", nameTmp.toString());
                log.info(source.toString());
            }
        }
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        List<Blog> blogList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            JSONObject jsonObject = new JSONObject(hit.getSourceAsMap());
            log.info("search:{}", jsonObject.toJSONString());
            Blog object = JSONObject.toJavaObject(jsonObject, Blog.class);
            // 处理图片问题
            object.setPhotoUrl(object.getPhotoList().get(0));
            blogList.add(object);
        }
        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(BaseSysConf.TOTAL, hits.getTotalHits());
        // 返回总页数
        map.put(BaseSysConf.TOTAL_PAGE, hits.getMaxScore());
        // 返回当前页大小
        map.put(BaseSysConf.PAGE_SIZE, size);
        // 返回当前页
        map.put(BaseSysConf.CURRENT_PAGE, startRow);
        // 返回数据
        map.put(BaseSysConf.BLOG_LIST, blogList);
        return map;
    }
}
