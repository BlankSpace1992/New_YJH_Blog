package com.blog.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.domain.WebConfig;
import com.blog.business.mapper.WebConfigMapper;
import com.blog.business.service.WebConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public WebConfig getWebConfigByShowList() {
        // 优先从redis中获取Ip资源
        String result = (String) redisUtil.get(BaseRedisConf.WEB_CONFIG);
        // 判断redis中是否存在资源
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, WebConfig.class);
        }
        // 从数据库中获取数据
        WebConfig webConfig = this.list().get(0);
        // TODO: 2021/6/2  图片尚未处理
        return webConfig;
    }
}
