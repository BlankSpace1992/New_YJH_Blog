package com.blog.business.picture.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.File;
import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.domain.Storage;
import com.blog.business.picture.mapper.StorageMapper;
import com.blog.business.picture.service.FileService;
import com.blog.business.picture.service.NetworkDiskService;
import com.blog.business.picture.service.StorageService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.entity.SystemConfigCommon;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.FeignUtils;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {
    @Autowired
    private FeignUtils feignUtils;
    @Autowired
    private FileService fileService;
    @Autowired
    private NetworkDiskService networkDiskService;

    @Override
    public ResultBody initStorageSize(String adminUid, Long maxStorageSize) {
        // 查询当前用户是否已经初始化
        LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
        storageWrapper.eq(Storage::getAdminUid, adminUid);
        Storage storage = this.getOne(storageWrapper);
        // 如果不为空
        if (StringUtils.isNotNull(storage)) {
            return ResultBody.error(BaseMessageConf.ENTITY_EXIST);
        }
        Storage saveStorage = new Storage();
        saveStorage.setAdminUid(adminUid);
        saveStorage.setStorageSize(0L);
        saveStorage.setMaxStorageSize(maxStorageSize);
        this.save(saveStorage);
        return ResultBody.success();
    }

    @Override
    public ResultBody editStorageSize(String adminUid, Long maxStorageSize) {
        // 查询当前用户是否已经初始化
        LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
        storageWrapper.eq(Storage::getAdminUid, adminUid);
        Storage storage = this.getOne(storageWrapper);
        // 如果不为空
        if (StringUtils.isNull(storage)) {
            return this.initStorageSize(adminUid, maxStorageSize);
        }
        if (maxStorageSize < storage.getStorageSize()) {
            return ResultBody.error("网盘容量不能小于当前已用空间");
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

    @Override
    public Storage getStorageByAdmin() {
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取用户存储信息失败"));
        String adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        if (StringUtils.isNotEmpty(adminUid)) {
            LambdaQueryWrapper<Storage> storageWrapper = new LambdaQueryWrapper<>();
            storageWrapper.eq(Storage::getStatus, EnumsStatus.ENABLE);
            storageWrapper.eq(Storage::getAdminUid, adminUid);
            storageWrapper.last(BaseSysConf.LIMIT_ONE);
            return this.getOne(storageWrapper);
        }
        return null;
    }

    @Override
    public ResultBody uploadFile(HttpServletRequest request, NetworkDisk networkDisk,
                                 List<MultipartFile> multipartFiles) {
        SystemConfigCommon systemConfig = feignUtils.getSystemConfig();
        if (StringUtils.isNull(systemConfig)) {
            return ResultBody.error(BaseMessageConf.SYSTEM_CONFIG_NOT_EXIST);
        }
        // 计算文件大小
        long newStorageSize = 0L;
        long storageSize = 0L;
        for (MultipartFile fileData : multipartFiles) {
            newStorageSize += fileData.getSize();
        }
        Storage storage = getStorageByAdmin();
        if (StringUtils.isNotNull(storage)) {
            storageSize = storage.getStorageSize() + newStorageSize;
            // 判断上传的文件是否超过了剩余空间
            if (storage.getMaxStorageSize() < storageSize) {
                return ResultBody.error("上传失败，您可用的空间已经不足！");
            } else {
                storage.setStorageSize(storageSize);
            }
        } else {
            return ResultBody.error("上传失败，您没有分配可用的上传空间！");
        }
        // 上传文件
        List<File> fileList = fileService.batchUploadFile(request, multipartFiles, systemConfig);
        List<NetworkDisk> networkDiskList = new ArrayList<>();

        for (File file : fileList) {
            NetworkDisk saveNetworkDisk = new NetworkDisk();
            saveNetworkDisk.setAdminUid(request.getAttribute(BaseSysConf.ADMIN_UID).toString());
            saveNetworkDisk.setFilePath(networkDisk.getFilePath());
            saveNetworkDisk.setQiNiuUrl(file.getQiNiuUrl());
            saveNetworkDisk.setLocalUrl(file.getPicUrl());
            saveNetworkDisk.setMinioUrl(file.getMinioUrl());
            saveNetworkDisk.setFileSize(file.getFileSize());
            saveNetworkDisk.setFileName(file.getPicName());
            saveNetworkDisk.setExtendName(file.getPicExpandedName());
            saveNetworkDisk.setFileOldName(file.getFileOldName());
            saveNetworkDisk.setCreateTime(new Date());
            networkDiskList.add(saveNetworkDisk);
        }
        // 上传文件
        networkDiskService.saveBatch(networkDiskList);
        // 更新容量大小
        this.updateById(storage);
        return ResultBody.success();
    }
}
