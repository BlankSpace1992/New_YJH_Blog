package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.PictureVO;
import com.blog.business.web.domain.Picture;
import com.blog.business.web.mapper.PictureMapper;
import com.blog.business.web.service.PictureService;
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
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    @Autowired
    private PictureFeignClient pictureFeignClient;

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
}
