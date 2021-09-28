package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictData;
import com.blog.business.web.domain.SysDictType;
import com.blog.business.web.mapper.SysDictTypeMapper;
import com.blog.business.web.service.SysDictDataService;
import com.blog.business.web.service.SysDictTypeService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysDictDataService sysDictDataService;

    @Override
    public IPage<SysDictType> getPageList(SysDictTypeVO sysDictTypeVO) {
        // 注入分页参数
        IPage<SysDictType> page = new Page<>();
        page.setCurrent(sysDictTypeVO.getCurrentPage());
        page.setSize(sysDictTypeVO.getPageSize());

        return baseMapper.getPageList(page, sysDictTypeVO);
    }

    @Override
    public ResultBody add(SysDictTypeVO sysDictTypeVO) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "新增字典类型失败"));
        // 判断当前字典类型是否存在
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, sysDictTypeVO.getDictType());
        wrapper.eq(SysDictType::getStatus, EnumsStatus.ENABLE);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        SysDictType dictTypeTemp = this.getOne(wrapper);
        if (StringUtils.isNotNull(dictTypeTemp)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        SysDictType sysDictType = new SysDictType();
        sysDictType.setDictName(sysDictTypeVO.getDictName());
        sysDictType.setDictType(sysDictTypeVO.getDictType());
        sysDictType.setRemark(sysDictTypeVO.getRemark());
        sysDictType.setIsPublish(sysDictTypeVO.getIsPublish());
        sysDictType.setSort(sysDictTypeVO.getSort());
        sysDictType.setCreateByUid(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        sysDictType.setUpdateByUid(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        this.save(sysDictType);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(SysDictTypeVO sysDictTypeVO) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "编辑字典类型失败"));
        SysDictType sysDictType = this.getById(sysDictTypeVO.getUid());
        if (StringUtils.isNull(sysDictType)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        // 判断编辑的字典类型是否存在
        if (!sysDictType.getDictType().equals(sysDictTypeVO.getDictType())) {
            LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDictType::getDictType, sysDictTypeVO.getDictType());
            queryWrapper.eq(SysDictType::getStatus, EnumsStatus.ENABLE);
            queryWrapper.last(BaseSysConf.LIMIT_ONE);
            SysDictType temp = this.getOne(queryWrapper);
            if (StringUtils.isNotNull(temp)) {
                return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
            }
        }
        sysDictType.setDictName(sysDictTypeVO.getDictName());
        sysDictType.setDictType(sysDictTypeVO.getDictType());
        sysDictType.setRemark(sysDictTypeVO.getRemark());
        sysDictType.setIsPublish(sysDictTypeVO.getIsPublish());
        sysDictType.setSort(sysDictTypeVO.getSort());
        sysDictType.setUpdateByUid(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
        sysDictType.setUpdateTime(new Date());
        this.updateById(sysDictType);
        // 获取Redis中特定前缀
        Set<Object> keys = redisUtil.keys(BaseSysConf.REDIS_DICT_TYPE + BaseSysConf.REDIS_SEGMENTATION + "*");
        redisUtil.delete(keys);
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<SysDictTypeVO> sysDictTypeVoList) {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "删除字典类型失败"));
        String adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        // 获取删除的字典类型的uid
        List<String> uidList = sysDictTypeVoList.stream().map(SysDictTypeVO::getUid).collect(Collectors.toList());
        // 判断要删除的分类是否有博客
        LambdaQueryWrapper<SysDictData> sysDictDataWrapper = new LambdaQueryWrapper<>();
        sysDictDataWrapper.eq(SysDictData::getStatus, EnumsStatus.ENABLE);
        sysDictDataWrapper.in(SysDictData::getDictTypeUid, uidList);
        int count = sysDictDataService.count(sysDictDataWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.DICT_DATA_UNDER_THIS_SORT);
        }
        List<SysDictType> sysDictTypes = this.listByIds(uidList);
        sysDictTypes.forEach(item -> {
            item.setStatus(EnumsStatus.DISABLED);
            item.setUpdateByUid(adminUid);
        });
        this.updateBatchById(sysDictTypes);
        // 获取Redis中特定前缀
        Set<Object> keys = redisUtil.keys(BaseSysConf.REDIS_DICT_TYPE + BaseSysConf.REDIS_SEGMENTATION + "*");
        redisUtil.delete(keys);
        return ResultBody.success();
    }
}
