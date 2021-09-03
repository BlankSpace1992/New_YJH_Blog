package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.vo.SubjectVO;
import com.blog.business.web.mapper.SubjectMapper;
import com.blog.business.web.service.SubjectService;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
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
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Autowired
    private PictureFeignClient pictureFeignClient;

    @Override
    public IPage<Subject> getList(SubjectVO subjectVO) {
        // 查询主题信息
        LambdaQueryWrapper<Subject> subjectWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(subjectVO.getKeyword())) {
            subjectWrapper.like(Subject::getSubjectName, subjectVO.getKeyword().trim());
        }
        subjectWrapper.eq(Subject::getStatus, EnumsStatus.ENABLE);
        subjectWrapper.orderByDesc(Subject::getSort);
        // 设置分页参数
        Page<Subject> page = new Page<>();
        page.setCurrent(subjectVO.getCurrentPage());
        page.setSize(subjectVO.getPageSize());
        IPage<Subject> pageList = this.page(page, subjectWrapper);
        // 获取实际数据
        List<Subject> subjectList = pageList.getRecords();
        // 处理图片问题
        final StringBuffer fileUids = new StringBuffer();
        subjectList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureListMap = this.pictureFeignClient.getPicture(fileUids.toString(),
                BaseSysConf.FILE_SEGMENTATION);

        Map<String, String> pictureMap = new HashMap<>();
        pictureListMap.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        for (Subject item : subjectList) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidTemp = StringUtils.stringToList(item.getFileUid(), BaseSysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        pageList.setRecords(subjectList);
        return pageList;
    }
}
