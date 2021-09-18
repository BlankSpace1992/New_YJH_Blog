package com.blog.picture.controller;

import com.blog.business.picture.service.StorageService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/9/16 15:13
 */
@RestController
@RequestMapping(value = "/storage")
@Api(value = "�洢������ؽӿ�", tags = {"�洢������ؽӿ�"})
public class StorageController {
    @Value(value = "${file.upload.path}")
    String path;
    @Autowired
    private StorageService storageService;

    /**
     * ��ʼ��������С
     *
     * @param adminUid       ����Աuid
     * @param maxStorageSize �����������
     * @return ��ʼ��������С
     * @author yujunhong
     * @date 2021/9/16 15:15
     */
    @PostMapping(value = "/initStorageSize")
    @ApiOperation(value = "��ʼ��������С")
    public ResultBody initStorageSize(@ApiParam(name = "adminUid", value = "����Աuid") @RequestParam("adminUid") String adminUid,
                                      @ApiParam(name = "maxStorageSize", value = "����������� ") @RequestParam(value =
                                              "maxStorageSize", defaultValue = "0") Long maxStorageSize) {
        return storageService.initStorageSize(adminUid, maxStorageSize);
    }

    /**
     * �༭������С
     *
     * @param adminUid       ����Աuid
     * @param maxStorageSize �����������
     * @return �༭������С
     * @author yujunhong
     * @date 2021/9/16 15:23
     */
    @PostMapping(value = "/editStorageSize")
    @ApiOperation(value = "�༭������С")
    public ResultBody editStorageSize(@ApiParam(name = "adminUid", value = "����Աuid") @RequestParam("adminUid") String adminUid,
                                      @ApiParam(name = "maxStorageSize", value = "����������� ") @RequestParam(value =
                                              "maxStorageSize", defaultValue = "0") Long maxStorageSize) {

        return storageService.editStorageSize(adminUid, maxStorageSize);
    }

    /**
     * ͨ������Աuid����ȡ�洢��Ϣ
     *
     * @param adminUidList �û�id����
     * @return �洢��Ϣ
     * @author yujunhong
     * @date 2021/9/16 15:28
     */
    @GetMapping(value = "/getStorageByAdminUid")
    @ApiOperation(value = "ͨ������Աuid����ȡ�洢��Ϣ")
    public ResultBody getStorageByAdminUid(@RequestParam("adminUidList") List<String> adminUidList) {

        return storageService.getStorageByAdminUid(adminUidList);
    }
}
