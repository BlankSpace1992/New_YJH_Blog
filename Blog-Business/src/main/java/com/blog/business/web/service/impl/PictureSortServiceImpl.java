package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.PictureSortVO;
import com.blog.business.web.domain.PictureSort;
import com.blog.business.web.mapper.PictureSortMapper;
import com.blog.business.web.service.PictureSortService;
import com.blog.constants.BaseSysConf;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class PictureSortServiceImpl extends ServiceImpl<PictureSortMapper, PictureSort> implements PictureSortService {
    @Autowired
    private PictureFeignClient pictureFeignClient;

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
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });

        for (PictureSort item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.stringToList(BaseSysConf.FILE_SEGMENTATION,item.getFileUid());
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
}
