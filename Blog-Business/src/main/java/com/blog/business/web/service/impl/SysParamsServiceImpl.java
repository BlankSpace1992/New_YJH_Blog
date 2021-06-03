package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.SysParams;
import com.blog.business.web.mapper.SysParamsMapper;
import com.blog.business.web.service.SysParamsService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SysParamsServiceImpl extends ServiceImpl<SysParamsMapper, SysParams> implements SysParamsService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Integer getSysParamsValueByKey(String paramKey) {
        // 优先从redis中获取
        String redisKey = BaseRedisConf.SYSTEM_PARAMS + BaseRedisConf.SEGMENTATION + paramKey;
        String paramsValue = (String) redisUtil.get(redisKey);
        // 判断redis中是否有值
        if (StringUtils.isEmpty(paramsValue)) {
            // 如果redis中不存在则再访问数据库查询
            SysParams sysParams =
                    Optional.ofNullable(this.getSysParamsByKey(paramKey)).orElseThrow(() -> new CommonErrorException(ErrorCode.PLEASE_CONFIGURE_SYSTEM_PARAMS, BaseMessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS));
            // 获取对应的值
            paramsValue = sysParams.getParamsValue();
            // 存放进redis中
            redisUtil.set(redisKey, paramsValue,3600);
        }
        return Integer.valueOf(paramsValue);
    }

    @Override
    public SysParams getSysParamsByKey(String paramKey) {
        LambdaQueryWrapper<SysParams> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysParams::getParamsKey, paramKey);
        wrapper.eq(SysParams::getStatus, EnumsStatus.ENABLE);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        return this.getOne(wrapper);
    }
}
