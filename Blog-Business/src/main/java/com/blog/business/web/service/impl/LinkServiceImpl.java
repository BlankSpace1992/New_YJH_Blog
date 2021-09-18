package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Link;
import com.blog.business.web.mapper.LinkMapper;
import com.blog.business.web.service.LinkService;
import com.blog.business.web.service.SysParamsService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.constants.EnumsStatus;
import com.blog.exception.CommonErrorException;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<Link> getLink() {
        // 获取系统参数中配置得友情链接数量
        int friendLinkCount = Integer.parseInt(sysParamsService.getSysParamsValueByKey(BaseSysConf.FRIENDLY_LINK_COUNT));
        // 优先从redis中获取数据
        String redisKey = BaseRedisConf.BLOG_LINK + Constants.SYMBOL_COLON + friendLinkCount;
        String result = (String) redisUtil.get(redisKey);
        IPage<Link> page = new Page<>();
        // 判断redis中否有数据
        if (StringUtils.isNotEmpty(redisKey)) {
            List<Link> links =
                    JSON.parseArray(result, Link.class);
            page.setRecords(links);
            return page;
        }
        // 从数据库获取数据
        page.setCurrent(1);
        page.setSize(friendLinkCount);
        baseMapper.getLink(page, EnumsStatus.ENABLE, EnumsStatus.PUBLISH);
        // 获取连接集合
        List<Link> linkList = page.getRecords();
        if (StringUtils.isNotEmpty(linkList)) {
            redisUtil.set(redisKey, JSON.toJSONString(linkList), 3600);
        }
        return page;
    }

    @Override
    public void addLinkCount(String uid) {
        // 获取连接信息
        Link link = Optional.ofNullable(this.getById(uid)).orElseThrow(() -> new CommonErrorException("当前友情链接已不存在"));
        // 获取最新点击数
        Integer clickCount = link.getClickCount() + 1;
        // 更新数据
        link.setClickCount(clickCount);
        this.updateById(link);
    }
}
