package com.blog.business.picture.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.Storage;
import com.blog.business.picture.mapper.StorageMapper;
import com.blog.business.picture.service.StorageService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

    @Override
    public ResultBody initStorageSize(String adminUid, Long maxStorageSize) {
        // ��ѯ��ǰ�û��Ƿ��Ѿ���ʼ��
        LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
        storageWrapper.eq(Storage::getAdminUid, adminUid);
        Storage storage = this.getOne(storageWrapper);
        // �����Ϊ��
        if (StringUtils.isNotNull(storage)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Storage saveStorage = new Storage();
        saveStorage.setAdminUid(adminUid);
        saveStorage.setStorageSize(0L);
        saveStorage.setMaxStorageSize(maxStorageSize);
        return ResultBody.success();
    }

    @Override
    public ResultBody editStorageSize(String adminUid, Long maxStorageSize) {
        // ��ѯ��ǰ�û��Ƿ��Ѿ���ʼ��
        LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
        storageWrapper.eq(Storage::getAdminUid, adminUid);
        Storage storage = this.getOne(storageWrapper);
        // �����Ϊ��
        if (StringUtils.isNull(storage)) {
            return this.initStorageSize(adminUid, maxStorageSize);
        }
        if (maxStorageSize < storage.getStorageSize()) {
            return ResultBody.error("������������С�ڵ�ǰ���ÿռ�");
        }
        storage.setMaxStorageSize(maxStorageSize);
        this.updateById(storage);
        return ResultBody.success();
    }

    @Override
    public ResultBody getStorageByAdminUid(List<String> adminUidList) {
        LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
        storageWrapper.in(Storage::getAdminUid, adminUidList);
        storageWrapper.eq(Storage::getStatus, EnumsStatus.ENABLE);
        return ResultBody.success(JSON.toJSONString(this.list(storageWrapper)));
    }
}
