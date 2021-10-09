package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.Picture;
import com.blog.business.web.domain.PictureSort;
import com.blog.business.web.mapper.PictureSortMapper;
import com.blog.business.web.service.PictureService;
import com.blog.business.web.service.PictureSortService;
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
public class PictureSortServiceImpl extends ServiceImpl<PictureSortMapper, PictureSort> implements PictureSortService {
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private WebUtils webUtils;

    @Override
    public IPage<PictureSort> getPageList(PictureSortVO pictureSortVO) {
        // 注入分页参数
        IPage<PictureSort> page = new Page<>();
        page.setSize(pictureSortVO.getPageSize());
        page.setCurrent(pictureSortVO.getCurrentPage());
        // 查询数据
        IPage<PictureSort> pageList = baseMapper.getPageList(page, pictureSortVO);
        // 获取真实数据
        List<PictureSort> list = pageList.getRecords();
        // 拼接图片地址
        StringBuilder fileUid = new StringBuilder();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUid.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> picture = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUid)) {
            picture = pictureFeignClient.getPicture(fileUid.toString(),
                    BaseSysConf.FILE_SEGMENTATION);
        }

        Map<String, String> pictureMap = new HashMap<>();
        picture = webUtils.getPictureMap(picture);
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });

        for (PictureSort item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,
                        item.getFileUid());
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(pictureItem -> {
                    pictureListTemp.add(pictureMap.get(pictureItem));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public ResultBody add(PictureSortVO pictureSortVO) {
        PictureSort pictureSort = new PictureSort();
        pictureSort.setName(pictureSortVO.getName());
        pictureSort.setParentUid(pictureSortVO.getParentUid());
        pictureSort.setSort(pictureSortVO.getSort());
        pictureSort.setFileUid(pictureSortVO.getFileUid());
        pictureSort.setStatus(EnumsStatus.ENABLE);
        pictureSort.setIsShow(pictureSortVO.getIsShow());
        pictureSort.setUpdateTime(new Date());
        this.save(pictureSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(PictureSortVO pictureSortVO) {
        PictureSort pictureSort = this.getById(pictureSortVO.getUid());
        if (StringUtils.isNull(pictureSort)) {
            return ResultBody.error("操作失败");
        }
        pictureSort.setName(pictureSortVO.getName());
        pictureSort.setParentUid(pictureSortVO.getParentUid());
        pictureSort.setSort(pictureSortVO.getSort());
        pictureSort.setFileUid(pictureSortVO.getFileUid());
        pictureSort.setIsShow(pictureSortVO.getIsShow());
        pictureSort.setUpdateTime(new Date());
        this.updateById(pictureSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(PictureSortVO pictureSortVO) {
        // 判断要删除的分类，是否有图片
        LambdaQueryWrapper<Picture> pictureWrapper = new LambdaQueryWrapper<>();
        pictureWrapper.eq(Picture::getStatus, EnumsStatus.ENABLE);
        pictureWrapper.eq(Picture::getPictureSortUid, pictureSortVO.getUid());
        int count = pictureService.count(pictureWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.PICTURE_UNDER_THIS_SORT);
        }
        PictureSort pictureSort = this.getById(pictureSortVO.getUid());
        pictureSort.setStatus(EnumsStatus.DISABLED);
        pictureSort.setUpdateTime(new Date());
        this.updateById(pictureSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody stick(PictureSortVO pictureSortVO) {
        PictureSort pictureSort = this.getById(pictureSortVO.getUid());
        if (StringUtils.isNull(pictureSort)) {
            return ResultBody.error("操作失败");
        }
        // 查找出分类中最大的一个数据
        LambdaQueryWrapper<PictureSort> pictureSortWrapper = new LambdaQueryWrapper<>();
        pictureSortWrapper.orderByDesc(PictureSort::getSort);
        pictureSortWrapper.last(BaseSysConf.LIMIT_ONE);
        PictureSort maxSort = this.getOne(pictureSortWrapper);
        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        if (maxSort.getUid().equals(pictureSort.getUid())) {
            return ResultBody.error(BaseMessageConf.THIS_SORT_IS_TOP);
        }
        int sortCount = maxSort.getSort() + 1;
        pictureSort.setSort(sortCount);
        pictureSort.setUpdateTime(new Date());
        this.updateById(pictureSort);
        return ResultBody.success();
    }

    @Override
    public ResultBody getPictureSortByUid(PictureSortVO pictureSortVO) {
        return ResultBody.success(this.getById(pictureSortVO.getUid()));
    }
}
