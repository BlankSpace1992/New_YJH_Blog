package com.blog.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * @author yujunhong
 * @date 2021/11/1 10:52
 */
@Configuration
public class ElasticSearchConfig {
    /**
     * http连接实体
     */
    private ArrayList<HttpHost> hostList;
    /**
     * 端口号
     */
    @Value(value = "${elasticsearch.port}")
    private Integer port;
    /**
     * ip地址,按照逗号隔开
     */
    @Value(value = "${elasticsearch.ip}")
    private String hosts;
    /**
     * 连接超时时间
     */
    @Value("${elasticsearch.connectTimeOut}")
    private static int connectTimeOut;
    /**
     * 连接超时时间
     */
    @Value("${elasticsearch.socketTimeOut}")
    private static int socketTimeOut;
    /**
     * 获取连接的超时时间
     */
    @Value("${elasticsearch.connectionRequestTimeOut}")
    private int connectionRequestTimeOut;
    /**
     * 最大连接数
     */
    @Value("${elasticsearch.maxConnectNum}")
    private int maxConnectNum;
    /**
     * 最大路由连接数
     */
    @Value("${elasticsearch.maxConnectPerRoute}")
    private int maxConnectPerRoute;

    /**
     * 初始化连接
     *
     * @author yujunhong
     * @date 2021/11/1 10:54
     */
    @PostConstruct
    public void init() {
        hostList = new ArrayList<>();
        String[] hostArray = hosts.split(",");
        for (String host : hostArray) {
            // 使用的协议
            String schema = "http";
            hostList.add(new HttpHost(host, port, schema));
        }
    }

    /**
     * 构造Bean,返回RestHighLevelClient 客户端
     *
     * @return RestHighLevelClient 客户端
     * @author yujunhong
     * @date 2021/11/1 10:56
     */
    @Bean
    public RestHighLevelClient client() {
        // 默认使用第一个ip地址
        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[0]));
        // 异步httpclient连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeOut);
            requestConfigBuilder.setSocketTimeout(socketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
            return requestConfigBuilder;
        });
        // 异步httpclient连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
