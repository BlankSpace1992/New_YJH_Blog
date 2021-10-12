package com.blog.picture.controller;

import com.alibaba.fastjson.JSON;
import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.domain.TreeNode;
import com.blog.business.picture.domain.vo.NetworkDiskVO;
import com.blog.business.picture.service.NetworkDiskService;
import com.blog.constants.BaseSysConf;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.FeignUtils;
import com.blog.utils.FileUtils;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/10/9 15:38
 */
@RestController
@RequestMapping("/networkDisk")
@Api(value = "网盘服务相关接口", tags = {"网盘服务相关接口"})
public class NetworkController {
    /**
     * 根节点id
     */
    public static long treeId = 0;

    @Autowired
    private NetworkDiskService networkDiskService;
    @Autowired
    private FeignUtils feignUtil;


    /**
     * 创建文件
     *
     * @param networkDisk 实体对象
     * @return 创建文件
     * @author yujunhong
     * @date 2021/10/9 15:40
     */
    @ApiOperation(value = "创建文件", notes = "创建文件")
    @PostMapping(value = "/createFile")
    public ResultBody createFile(@RequestBody NetworkDisk networkDisk) {
        // 获取用户uid
        String adminUid = RequestHolder.checkLogin();
        networkDisk.setAdminUid(adminUid);
        networkDiskService.createFile(networkDisk);
        return ResultBody.success();
    }

    /**
     * 获取文件列表
     *
     * @param networkDisk 查询条件
     * @return 获取文件列表
     * @author yujunhong
     * @date 2021/10/9 15:48
     */
    @ApiOperation(value = "获取文件列表", notes = "获取文件列表")
    @PostMapping(value = "/getFileList")
    public ResultBody getFileList(@RequestBody NetworkDisk networkDisk) {
        RequestHolder.checkLogin();
        // 路径解码
        String decode = StringUtils.EMPTY;
        try {
            decode = URLDecoder.decode(networkDisk.getFilePath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<NetworkDisk> fileList = networkDiskService.getFileList(networkDisk);
        return ResultBody.success(fileList);
    }

    /**
     * 重命名文件
     *
     * @param networkDiskVO 实体对象
     * @return 重命名文件
     * @author yujunhong
     * @date 2021/10/9 16:05
     */
    @ApiOperation(value = "重命名文件", notes = "重命名文件")
    @PostMapping(value = "/edit")
    public ResultBody edit(@RequestBody NetworkDiskVO networkDiskVO) {
        RequestHolder.checkLogin();
        return networkDiskService.updateFilepathByFilepath(networkDiskVO);
    }

    /**
     * 批量删除文件
     *
     * @param networkDiskVOList 实体集合
     * @return 批量删除文件
     * @author yujunhong
     * @date 2021/10/11 14:08
     */
    @ApiOperation(value = "批量删除文件", notes = "批量删除文件")
    @PostMapping(value = "/batchDeleteFile")
    public ResultBody batchDeleteFile(@RequestBody List<NetworkDiskVO> networkDiskVOList) {
        // 检查是否登录
        RequestHolder.checkLogin();
        // 获取系统配置信息
        Map<String, String> systemConfigMap = feignUtil.getSystemConfigMap(RequestHolder.getAdminToken());

        return networkDiskService.deleteFile(networkDiskVOList, systemConfigMap);
    }

    /**
     * 删除文件
     *
     * @param networkDiskVO 实体对象
     * @return 删除文件
     * @author yujunhong
     * @date 2021/10/11 14:38
     */
    @ApiOperation(value = "删除文件", notes = "删除文件")
    @PostMapping(value = "/deleteFile")
    public ResultBody deleteFile(@RequestBody NetworkDiskVO networkDiskVO) {
        // 检查是否登录
        RequestHolder.checkLogin();
        // 获取系统配置信息
        Map<String, String> systemConfigMap = feignUtil.getSystemConfigMap(RequestHolder.getAdminToken());
        List<NetworkDiskVO> networkDiskVOList = Collections.singletonList(networkDiskVO);
        return networkDiskService.deleteFile(networkDiskVOList, systemConfigMap);
    }

    /**
     * 文件移动
     *
     * @param networkDiskVO 实体对象
     * @return 文件移动
     * @author yujunhong
     * @date 2021/10/11 14:41
     */
    @ApiOperation(value = "文件移动", notes = "文件移动")
    @PostMapping(value = "/moveFile")
    public ResultBody moveFile(@RequestBody NetworkDiskVO networkDiskVO) {
        // 检查是否登录
        RequestHolder.checkLogin();
        return networkDiskService.updateFilepathByFilepath(networkDiskVO);
    }

    /**
     * 批量移动文件
     *
     * @param networkDiskVO 实体对象
     * @return 批量移动文件
     * @author yujunhong
     * @date 2021/10/11 14:44
     */
    @ApiOperation(value = "批量移动文件", notes = "批量移动文件")
    @PostMapping(value = "/batchMoveFile")
    public ResultBody batchMoveFile(@RequestBody NetworkDiskVO networkDiskVO) {
        RequestHolder.checkLogin();
        String files = networkDiskVO.getFiles();
        String newFilePath = networkDiskVO.getNewFilePath();
        List<NetworkDiskVO> fileList = JSON.parseArray(files, NetworkDiskVO.class);
        for (NetworkDiskVO file : fileList) {
            file.setNewFilePath(newFilePath);
            file.setOldFilePath(file.getFilePath());
            networkDiskService.updateFilepathByFilepath(file);
        }
        return ResultBody.success();
    }

    /**
     * 通过文件类型查询文件
     *
     * @param networkDisk 查询条件实体
     * @return 通过文件类型查询文件
     * @author yujunhong
     * @date 2021/10/11 14:46
     */
    @ApiOperation(value = "通过文件类型查询文件", notes = "通过文件类型查询文件")
    @GetMapping(value = "/selectFileByFileType")
    public ResultBody selectFileByFileType(NetworkDisk networkDisk) {
        List<NetworkDisk> networkDisks =
                networkDiskService.selectFileByFileType(FileUtils.getFileExtendsByType(networkDisk.getFileType()),
                        BaseSysConf.DEFAULT_UID);
        return ResultBody.success(networkDisks);
    }

    /**
     * 获取文件树
     *
     * @return 获取文件树
     * @author yujunhong
     * @date 2021/10/11 14:53
     */
    @ApiOperation(value = "获取文件树", notes = "获取文件树")
    @PostMapping(value = "/getFileTree")
    public ResultBody getFileTree() {
        List<NetworkDisk> filePathList = networkDiskService.selectPathTree();
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setNodeName("/");
        return ResultBody.success();
    }
}
