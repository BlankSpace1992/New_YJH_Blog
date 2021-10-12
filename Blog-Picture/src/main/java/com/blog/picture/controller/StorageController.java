package com.blog.picture.controller;

import com.blog.business.picture.domain.NetworkDisk;
import com.blog.business.picture.service.StorageService;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/16 15:13
 */
@RestController
@RequestMapping(value = "/storage")
@Api(value = "存储服务相关接口", tags = {"存储服务相关接口"})
public class StorageController {
    @Value(value = "${file.upload.path}")
    String path;
    @Autowired
    private StorageService storageService;

    /**
     * 初始化容量大小
     *
     * @param adminUid       管理员uid
     * @param maxStorageSize 最大网盘容量
     * @return 初始化容量大小
     * @author yujunhong
     * @date 2021/9/16 15:15
     */
    @PostMapping(value = "/initStorageSize")
    @ApiOperation(value = "初始化容量大小")
    public ResultBody initStorageSize(@ApiParam(name = "adminUid", value = "管理员uid") @RequestParam("adminUid") String adminUid,
                                      @ApiParam(name = "maxStorageSize", value = "最大网盘容量 ") @RequestParam(value =
                                              "maxStorageSize", defaultValue = "0") Long maxStorageSize) {
        return storageService.initStorageSize(adminUid, maxStorageSize);
    }

    /**
     * 编辑容量大小
     *
     * @param adminUid       管理员uid
     * @param maxStorageSize 最大网盘容量
     * @return 编辑容量大小
     * @author yujunhong
     * @date 2021/9/16 15:23
     */
    @PostMapping(value = "/editStorageSize")
    @ApiOperation(value = "编辑容量大小")
    public ResultBody editStorageSize(@ApiParam(name = "adminUid", value = "管理员uid") @RequestParam("adminUid") String adminUid,
                                      @ApiParam(name = "maxStorageSize", value = "最大网盘容量 ") @RequestParam(value =
                                              "maxStorageSize", defaultValue = "0") Long maxStorageSize) {

        return storageService.editStorageSize(adminUid, maxStorageSize);
    }

    /**
     * 通过管理员uid，获取存储信息
     *
     * @param adminUidList 用户id集合
     * @return 存储信息
     * @author yujunhong
     * @date 2021/9/16 15:28
     */
    @GetMapping(value = "/getStorageByAdminUid")
    @ApiOperation(value = "通过管理员uid，获取存储信息")
    public ResultBody getStorageByAdminUid(@RequestParam("adminUidList") List<String> adminUidList) {

        return storageService.getStorageByAdminUid(adminUidList);
    }

    /**
     * 上传文件
     *
     * @param request     请求
     * @param networkDisk 存储实体
     * @return 上传文件
     * @author yujunhong
     * @date 2021/10/11 15:02
     */
    @PostMapping(value = "/uploadFile")
    @ApiOperation(value = "上传文件")
    public ResultBody uploadFile(HttpServletRequest request, NetworkDisk networkDisk) {
        // 检查是否登录
        RequestHolder.checkLogin();
        // 从请求中获取文件
        List<MultipartFile> multipartFiles = FileUtils.getMultipartFiles(request);
        return storageService.uploadFile(request, networkDisk, multipartFiles);
    }

    /**
     * 查询当前用户存储信息
     *
     * @return 查询当前用户存储信息
     * @author yujunhong
     * @date 2021/10/11 15:13
     */
    @GetMapping(value = "/getStorage")
    @ApiOperation(value = "查询当前用户存储信息")
    public ResultBody getStorage() {
        // 检查是否登录
        RequestHolder.checkLogin();
        return ResultBody.success(storageService.getStorageByAdmin());
    }

}
