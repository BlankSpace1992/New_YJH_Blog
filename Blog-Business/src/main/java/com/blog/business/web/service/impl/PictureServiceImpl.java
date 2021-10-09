package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.Blog;
import com.blog.business.web.domain.Picture;
import com.blog.business.web.domain.PictureSort;
import com.blog.business.web.mapper.PictureMapper;
import com.blog.business.web.service.BlogService;
import com.blog.business.web.service.PictureService;
import com.blog.business.web.service.PictureSortService;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
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
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private BlogService blogService;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;
    @Autowired
    private PictureSortService pictureSortService;
    @Autowired
    private WebUtils webUtils;

    @Override
    public IPage<Picture> getPageList(PictureVO pictureVO) {
        // 注入分页参数
        IPage<Picture> page = new Page<>();
        page.setCurrent(pictureVO.getCurrentPage());
        page.setSize(pictureVO.getPageSize());
        // 查询数据
        IPage<Picture> pageList = baseMapper.getPageList(page, pictureVO);
        // 获取真实数据
        List<Picture> pictureList = pageList.getRecords();
        // 拼接图片地址
        StringBuilder fileUid = new StringBuilder();
        pictureList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUid.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> picture = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUid)) {
            picture = pictureFeignClient.getPicture(fileUid.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }
        picture = webUtils.getPictureMap(picture);
        Map<String, String> pictureMap = new HashMap<>();
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        for (Picture item : pictureList) {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                item.setPictureUrl(pictureMap.get(item.getFileUid()));
            }
        }
        pageList.setRecords(pictureList);
        return pageList;
    }

    @Override
    public ResultBody add(List<PictureVO> pictureVOList) {
        List<Picture> pictureList = new ArrayList<>();
        for (PictureVO pictureVO : pictureVOList) {
            Picture picture = new Picture();
            picture.setFileUid(pictureVO.getFileUid());
            picture.setPictureSortUid(pictureVO.getPictureSortUid());
            picture.setPicName(pictureVO.getPicName());
            picture.setStatus(EnumsStatus.ENABLE);
            pictureList.add(picture);
        }
        this.saveBatch(pictureList);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(PictureVO pictureVO) {
        Picture picture = this.getById(pictureVO.getUid());
        if (StringUtils.isNull(picture)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // 这里需要更新所有的博客，将图片替换成 裁剪的图片
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getStatus, EnumsStatus.ENABLE);
        blogWrapper.eq(Blog::getFileUid, pictureVO.getFileUid());
        List<Blog> blogList = blogService.list(blogWrapper);
        if (blogList.size() > 0) {
            blogList.forEach(item -> {
                item.setFileUid(pictureVO.getFileUid());
            });
            blogService.updateBatchById(blogList);
            // // TODO: 2021/10/8 处理redis信息
         /*   Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.EDIT_BATCH);

            //发送到RabbitMq
            rabbitMqUtils.sendSimpleEmail(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);*/
        }
        picture.setFileUid(pictureVO.getFileUid());
        picture.setPicName(pictureVO.getPicName());
        picture.setPictureSortUid(pictureVO.getPictureSortUid());
        picture.setUpdateTime(new Date());
        this.updateById(picture);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(PictureVO pictureVO) {
        // 图片删除的时候，是携带多个id拼接而成的
        String uidStr = pictureVO.getUid();
        if (StringUtils.isEmpty(uidStr)) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION, pictureVO.getUid());
        for (String item : uidList) {
            Picture picture = this.getById(item);
            picture.setStatus(EnumsStatus.DISABLED);
            picture.setUpdateTime(new Date());
            this.updateById(picture);
        }
        return ResultBody.success();
    }

    @Override
    public ResultBody setCover(PictureVO pictureVO) {
        PictureSort pictureSort = pictureSortService.getById(pictureVO.getPictureSortUid());
        if (StringUtils.isNull(pictureSort)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        Picture picture = this.getById(pictureVO.getUid());
        if (StringUtils.isNull(picture)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        pictureSort.setFileUid(picture.getFileUid());
        pictureSort.setUpdateTime(new Date());
        pictureSortService.updateById(pictureSort);
        return ResultBody.success();
    }
}
