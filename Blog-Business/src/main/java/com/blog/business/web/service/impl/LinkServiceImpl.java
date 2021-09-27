package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.LinkVO;
import com.blog.business.web.domain.Link;
import com.blog.business.web.mapper.LinkMapper;
import com.blog.business.web.service.LinkService;
import com.blog.business.web.service.SysParamsService;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;

    @Override
    public IPage<Link> getLink() {
        // 获取系统参数中配置得友情链接数量
        int friendLinkCount =
                Integer.parseInt(sysParamsService.getSysParamsValueByKey(BaseSysConf.FRIENDLY_LINK_COUNT));
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

    @Override
    public IPage<Link> getLinkList(LinkVO linkVO) {
        // 注入分页参数
        IPage<Link> page = new Page<>();
        page.setCurrent(linkVO.getCurrentPage());
        page.setSize(linkVO.getPageSize());
        IPage<Link> linkPageList = baseMapper.getLinkList(page, linkVO);
        // 获取实际数据
        List<Link> linkList = linkPageList.getRecords();
        // 拼接图片
        StringBuilder fileIdBuilder = new StringBuilder();
        // 给友情链接添加图片
        linkList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileIdBuilder.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureList = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileIdBuilder)) {
            pictureList = pictureFeignClient.getPicture(fileIdBuilder.toString(), BaseSysConf.FILE_SEGMENTATION);
        }
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        for (Link item : linkList) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidListTemp = StringUtils.stringToList(Constants.SYMBOL_COMMA, item.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidListTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        linkPageList.setRecords(linkList);
        return linkPageList;
    }

    @Override
    public ResultBody add(LinkVO linkVO) {
        Link link = new Link();
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setUrl(linkVO.getUrl());
        link.setClickCount(0);
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setSort(linkVO.getSort());
        link.setEmail(linkVO.getEmail());
        link.setFileUid(linkVO.getFileUid());
        link.setStatus(EnumsStatus.ENABLE);
        this.save(link);
        // 友链从申请状态到发布状态，需要发送邮件到站长邮箱
        if (StringUtils.isNotEmpty(link.getEmail())) {
            String linkApplyText = "<a href=\" " + link.getUrl() + "\">" + link.getTitle() + "</a> 站长，您申请的友链已经成功上架~";
            rabbitMqUtils.sendSimpleEmail(link.getEmail(), linkApplyText);
        }
        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(LinkVO linkVO) {
        Link link = this.getById(linkVO.getUid());
        if (StringUtils.isNull(link)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        Integer linkStatus = link.getLinkStatus();
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setUrl(linkVO.getUrl());
        link.setSort(linkVO.getSort());
        link.setEmail(linkVO.getEmail());
        link.setFileUid(linkVO.getFileUid());
        link.setUpdateTime(new Date());
        this.updateById(link);
        // 友链从申请状态到发布状态，需要发送邮件到站长邮箱
        if (StringUtils.isNotEmpty(link.getEmail())) {
            if (EnumLinkStatus.APPLY.equals(linkStatus) && EnumLinkStatus.PUBLISH.equals(linkVO.getLinkStatus())) {
                String linkApplyText = "<a href=\" " + link.getUrl() + "\">" + link.getTitle() + "</a> " +
                        "站长，您申请的友链已经成功上架~";
                rabbitMqUtils.sendSimpleEmail(link.getEmail(), linkApplyText);
            }
        }
        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(LinkVO linkVO) {
        Link link = this.getById(linkVO.getUid());
        if (StringUtils.isNull(link)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        link.setStatus(EnumsStatus.DISABLED);
        link.setUpdateTime(new Date());
        this.updateById(link);
        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();
        return ResultBody.success();
    }

    @Override
    public ResultBody stick(LinkVO linkVO) {
        Link link = this.getById(linkVO.getUid());
        if (StringUtils.isNull(link)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // 查询出最大的友情连接
        LambdaQueryWrapper<Link> linkWrapper = new LambdaQueryWrapper<>();
        linkWrapper.orderByDesc(Link::getSort);
        linkWrapper.last(BaseSysConf.LIMIT_ONE);
        Link maxSortLink = this.getOne(linkWrapper);
        if (StringUtils.isEmpty(maxSortLink.getUid())) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        if (maxSortLink.getUid().equals(link.getUid())) {
            return ResultBody.error(BaseMessageConf.OPERATION_FAIL);
        }
        Integer sortCount = maxSortLink.getSort() + 1;
        link.setSort(sortCount);
        link.setUpdateTime(new Date());
        this.updateById(link);
        // 删除Redis中的BLOG_LINK
        deleteRedisBlogLinkList();
        return ResultBody.success();
    }

    /**
     * 删除Redis中的友链列表
     *
     * @author yujunhong
     * @date 2021/9/27 15:19
     */
    private void deleteRedisBlogLinkList() {
        // 删除Redis中的BLOG_LINK
        Set<Object> keys = redisUtil.keys(BaseRedisConf.BLOG_LINK + Constants.SYMBOL_COLON + "*");
        redisUtil.delete(keys);
    }
}
