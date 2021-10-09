package com.blog.picture.controller;

import com.blog.business.picture.domain.File;
import com.blog.business.picture.service.FileService;
import com.blog.entity.FileVO;
import com.blog.entity.SystemConfigCommon;
import com.blog.exception.ResultBody;
import com.blog.utils.FeignUtils;
import com.blog.utils.MinIoUtils;
import com.blog.utils.StringUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/22 09:51
 */
@RestController
@RequestMapping(value = "/file")
@Api(value = "文件服务相关接口", tags = "文件服务相关接口")
public class FileController {
    @Autowired
    private MinIoUtils minIoUtils;
    @Autowired
    private FeignUtils feignUtils;
    @Autowired
    private FileService fileService;

    /**
     * 截图上传
     *
     * @param file 截图文件
     * @return 成功标志
     * @author yujunhong
     * @date 2021/7/7 16:50
     */
    @ApiOperation(value = "截图上传")
    @PostMapping(value = "/cropperPicture")
    public ResultBody cropperPicture(@RequestParam(value = "file") MultipartFile file) {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(file);
        return ResultBody.success(fileService.cropperPicture(multipartFileList));
    }

    /**
     * 获取文件的信息接口
     *
     * @param fileIds 文件id集合
     * @param code    切割符
     * @return 文件信息
     * @author yujunhong
     * @date 2021/7/30 14:42
     */
    @ApiOperation(value = "通过fileIds获取图片信息接口")
    @GetMapping(value = "/getPicture")
    public List<Map<String, Object>> getPicture(@ApiParam(name = "fileIds", value = "文件ids") @RequestParam(name =
            "fileIds",
            required = false) String fileIds,
                                                @ApiParam(name = "code", value = "切割符") @RequestParam(name = "code",
                                                        required =
                                                                false) String code) {
        if (StringUtils.isEmpty(fileIds)) {
            return new ArrayList<>();
        }
        return fileService.getPicture(fileIds, code);
    }

    /**
     * 多文件上传 同步处理
     * 上传图片接口   传入 userId sysUserId ,有那个传哪个，记录是谁传的,
     * projectName 传入的项目名称如 base 默认是base
     * sortName 传入的模块名， 如 admin，user ,等，不在数据库中记录的是不会上传的
     *
     * @param request   请求
     * @param filedatas 上传文件集合
     * @return 成功信息
     * @author yujunhong
     * @date 2021/7/30 15:01
     */
    @ApiOperation(value = "多图片上传接口", notes = "多图片上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileDatas", value = "文件数据", required = true),
            @ApiImplicitParam(name = "userUid", value = "用户UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sysUserId", value = "管理员UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sortName", value = "模块名", required = false, dataType = "String")
    })
    @PostMapping(value = "/pictures")
    public synchronized ResultBody uploadPicture(HttpServletRequest request, List<MultipartFile> filedatas) {
        // 获取系统配置文件
        SystemConfigCommon systemConfig = feignUtils.getSystemConfig();
        List<File> files = fileService.batchUploadFile(request, filedatas, systemConfig);
        return ResultBody.success(files);
    }

    /**
     * 通过url将图片上传到服务器中
     *
     * @param fileVO 文件图片类
     * @return 成功信息
     * @author yujunhong
     * @date 2021/7/30 15:08
     */
    @ApiOperation(value = "通过URL上传图片")
    @PostMapping(value = "/uploadPictureUrl")
    public ResultBody uploadPictureUrl(@RequestBody FileVO fileVO) {
        List<File> files = fileService.uploadPictureByUrl(fileVO);
        return ResultBody.success(files);
    }

    /**
     * ckEditor 文本编辑器 图片上传
     *
     * @param request 请求
     * @return 成功上传
     * @author yujunhong
     * @date 2021/7/30 15:53
     */
    @ApiOperation(value = "ckEditor 文本编辑器 图片上传")
    @PostMapping(value = "/ckEditorUploadFile")
    public ResultBody ckEditorUploadFile(HttpServletRequest request) {
        fileService.ckEditorUploadFile(request);
        return ResultBody.success();
    }

    /**
     * 复制得图片上传 ckEditor
     *
     * @return 上传成功
     * @author yujunhong
     * @date 2021/8/2 10:27
     */
    @ApiOperation(value = "复制得图片上传")
    @PostMapping(value = "/ckEditorUploadCopyFile")
    public ResultBody ckEditorUploadCopyFile() {
        Map<String, Object> map = fileService.ckEditorUploadCopyFile();
        return ResultBody.success(map);
    }

    /**
     * ckEditor工具栏 插入\编辑超链接的文件上传
     *
     * @return 上传成功
     * @author yujunhong
     * @date 2021/8/2 14:06
     */
    @ApiOperation(value = "工具栏-插入/编辑超链接的文件上传")
    @PostMapping(value = "/ckEditorUploadToolFile")
    public ResultBody ckEditorUploadToolFile() {
        fileService.ckEditorUploadToolFile();
        return ResultBody.success();
    }

}
