package com.blog.business.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.picture.domain.Storage;
import com.blog.exception.ResultBody;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
public interface StorageService extends IService<Storage> {
    /**
     * 初始化容量大小
     *
     * @param adminUid       管理员uid
     * @param maxStorageSize 最大网盘容量
     * @return 初始化容量大小
     * @author yujunhong
     * @date 2021/9/16 15:17
     */
    ResultBody initStorageSize(String adminUid, Long maxStorageSize);

    /**
     * 编辑容量大小
     *
     * @param adminUid       管理员uid
     * @param maxStorageSize 最大网盘容量
     * @return 初始化容量大小
     * @author yujunhong
     * @date 2021/9/16 15:17
     */
    ResultBody editStorageSize(String adminUid, Long maxStorageSize);

    /**
     * 通过管理员uid，获取存储信息
     *
     * @param adminUidList 用户id集合
     * @return 存储信息
     * @author yujunhong
     * @date 2021/9/16 15:29
     */
    ResultBody getStorageByAdminUid(List<String> adminUidList);

}
