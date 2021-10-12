package com.blog.business.picture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.domain.Storage;
import com.blog.business.picture.domain.vo.NetworkDiskVO;
import com.blog.business.picture.mapper.NetworkDiskMapper;
import com.blog.business.picture.service.NetworkDiskService;
import com.blog.business.picture.service.StorageService;
import com.blog.constants.BaseSQLConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.constants.FilePriority;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.FeignUtils;
import com.blog.utils.FileUtils;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
@Service
@Slf4j
public class NetworkDiskServiceImpl extends ServiceImpl<NetworkDiskMapper, NetworkDisk> implements NetworkDiskService {
    @Autowired
    private FeignUtils feignUtils;
    @Autowired
    private StorageService storageService;
    @Value(value = "${file.upload.path}")
    private String uploadPath;

    @Override
    public void createFile(NetworkDisk networkDisk) {
        this.save(networkDisk);
    }

    @Override
    public List<NetworkDisk> getFileList(NetworkDisk networkDisk) {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "获取文件列表失败"));
        // 获取token
        if (StringUtils.isNull(request.getAttribute(BaseSysConf.TOKEN))) {
            return null;
        }
        String token = request.getAttribute(BaseSysConf.TOKEN).toString();
        // 通过token获取配置文件
        Map<String, String> systemConfigMap = feignUtils.getSystemConfigMap(token);
        // 获取图片优先级
        String picturePriority = systemConfigMap.get(BaseSysConf.PICTURE_PRIORITY);
        LambdaQueryWrapper<NetworkDisk> networkDiskWrapper = new LambdaQueryWrapper<>();
        networkDiskWrapper.eq(NetworkDisk::getStatus, EnumsStatus.ENABLE);
        // 根据扩展名查找
        if (networkDisk.getFileType() != 0) {
            // 判断是否是其它文件
            if (FileUtils.OTHER_TYPE == networkDisk.getFileType()) {
                networkDiskWrapper.notIn(NetworkDisk::getExtendName,
                        FileUtils.getFileExtendsByType(networkDisk.getFileType()));
            } else {
                networkDiskWrapper.in(NetworkDisk::getExtendName,
                        FileUtils.getFileExtendsByType(networkDisk.getFileType()));
            }
        } else if (StringUtils.isNotEmpty(networkDisk.getFilePath())) {
            // 没有扩展名时，查找全部
            networkDiskWrapper.eq(NetworkDisk::getExtendName, networkDisk.getFilePath());
        }
        networkDiskWrapper.orderByAsc(NetworkDisk::getCreateTime);
        // 查询数据
        List<NetworkDisk> networkDiskList = this.list(networkDiskWrapper);
        networkDiskList.forEach(item -> {
            if (FilePriority.QI_NIU.equals(picturePriority)) {
                item.setFileUrl(systemConfigMap.get(BaseSysConf.QI_NIU_PICTURE_BASE_URL) + item.getQiNiuUrl());
            } else if (FilePriority.MINIO.equals(picturePriority)) {
                item.setFileUrl(systemConfigMap.get(BaseSysConf.MINIO_PICTURE_BASE_URL) + item.getMinioUrl());
            } else {
                item.setFileUrl(systemConfigMap.get(BaseSysConf.LOCAL_PICTURE_BASE_URL) + item.getLocalUrl());
            }
        });
        return networkDiskList;
    }

    @Override
    public ResultBody updateFilepathByFilepath(NetworkDiskVO networkDiskVO) {
        String oldFilePath = networkDiskVO.getOldFilePath();
        String newFilePath = networkDiskVO.getNewFilePath();
        String fileName = networkDiskVO.getFileName();
        String fileOldName = networkDiskVO.getFileOldName();
        String extendName = networkDiskVO.getExtendName();

        if ("null".equals(networkDiskVO.getExtendName())) {
            extendName = null;
        }
        // 判断移动的路径是否相同【拼接出原始目录】
        String fileOldPath = oldFilePath + fileOldName + "/";
        if (fileOldPath.equals(newFilePath)) {
            return ResultBody.error("不能选择自己");
        }
        // 移动根目录
        LambdaQueryWrapper<NetworkDisk> networkDiskWrapper = new LambdaQueryWrapper<>();
        networkDiskWrapper.eq(NetworkDisk::getFilePath, oldFilePath);
        networkDiskWrapper.eq(NetworkDisk::getFileName, fileName);
        if (StringUtils.isNotEmpty(extendName)) {
            networkDiskWrapper.eq(NetworkDisk::getExtendName, extendName);
        } else {
            networkDiskWrapper.isNull(NetworkDisk::getExtendName);
        }
        List<NetworkDisk> networkDiskList = this.list(networkDiskWrapper);
        for (NetworkDisk networkDisk : networkDiskList) {
            // 修改新的路径
            networkDisk.setFilePath(newFilePath);
            // 修改旧文件名
            networkDisk.setFileOldName(networkDiskVO.getFileOldName());
            // 如果扩展名为空，代表是文件夹，还需要修改文件名
            if (StringUtils.isEmpty(extendName)) {
                networkDisk.setFileName(networkDiskVO.getFileOldName());
            }
        }
        if (networkDiskList.size() > 0) {
            this.updateBatchById(networkDiskList);
        }
        //移动子目录
        oldFilePath = oldFilePath + fileName + "/";
        newFilePath = newFilePath + fileOldName + "/";

        oldFilePath = oldFilePath.replace("\\", "\\\\\\\\");
        oldFilePath = oldFilePath.replace("'", "\\'");
        oldFilePath = oldFilePath.replace("%", "\\%");
        oldFilePath = oldFilePath.replace("_", "\\_");

        //为null说明是目录，则需要移动子目录
        if (extendName == null) {
            //移动根目录
            QueryWrapper<NetworkDisk> childQueryWrapper = new QueryWrapper<>();
            childQueryWrapper.likeRight(BaseSQLConf.FILE_PATH, oldFilePath);
            List<NetworkDisk> childList = this.list(childQueryWrapper);
            for (NetworkDisk networkDisk : childList) {
                String filePath = networkDisk.getFilePath();
                networkDisk.setFilePath(filePath.replace(oldFilePath, newFilePath));
            }
            if (childList.size() > 0) {
                this.updateBatchById(childList);
            }
        }
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteFile(List<NetworkDiskVO> networkDiskVOList, Map<String, String> systemConfigMap) {
        // 循环遍历
        for (NetworkDiskVO networkDiskVO : networkDiskVOList) {
            // 获取uid
            String uid = networkDiskVO.getUid();
            if (StringUtils.isEmpty(uid)) {
                return ResultBody.error("删除的文件不能为空");
            }
            // 获取信息
            NetworkDisk networkDisk = this.getById(uid);
            String uploadLocal = systemConfigMap.get(BaseSysConf.UPLOAD_LOCAL);
            String uploadQiNiu = systemConfigMap.get(BaseSysConf.UPLOAD_QI_NIU);
            String uploadMinio = systemConfigMap.get(BaseSysConf.UPLOAD_MINIO);

            // 修改为删除状态
            networkDisk.setStatus(EnumsStatus.DISABLED);
            this.updateById(networkDisk);
            // 判断删除的是文件 or 文件夹
            if (BaseSysConf.ONE == networkDisk.getIsDir()) {
                // 删除的是文件夹，那么需要把文件下所有的文件获得，进行删除
                // 获取文件的路径，查询出该路径下所有的文件
                String path = networkDisk.getFilePath() + networkDisk.getFileName();
                LambdaQueryWrapper<NetworkDisk> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(NetworkDisk::getStatus, EnumsStatus.ENABLE);
                // 查询以  path%  开头的
                queryWrapper.likeRight(NetworkDisk::getFilePath, path);
                List<NetworkDisk> list = this.list(queryWrapper);

                if (list.size() > 0) {
                    // 将所有的状态设置成失效
                    list.forEach(item -> {
                        item.setStatus(EnumsStatus.DISABLED);
                    });
                    Boolean isUpdateSuccess = this.updateBatchById(list);
                    if (isUpdateSuccess) {

                        // 删除本地文件，同时移除本地文件
                        if (EnumsStatus.OPEN.equals(uploadLocal)) {
                            // 获取删除的路径
                            List<String> fileList = new ArrayList<>();
                            list.forEach(item -> {
                                fileList.add(uploadPath + item.getLocalUrl());
                            });
                            // 批量删除本地图片
                            FileUtils.deleteFileList(fileList);
                        }
                        // TODO: 2021/10/11 minio/qiniu暂收不做处理
                        /*// 删除七牛云上文件
                        if (EOpenStatus.OPEN.equals(uploadQiNiu)) {
                            List<String> fileList = new ArrayList<>();
                            list.forEach(item -> {
                                fileList.add(item.getQiNiuUrl());
                            });
                            qiniuUtil.deleteFileList(fileList, qiNiuConfig);
                        }

                        // 删除Minio中的文件
                        if (EOpenStatus.OPEN.equals(uploadMinio)) {
                            List<String> fileList = new ArrayList<>();
                            list.forEach(item -> {
                                fileList.add(item.getMinioUrl());
                            });
                            minioUtil.deleteBatchFile(fileList);
                        }*/
                    }
                }
            } else {
                // TODO 以后这里可以写成定时器，而不是马上删除，增加回收站的功能
                // 删除本地文件，同时移除本地文件
                if (EnumsStatus.OPEN.equals(uploadLocal)) {
                    String localUrl = networkDisk.getLocalUrl();
                    FileUtils.deleteFile(uploadPath + localUrl);
                }
                // TODO: 2021/10/11 minio/qiniu暂收不做处理
                /*// 删除七牛云上文件
                if (EOpenStatus.OPEN.equals(uploadQiNiu)) {
                    String qiNiuUrl = networkDisk.getQiNiuUrl();
                    qiniuUtil.deleteFile(qiNiuUrl, qiNiuConfig);
                }

                // 删除Minio中的文件
                if (EOpenStatus.OPEN.equals(uploadMinio)) {
                    String minioUrl = networkDisk.getMinioUrl();
                    if (StringUtils.isNotEmpty(minioUrl)) {
                        String[] minUrlArray = minioUrl.split("/");
                        // 找到文件名
                        minioUtil.deleteFile(minUrlArray[minUrlArray.length - 1]);
                    } else {
                        log.error("删除的文件不存在Minio文件地址");
                    }
                }*/

                Storage storage = storageService.getStorageByAdmin();
                if (StringUtils.isNull(storage)) {
                    return ResultBody.error("当前用户无存储信息");
                }
                long storageSize = 0L;
                try {
                    storageSize = storage.getStorageSize() - networkDisk.getFileSize();
                } catch (Exception e) {
                    log.info("本地文件空间不存在!");
                }
                storage.setStorageSize(storageSize > 0 ? storageSize : 0L);
                storageService.updateById(storage);
            }
        }
        return ResultBody.success();
    }

    @Override
    public List<NetworkDisk> selectFileByFileType(List<String> filenameList, String adminUid) {
        LambdaQueryWrapper<NetworkDisk> networkDiskWrapper = new LambdaQueryWrapper<>();
        networkDiskWrapper.in(NetworkDisk::getExtendName, filenameList);
        networkDiskWrapper.eq(NetworkDisk::getAdminUid, adminUid);
        networkDiskWrapper.eq(NetworkDisk::getStatus, EnumsStatus.ENABLE);
        return this.list(networkDiskWrapper);
    }

    @Override
    public List<NetworkDisk> selectPathTree() {
        LambdaQueryWrapper<NetworkDisk> networkDiskWrapper = new LambdaQueryWrapper<>();
        networkDiskWrapper.eq(NetworkDisk::getStatus, EnumsStatus.ENABLE);
        networkDiskWrapper.eq(NetworkDisk::getIsDir, BaseSysConf.ONE);
        return this.list(networkDiskWrapper);
    }

}
