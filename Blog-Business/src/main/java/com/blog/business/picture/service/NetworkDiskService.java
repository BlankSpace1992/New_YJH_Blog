package com.blog.business.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.domain.vo.NetworkDiskVO;
import com.blog.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
public interface NetworkDiskService extends IService<NetworkDisk> {

    /**
     * 创建文件
     *
     * @param networkDisk 实体对象
     * @author yujunhong
     * @date 2021/10/9 15:46
     */
    void createFile(NetworkDisk networkDisk);


    /**
     * 获取文件列表
     *
     * @param networkDisk 查询条件
     * @return 获取文件列表
     * @author yujunhong
     * @date 2021/10/9 15:52
     */
    List<NetworkDisk> getFileList(NetworkDisk networkDisk);


    /**
     * 重命名文件
     *
     * @param networkDiskVO 实体对象
     * @return 重命名文件
     * @author yujunhong
     * @date 2021/10/9 16:06
     */
    ResultBody updateFilepathByFilepath(NetworkDiskVO networkDiskVO);

    /**
     * 批量删除文件
     *
     * @param networkDiskVOList 实体集合
     * @param systemConfigMap   系统配置文件
     * @return 批量删除文件
     * @author yujunhong
     * @date 2021/10/11 14:22
     */
    ResultBody deleteFile(List<NetworkDiskVO> networkDiskVOList, Map<String, String> systemConfigMap);


    /**
     * 通过文件类型查询文件
     *
     * @param filenameList 文件名称集合
     * @param adminUid     用户uid
     * @return 通过文件类型查询文件
     * @author yujunhong
     * @date 2021/10/11 14:48
     */
    List<NetworkDisk> selectFileByFileType(List<String> filenameList, String adminUid);

    /**
     * 获取所有文件夹节点
     *
     * @return 获取所有文件夹节点
     * @author yujunhong
     * @date 2021/10/11 14:54
     */
    List<NetworkDisk> selectPathTree();
}
