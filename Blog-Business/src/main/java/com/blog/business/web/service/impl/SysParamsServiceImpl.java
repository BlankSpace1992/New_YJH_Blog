package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.SysParamsVO;
import com.blog.business.web.domain.SysParams;
import com.blog.business.web.mapper.SysParamsMapper;
import com.blog.business.web.service.SysParamsService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
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
public class SysParamsServiceImpl extends ServiceImpl<SysParamsMapper, SysParams> implements SysParamsService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getSysParamsValueByKey(String paramKey) {
        // 优先从redis中获取
        String redisKey = BaseRedisConf.SYSTEM_PARAMS + BaseRedisConf.SEGMENTATION + paramKey;
        String paramsValue = (String) redisUtil.get(redisKey);
        // 判断redis中是否有值
        if (StringUtils.isEmpty(paramsValue)) {
            // 如果redis中不存在则再访问数据库查询
            SysParams sysParams =
                    Optional.ofNullable(this.getSysParamsByKey(paramKey)).orElseThrow(() -> new CommonErrorException(ErrorCode.PLEASE_CONFIGURE_SYSTEM_PARAMS, BaseMessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS));
            // 获取对应的值
            paramsValue = sysParams.getParamsValue();
            // 存放进redis中
            redisUtil.set(redisKey, paramsValue, 3600);
        }
        return paramsValue;
    }

    @Override
    public SysParams getSysParamsByKey(String paramKey) {
        LambdaQueryWrapper<SysParams> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysParams::getParamsKey, paramKey);
        wrapper.eq(SysParams::getStatus, EnumsStatus.ENABLE);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        return this.getOne(wrapper);
    }

    @Override
    public IPage<SysParams> getSysParamsPageList(SysParamsVO sysParamsVO) {
        // 注入分页参数
        IPage<SysParams> page = new Page<>();
        page.setCurrent(sysParamsVO.getCurrentPage());
        page.setSize(sysParamsVO.getPageSize());
        return baseMapper.getSysParamsPageList(page, sysParamsVO);
    }

    @Override
    public ResultBody add(SysParamsVO sysParamsVO) {
        // 判断当前的参数是否存在
        LambdaQueryWrapper<SysParams> sysParamsWrapper = new LambdaQueryWrapper<>();
        sysParamsWrapper.eq(SysParams::getParamsKey, sysParamsVO.getParamsKey());
        sysParamsWrapper.eq(SysParams::getStatus, EnumsStatus.ENABLE);
        sysParamsWrapper.last(BaseSysConf.LIMIT_ONE);
        SysParams sysParamsTemp = this.getOne(sysParamsWrapper);
        if (StringUtils.isNotNull(sysParamsTemp)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        SysParams sysParams = new SysParams();
        sysParams.setParamsName(sysParamsVO.getParamsName());
        sysParams.setParamsKey(sysParamsVO.getParamsKey());
        sysParams.setParamsValue(sysParamsVO.getParamsValue());
        sysParams.setParamsType(sysParamsVO.getParamsType());
        sysParams.setRemark(sysParamsVO.getRemark());
        sysParams.setSort(sysParamsVO.getSort());
        this.save(sysParams);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(SysParamsVO sysParamsVO) {
        SysParams sysParams = this.getById(sysParamsVO.getUid());
        // 判断编辑的参数键名是否存在
        if (!sysParamsVO.getParamsKey().equals(sysParams.getParamsKey())) {
            LambdaQueryWrapper<SysParams> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysParams::getParamsKey, sysParamsVO.getParamsKey());
            queryWrapper.eq(SysParams::getStatus, EnumsStatus.ENABLE);
            queryWrapper.last(BaseSysConf.LIMIT_ONE);
            SysParams temp = this.getOne(queryWrapper);
            if (StringUtils.isNull(temp)) {
                return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
            }
        }
        sysParams.setParamsName(sysParamsVO.getParamsName());
        sysParams.setParamsKey(sysParamsVO.getParamsKey());
        sysParams.setParamsValue(sysParamsVO.getParamsValue());
        sysParams.setParamsType(sysParamsVO.getParamsType());
        sysParams.setRemark(sysParamsVO.getRemark());
        sysParams.setSort(sysParamsVO.getSort());
        sysParams.setUpdateTime(new Date());
        this.updateById(sysParams);
        // 清空Redis中存在的配置
        redisUtil.delete(BaseRedisConf.SYSTEM_PARAMS + BaseRedisConf.SEGMENTATION + sysParamsVO.getParamsKey());
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatch(List<SysParamsVO> sysParamsVoList) {
        // 获取需要删除的参数的uid
        List<String> sysParamIdList = sysParamsVoList.stream().map(SysParamsVO::getUid).collect(Collectors.toList());
        List<SysParams> sysParamsList = this.listByIds(sysParamIdList);
        // 更新完成数据库后，还需要清空Redis中的缓存，因此需要存储键值
        List<Object> redisKeys = new ArrayList<>();
        for(SysParams item : sysParamsList) {
            // 判断删除列表中是否含有系统内置参数
            if(item.getParamsType() == Constants.NUM_ONE) {
                return ResultBody.error(BaseMessageConf.SYS_PARAMS_NOT_DELETED);
            }
            item.setStatus(EnumsStatus.DISABLED);
            redisKeys.add(BaseRedisConf.SYSTEM_PARAMS + BaseRedisConf.SEGMENTATION + item.getParamsKey());
        }
        this.updateBatchById(sysParamsList);
        // 清空Redis中的配置
        redisUtil.delete(redisKeys);
        return ResultBody.success();
    }
}
