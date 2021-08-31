package com.blog.business.search.mapper;

import com.blog.business.search.domain.ElasticSearchVO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yujunhong
 * @date 2021/8/31 13:42
 */
public interface BlogSearchMapper extends ElasticsearchRepository<ElasticSearchVO,String> {
}
