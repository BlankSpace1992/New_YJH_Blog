package com.blog.business.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.mapper.SystemConfigMapper;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public SystemConfig getsSystemConfig() {
        // 优先从redis获取数据
        String result = (String) redisUtil.get(BaseRedisConf.SYSTEM_CONFIG);
        // 判断redis中是否存在资源
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, SystemConfig.class);
        }
        // 从数据库中获取数据
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getStatus, EnumsStatus.ENABLE);
        wrapper.orderByDesc(SystemConfig::getCreateTime);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        SystemConfig systemConfig = this.getOne(wrapper);
        // 将数据缓存进redis
        if (StringUtils.isNotNull(systemConfig)) {
            redisUtil.set(BaseRedisConf.SYSTEM_CONFIG, JSON.toJSONString(systemConfig), 86400);
        }
        return systemConfig;
    }
}
