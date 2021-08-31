package com.blog.business.search.helper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * ElasticSearch高亮配置
 *
 * @author yujunhong
 * @date 2021/8/31 12:01
 */
@Component
@Slf4j
public class HighlightResultHelper implements SearchResultMapper {
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
        List<T> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            if (hit != null) {
                T result = null;
                if (StringUtils.hasText(hit.getSourceAsString())) {
                    result = JSONObject.parseObject(hit.getSourceAsString(), aClass);
                }
                // 高亮查询
                for (HighlightField field : hit.getHighlightFields().values()) {
                    try {
                        PropertyUtils.setProperty(result, field.getName(), concat(field.fragments()));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("设置高亮字段异常：{}", e.getMessage(), e);
                    }
                }
                results.add(result);
            }
        }
        return new AggregatedPageImpl<T>(results, pageable, searchResponse.getHits().getTotalHits(), searchResponse
                .getAggregations(), searchResponse.getScrollId());
    }

    @Override
    public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
        return null;
    }

    private String concat(Text[] texts) {
        StringBuffer sb = new StringBuffer();
        for (Text text : texts) {
            sb.append(text.toString());
        }
        return sb.toString();
    }
}
