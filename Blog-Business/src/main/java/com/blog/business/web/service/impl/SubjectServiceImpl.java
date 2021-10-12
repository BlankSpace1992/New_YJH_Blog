package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.Subject;
import com.blog.business.web.domain.SubjectItem;
import com.blog.business.web.domain.vo.SubjectVO;
import com.blog.business.web.mapper.SubjectMapper;
import com.blog.business.web.service.SubjectItemService;
import com.blog.business.web.service.SubjectService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private SubjectItemService subjectItemService;

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
                List<String> pictureUidTemp = StringUtils.stringToList(item.getFileUid(),
                        BaseSysConf.FILE_SEGMENTATION);
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

    @Override
    public ResultBody add(SubjectVO subjectVO) {
        // 判断当前分类名称是否已经存在
        LambdaQueryWrapper<Subject> subjectWrapper = new LambdaQueryWrapper<>();
        subjectWrapper.eq(Subject::getSubjectName, subjectVO.getSubjectName());
        subjectWrapper.eq(Subject::getStatus, EnumsStatus.ENABLE);
        subjectWrapper.last(BaseSysConf.LIMIT_ONE);
        Subject subjectTemp = this.getOne(subjectWrapper);
        if (StringUtils.isNotNull(subjectTemp)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectVO.getSubjectName());
        subject.setSummary(subjectVO.getSummary());
        subject.setFileUid(subjectVO.getFileUid());
        subject.setClickCount(subjectVO.getClickCount());
        subject.setCollectCount(subjectVO.getCollectCount());
        subject.setSort(subjectVO.getSort());
        subject.setStatus(EnumsStatus.ENABLE);
        this.save(subject);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(SubjectVO subjectVO) {
        // 判断当前专题是否存在
        Subject subject = this.getById(subjectVO.getUid());
        if (StringUtils.isNull(subject)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // 判断当前主题名称是否已经存在
        if (!subject.getSubjectName().equals(subjectVO.getSubjectName())) {
            LambdaQueryWrapper<Subject> subjectWrapper = new LambdaQueryWrapper<>();
            subjectWrapper.eq(Subject::getSubjectName, subjectVO.getSubjectName());
            subjectWrapper.eq(Subject::getStatus, EnumsStatus.ENABLE);
            subjectWrapper.last(BaseSysConf.LIMIT_ONE);
            Subject subjectTemp = this.getOne(subjectWrapper);
            if (StringUtils.isNotNull(subjectTemp)) {
                return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
            }
        }
        subject.setSubjectName(subjectVO.getSubjectName());
        subject.setSummary(subjectVO.getSummary());
        subject.setFileUid(subjectVO.getFileUid());
        subject.setClickCount(subjectVO.getClickCount());
        subject.setCollectCount(subjectVO.getCollectCount());
        subject.setSort(subjectVO.getSort());
        subject.setStatus(EnumsStatus.ENABLE);
        this.updateById(subject);
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<SubjectVO> subjectVOList) {
        // 获取主键集合
        List<String> uidList = subjectVOList.stream().map(SubjectVO::getUid).collect(Collectors.toList());
        // 判断要删除的主题，是否有资源
        LambdaQueryWrapper<SubjectItem> subjectItemWrapper = new LambdaQueryWrapper<>();
        subjectItemWrapper.eq(SubjectItem::getStatus, EnumsStatus.ENABLE);
        subjectItemWrapper.in(SubjectItem::getUid, uidList);
        int count = subjectItemService.count(subjectItemWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.SUBJECT_UNDER_THIS_SORT);
        }
        // 获取所有的主题
        List<Subject> subjects = this.listByIds(uidList);
        subjects.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EnumsStatus.DISABLED);
        });
        this.updateBatchById(subjects);
        return ResultBody.success();
    }
}
