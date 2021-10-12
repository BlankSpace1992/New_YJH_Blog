package com.blog.business.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.domain.Storage;
import com.blog.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 查询当前用户存储信息
     *
     * @return 存储信息
     * @author yujunhong
     * @date 2021/10/11 14:34
     */
    Storage getStorageByAdmin();

    /**
     * 上传文件
     *
     * @param multipartFiles 文件集合实体
     * @param networkDisk    存储实体
     * @param request        请求
     * @return 上传文件
     * @author yujunhong
     * @date 2021/10/11 15:04
     */
    ResultBody uploadFile(HttpServletRequest request, NetworkDisk networkDisk, List<MultipartFile> multipartFiles);
}
