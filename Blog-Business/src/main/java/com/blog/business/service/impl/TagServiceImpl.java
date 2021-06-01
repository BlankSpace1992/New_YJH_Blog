package com.blog.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.domain.Tag;
import com.blog.business.mapper.TagMapper;
import com.blog.business.service.SysParamsService;
import com.blog.business.service.TagService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.constants.EnumsStatus;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<Tag> getHotTag() {
        // 获取系统参数中配置得标签数量
        Integer hotTagCount = sysParamsService.getSysParamsValueByKey(BaseSysConf.HOT_TAG_COUNT);
        // 优先从redis中获取数据
        String redisKey = BaseRedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount;
        String result = (String) redisUtil.get(redisKey);
        IPage<Tag> page = new Page<>();
        // 判断redis中是否有值
        if (StringUtils.isNotEmpty(result)) {
            List<Tag> tags = JSON.parseArray(result, Tag.class);
            page.setRecords(tags);
            return page;
        }
        // 从数据库中获取数据
        page.setCurrent(1);
        page.setSize(hotTagCount);
        baseMapper.getHotTag(page, EnumsStatus.ENABLE);
        // 获取标签列表
        List<Tag> tagList = page.getRecords();
        if (StringUtils.isNotEmpty(tagList)) {
            redisUtil.set(redisKey, JSON.toJSONString(tagList), 3600);
        }
        return page;
    }
}
