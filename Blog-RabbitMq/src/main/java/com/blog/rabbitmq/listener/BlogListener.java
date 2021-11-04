package com.blog.rabbitmq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.feign.SearchFeignClient;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/11/4 14:21
 */
@Component
@Slf4j
public class BlogListener {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SearchFeignClient searchFeignClient;


    /**
     * 在这里同时需要对Redis和ElasticSearch进行操作，同时利用MQ来保证数据一致性
     *
     * @param result 数据信息
     * @author yujunhong
     * @date 2021/11/4 14:23
     */
    @RabbitListener(queues = BaseSysConf.CLOUD_BLOG)
    public void updateRedisAndElasticSearch(String result) {
        Map<String, Object> map = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });
        if (StringUtils.isNotNull(map)) {
            String command = (String) map.get(BaseSysConf.COMMAND);
            String uid = (String) map.get(BaseSysConf.BLOG_UID);

            //从Redis清空对应的数据
            redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE);
            redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO);
            redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_THREE);
            redisUtil.delete(BaseRedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_FOUR);
            redisUtil.delete(BaseRedisConf.HOT_BLOG);
            redisUtil.delete(BaseRedisConf.NEW_BLOG);
            redisUtil.delete(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_CONTRIBUTE_COUNT);
            redisUtil.delete(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_SORT);
            redisUtil.delete(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.BLOG_COUNT_BY_TAG);

            switch (command) {
                case BaseSysConf.DELETE_BATCH: {
                    log.info("rabbitMq处理批量删除博客");
                    redisUtil.set(BaseRedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
                    redisUtil.set(BaseRedisConf.MONTH_SET, "");
                    // 删除ElasticSearch博客索引
                    searchFeignClient.deleteElasticSearchByUids(uid);
                }
                break;
                case BaseSysConf.EDIT_BATCH: {
                    log.info("rabbitMq处理批量编辑博客");
                    redisUtil.set(BaseRedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
                    redisUtil.set(BaseRedisConf.MONTH_SET, "");

                }
                break;
                case BaseSysConf.ADD: {
                    log.info("rabbitMq处理增加博客");
                    updateRedis(map);
                    // 增加ES索引
                    searchFeignClient.addElasticSearchIndexByUid(uid);
                }
                break;
                case BaseSysConf.EDIT: {
                    log.info("rabbitMq处理编辑博客");
                    updateRedis(map);
                    // 更新ES索引
                    searchFeignClient.addElasticSearchIndexByUid(uid);
                }
                break;

                case BaseSysConf.DELETE: {
                    log.info("rabbitMq处理删除博客: uid:" + uid);
                    updateRedis(map);
                    // 删除ES索引
                    searchFeignClient.deleteElasticSearchByUid(uid);
                }
                break;
                default: {
                    log.info("无博客处理");
                }
            }
        }
    }

    /**
     * 处理redis缓存得年月份归档数据
     *
     * @param map 数据
     * @author yujunhong
     * @date 2021/11/4 14:30
     */
    private void updateRedis(Map<String, Object> map) {
        try {
            String createTime = (String) map.get(BaseSysConf.CREATE_TIME);
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(createTime))));
            String[] list = sd.split(Constants.SYMBOL_HYPHEN);
            String year = list[0];
            String month = list[1];
            String key = year + "年" + month + "月";
            redisUtil.delete(BaseRedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON + key);
            String jsonResult = (String) redisUtil.get(BaseRedisConf.MONTH_SET);
            List<String> monthSet = JSON.parseArray(jsonResult, String.class);
            boolean haveMonth = false;
            if (monthSet != null) {
                for (String item : monthSet) {
                    if (item.equals(key)) {
                        haveMonth = true;
                        break;
                    }
                }
                if (!haveMonth) {
                    monthSet.add(key);
                    redisUtil.set(BaseRedisConf.MONTH_SET, JSON.toJSONString(monthSet));
                }
            }

        } catch (Exception e) {
            log.error("更新Redis失败");
        }
    }
}
