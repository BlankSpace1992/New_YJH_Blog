package com.blog.business.picture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.File;
import com.blog.business.picture.domain.FileSort;
import com.blog.business.picture.mapper.FileMapper;
import com.blog.business.picture.service.*;
import com.blog.constants.*;
import com.blog.entity.FileVO;
import com.blog.entity.SystemConfigCommon;
import com.blog.exception.CommonErrorException;
import com.blog.holder.RequestHolder;
import com.blog.utils.FeignUtils;
import com.blog.utils.FileUtils;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    private FeignUtils feignUtils;
    @Autowired
    private FileSortService fileSortService;
    @Autowired
    private QiNiuService qiNiuService;
    @Autowired
    private MinioService minioService;
    @Autowired
    private LocalFileService localFileService;


    @Override
    public List<File> cropperPicture(List<MultipartFile> multipartFileList) {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "截图上传失败"));
        // 获取配置文件
        SystemConfigCommon systemConfig = feignUtils.getSystemConfig();
        // 七牛云图片基础地址
        String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
        // 本地地址
        String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
        // minio图片服务器地址
        String minioPictureBaseUrl = systemConfig.getMinioPictureBaseUrl();
        // 上传文件
        return this.batchUploadFile(request, multipartFileList, systemConfig);

    }

    @Override
    public List<File> batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList,
                                      SystemConfigCommon systemConfig) {
        // 是否上传七牛云
        String uploadQiNiu = systemConfig.getUploadQiNiu();
        // 是否上传本地
        String uploadLocal = systemConfig.getUploadLocal();
        // 是否上传MINIO
        String uploadMinio = systemConfig.getUploadMinio();
        // 判断来源
        String source = request.getParameter(BaseSysConf.SOURCE);
        // 如果是用户上传,则包含用户UID
        String userUid = StringUtils.EMPTY;
        // 如果是管理员上传,则包含管理员uid
        String adminUid = StringUtils.EMPTY;
        // 项目名
        String projectName = StringUtils.EMPTY;
        // 模块名
        String sortName = StringUtils.EMPTY;

        // 判断图片来源
        if (BaseSysConf.PICTURE.equals(source)) {
            // 当从web网站过来的，直接从参数中获取
            userUid = request.getParameter(BaseSysConf.USER_UID);
            adminUid = request.getParameter(BaseSysConf.ADMIN_UID);
            projectName = request.getParameter(BaseSysConf.PROJECT_NAME);
            sortName = request.getParameter(BaseSysConf.SORT_NAME);
        } else if (BaseSysConf.ADMIN.equals(source)) {
            // 当图片从admin传递过来的时候
            userUid = request.getAttribute(BaseSysConf.USER_UID).toString();
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(BaseSysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(BaseSysConf.SORT_NAME).toString();
        } else {
            userUid = request.getAttribute(BaseSysConf.USER_UID).toString();
            adminUid = request.getAttribute(BaseSysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(BaseSysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(BaseSysConf.SORT_NAME).toString();
        }

        // 工程名默认为base
        projectName = StringUtils.isEmpty(projectName) ? "base" : projectName;
        // 检测用户是否登录
        if (StringUtils.isEmpty(adminUid) && StringUtils.isEmpty(userUid)) {
            throw new CommonErrorException("请先注册");
        }

        // 查询文件分类
        LambdaQueryWrapper<FileSort> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileSort::getSortName, sortName);
        wrapper.eq(FileSort::getProjectName, projectName);
        wrapper.eq(FileSort::getStatus, EnumsStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(wrapper);
        if (StringUtils.isEmpty(fileSorts)) {
            throw new CommonErrorException("文件不允许被上传");
        }
        FileSort fileSort = fileSorts.get(0);
        // 获取存储路径
        String baseUrl = StringUtils.isEmpty(fileSort.getUrl()) ? "base/common" : fileSort.getUrl();
        // 保存
        List<File> lists = new ArrayList<>();
        // 文件上传
        if (StringUtils.isNotEmpty(multipartFileList)) {
            for (MultipartFile filedata : multipartFileList) {
                String oldName = filedata.getOriginalFilename();
                long size = filedata.getSize();
                //获取扩展名，默认是jpg
                String picExpandedName = FileUtils.getExpandedName(oldName);
                //获取新文件名
                String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + picExpandedName;
                String localUrl = "";
                String qiNiuUrl = "";
                String minioUrl = "";
                try {
                    MultipartFile tempFileData = filedata;
                    // 上传七牛云，判断是否能够上传七牛云
                    if (OpenStatus.OPEN.equals(uploadQiNiu)) {
                        qiNiuUrl = qiNiuService.uploadFile(tempFileData);
                    }

                    // 判断是否能够上传Minio文件服务器
                    if (OpenStatus.OPEN.equals(uploadMinio)) {
                        minioUrl = minioService.uploadFile(tempFileData);
                    }

                    // 判断是否能够上传至本地
                    if (OpenStatus.OPEN.equals(uploadLocal)) {
                        localUrl = localFileService.uploadFile(filedata, fileSort);
                    }
                } catch (Exception e) {
                    log.info("上传文件异常: {}", e.getMessage());
                    e.getStackTrace();
                }

                File file = new File();
                file.setCreateTime(new Date(System.currentTimeMillis()));
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(oldName);
                file.setFileSize(size);
                file.setPicExpandedName(picExpandedName);
                file.setPicName(newFileName);
                file.setPicUrl(localUrl);
                file.setStatus(EnumsStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                file.setQiNiuUrl(qiNiuUrl);
                file.setMinioUrl(minioUrl);
                lists.add(file);
            }

        }
        this.saveBatch(lists);
        return lists;
    }

    @Override
    public List<Map<String, Object>> getPicture(String fileIds, String code) {
        code = StringUtils.isEmpty(code) ? Constants.SYMBOL_COMMA : code;
        // 存储集合
        List<Map<String, Object>> list = new ArrayList<>();
        // 根据分隔符code拆分文件id
        List<String> fileIdList = StringUtils.stringToList(code, fileIds);
        // 查询对应的文件信息
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(File::getUid, fileIdList);
        List<File> fileList = this.list(wrapper);
        for (File file : fileList) {
            Map<String, Object> remap = new HashMap<>();
            // 获取七牛云地址
            remap.put(BaseSysConf.QI_NIU_URL, file.getQiNiuUrl());
            // 获取Minio对象存储地址
            remap.put(BaseSysConf.MINIO_URL, file.getMinioUrl());
            // 获取本地地址
            remap.put(BaseSysConf.URL, file.getPicUrl());
            // 后缀名，也就是类型
            remap.put(BaseSysConf.EXPANDED_NAME, file.getPicExpandedName());
            //名称
            remap.put(BaseSysConf.NAME, file.getPicName());
            remap.put(BaseSysConf.UID, file.getUid());
            remap.put(BaseSysConf.FILE_OLD_NAME, file.getFileOldName());
            list.add(remap);
        }
        return list;
    }

    @Override
    public List<File> uploadPictureByUrl(FileVO fileVO) {
        // 获取配置文件
        SystemConfigCommon systemConfig;
        if (StringUtils.isNotNull(fileVO.getSystemConfig())) {
            Map<String, String> map = fileVO.getSystemConfig();
            systemConfig = feignUtils.getSystemConfigByMap(map);
        } else {
            systemConfig = feignUtils.getSystemConfig();
        }
        // 用户id
        String userUid = fileVO.getUserUid();
        // 管理员id
        String adminUid = fileVO.getAdminUid();
        // 工程名称
        String projectName = fileVO.getProjectName();
        // 模块名称
        String sortName = fileVO.getSortName();
        // 图片url地址集合
        List<String> urlList = fileVO.getUrlList();
        // 默认为base
        projectName = StringUtils.isEmpty(projectName) ? "base" : projectName;
        // 判断用户是否登录
        if (StringUtils.isEmpty(userUid) || StringUtils.isEmpty(adminUid)) {
            throw new CommonErrorException(ErrorCode.INSERT_DEFAULT_ERROR, "请先注册");
        }
        // 查询文件分类信息
        LambdaQueryWrapper<FileSort> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileSort::getSortName, sortName);
        wrapper.eq(FileSort::getProjectName, projectName);
        wrapper.eq(FileSort::getStatus, EnumsStatus.ENABLE);
        // 获取文件分类信息
        FileSort fileSort =
                Optional.ofNullable(fileSortService.getOne(wrapper)).orElseThrow(() -> new CommonErrorException(ErrorCode.INSERT_DEFAULT_ERROR, "文件不被允许上传, 请填写文件分类信息"));
        List<File> files = new ArrayList<>();
        // 文件上传
        if (StringUtils.isNotEmpty(urlList)) {

            for (String url : urlList) {
                //获取新文件名(默认为jpg)
                String newFileName = System.currentTimeMillis() + ".jpg";
                // 将图片上传到本地服务器中以及七牛云中
                String picurl = "";
                String qiNiuUrl = "";
                String minioUrl = "";
                // 判断是否能够上传至本地
                if (OpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
                    picurl = localFileService.uploadPictureByUrl(url, fileSort);
                }
                // 上传七牛云，判断是否能够上传七牛云
                if (OpenStatus.OPEN.equals(systemConfig.getUploadMinio())) {
                    minioUrl = minioService.uploadPictureByUrl(url);
                }
                // 上传七牛云，判断是否能够上传七牛云
                if (OpenStatus.OPEN.equals(systemConfig.getUploadQiNiu())) {
                    qiNiuUrl = qiNiuService.uploadPictureByUrl(url, systemConfig);
                }
                File file = new File();
                file.setCreateTime(new Date(System.currentTimeMillis()));
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(url);
                file.setFileSize(0L);
                file.setPicExpandedName("jpg");
                file.setPicName(newFileName);
                file.setPicUrl(picurl);
                file.setQiNiuUrl(qiNiuUrl);
                file.setMinioUrl(minioUrl);
                file.setStatus(EnumsStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                this.save(file);
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public Map<String, Object> ckEditorUploadFile(HttpServletRequest request) {
        // 获取token
        String token = request.getParameter(BaseSysConf.TOKEN);
        // 获取配置文件
        SystemConfigCommon systemConfig = feignUtils.getSystemConfigByMap(feignUtils.getSystemConfigMap(token));
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        // 获取文件信息
        File saveFile = null;
        while (iter.hasNext()) {
            // 获取文件对象
            MultipartFile file = multiRequest.getFile(iter.next());
            if (StringUtils.isNotNull(file)) {
                // 获取旧名称
                String originalFilename = file.getOriginalFilename();
                // 获取扩展名称
                String expandedName = FileUtils.getExpandedName(originalFilename);
                // 判断是否是图片
                if (!FileUtils.isPicture(expandedName)) {
                    throw new CommonErrorException(BaseSysConf.MESSAGE, "请上传正确的图片");
                }
                //对图片大小进行限制
                if (file.getSize() > (10 * 1024 * 1024)) {
                    throw new CommonErrorException(BaseSysConf.MESSAGE, "图片大小不能超过10M");
                }

                // 设置图片上传服务必要的信息
                request.setAttribute(BaseSysConf.USER_UID, BaseSysConf.DEFAULT_UID);
                request.setAttribute(BaseSysConf.ADMIN_UID, BaseSysConf.DEFAULT_UID);
                request.setAttribute(BaseSysConf.PROJECT_NAME, BaseSysConf.BLOG);
                request.setAttribute(BaseSysConf.SORT_NAME, BaseSysConf.ADMIN);
                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                saveFile = this.batchUploadFile(request, fileData, systemConfig).get(0);
            }
        }
        Map<String, Object> map = new HashMap<>();
        // 设置回显数据
        map.put(BaseSysConf.UPLOADED, 1);
        map.put(BaseSysConf.FILE_NAME, saveFile.getPicName());
        String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
        // 设置图片服务根域名
        String url = localPictureBaseUrl + saveFile.getPicUrl();
        map.put(BaseSysConf.URL, url);
        return map;
    }

    @Override
    public Map<String, Object> ckEditorUploadCopyFile() {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("图片上传失败"));
        // 从参数中获取token
        String token = request.getParameter(BaseSysConf.TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new CommonErrorException(ErrorCode.INSERT_DEFAULT_ERROR, "未获取到token");
        }
        // 拆分token
        String[] params = token.split("\\?url=");
        // 从redis获取系统配置文件
        Map<String, String> qiNiuConfig = new HashMap<>();
        Map<String, String> systemConfigMap = feignUtils.getSystemConfigMap(params[0]);
        SystemConfigCommon systemConfig = feignUtils.getSystemConfigByMap(systemConfigMap);
        // 需要上传的url
        String url = params[1];
        // 判断需要上传的域名和本机图片域名是否一致
        if (FilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getQiNiuPictureBaseUrl()) && StringUtils.isNotEmpty(url) && url.contains(systemConfig.getQiNiuPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(BaseSysConf.UPLOADED, 1);
                result.put(BaseSysConf.FILE_NAME, url);
                result.put(BaseSysConf.URL, url);
                return result;
            }
        } else if (FilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
            // 表示优先显示Minio对象存储
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getMinioPictureBaseUrl()) && StringUtils.isNotEmpty(url) && url.contains(systemConfig.getMinioPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(BaseSysConf.UPLOADED, 1);
                result.put(BaseSysConf.FILE_NAME, url);
                result.put(BaseSysConf.URL, url);
                return result;
            }
        }
        // 表示优先显示本地服务器
        // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
        else {
            if (StringUtils.isNotEmpty(systemConfig.getLocalPictureBaseUrl()) && StringUtils.isNotEmpty(url) && url.contains(systemConfig.getLocalPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(BaseSysConf.UPLOADED, 1);
                result.put(BaseSysConf.FILE_NAME, url);
                result.put(BaseSysConf.URL, url);
                return result;
            }
        }
        // 查询文件分类
        LambdaQueryWrapper<FileSort> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileSort::getSortName, BaseSysConf.ADMIN);
        wrapper.eq(FileSort::getProjectName, BaseSysConf.BLOG);
        wrapper.eq(FileSort::getStatus, EnumsStatus.ENABLE);
        FileSort fileSort =
                Optional.ofNullable(fileSortService.getOne(wrapper)).orElseThrow(() -> new CommonErrorException(BaseSysConf.ERROR, "文件不被允许上传"));
        // 获取地址
        String sortUrl = StringUtils.isEmpty(fileSort.getUrl()) ? "base/common/" : fileSort.getUrl();
        // 存储文件名
        String newFileName = System.currentTimeMillis() + ".jpg";
        // 文件url的访问地址
        String localUrl = StringUtils.EMPTY;
        String qiNiuUrl = StringUtils.EMPTY;
        String minioUrl = StringUtils.EMPTY;
        // 上传到本地服务器
        if (OpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
            localUrl = localFileService.uploadPictureByUrl(url, fileSort);
        }
        // 上传到minio
        if (OpenStatus.OPEN.equals(systemConfig.getUploadMinio())) {
            minioUrl = minioService.uploadPictureByUrl(url);
        }
        // 上传到七牛云
        if (OpenStatus.OPEN.equals(systemConfig.getUploadQiNiu())) {
            qiNiuUrl = qiNiuService.uploadPictureByUrl(url, systemConfig);
        }

        File file = new File();
        file.setCreateTime(new Date(System.currentTimeMillis()));
        file.setFileSortUid(fileSort.getUid());
        file.setFileOldName(url);
        file.setFileSize(0L);
        file.setPicExpandedName(Constants.FILE_SUFFIX_JPG);
        file.setPicName(newFileName);

        // 设置本地图片
        file.setPicUrl(systemConfig.getLocalPictureBaseUrl() + localUrl);
        // 设置minio图片
        file.setMinioUrl(systemConfig.getMinioPictureBaseUrl() + minioUrl);
        // 设置七牛云图片
        file.setQiNiuUrl(systemConfig.getQiNiuPictureBaseUrl() + qiNiuUrl);

        file.setStatus(EnumsStatus.ENABLE);
        file.setUserUid(BaseSysConf.DEFAULT_UID);
        file.setAdminUid(BaseSysConf.DEFAULT_UID);
        this.save(file);
        Map<String, Object> result = new HashMap<>();
        result.put(BaseSysConf.UPLOADED, 1);
        result.put(BaseSysConf.FILE_NAME, newFileName);
        // 设置显示方式
        if (FilePriority.QI_NIU.equals(systemConfigMap.get(BaseSysConf.PICTURE_PRIORITY))) {
            result.put(BaseSysConf.URL, systemConfig.getQiNiuPictureBaseUrl() + qiNiuUrl);
        } else if (FilePriority.MINIO.equals(systemConfigMap.get(BaseSysConf.PICTURE_PRIORITY))) {
            result.put(BaseSysConf.URL, systemConfig.getMinioPictureBaseUrl() + localUrl);
        } else {
            result.put(BaseSysConf.URL, systemConfig.getLocalPictureBaseUrl() + localUrl);
        }
        return result;
    }

    @Override
    public void ckEditorUploadToolFile() {
        // 获取请求
        HttpServletRequest request =
                Optional.ofNullable(RequestHolder.getRequest()).orElseThrow(() -> new CommonErrorException("图片上传失败"));
        // 获取token值
        String token = request.getParameter(BaseSysConf.TOKEN);
        // 获取系统配置
        Map<String, String> systemConfigMap = feignUtils.getSystemConfigMap(token);
        SystemConfigCommon systemConfig = feignUtils.getSystemConfigByMap(systemConfigMap);
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iterator = multiRequest.getFileNames();
        while (iterator.hasNext()) {
            MultipartFile file = multiRequest.getFile(iterator.next());
            if (StringUtils.isNotNull(file)) {
                // 获取旧名称
                String oldName = file.getOriginalFilename();
                // 获取扩展名
                String expandedName = FileUtils.getExpandedName(oldName);
                // 对文件大小进行限制
                if (file.getSize() > (50 * 1024 * 1024)) {
                    throw new CommonErrorException(BaseSysConf.MESSAGE, "文件大小不能超过50M");
                }
                // 设置图片上传服务必要的信息
                request.setAttribute(BaseSysConf.USER_UID, BaseSysConf.DEFAULT_UID);
                request.setAttribute(BaseSysConf.ADMIN_UID, BaseSysConf.DEFAULT_UID);
                request.setAttribute(BaseSysConf.PROJECT_NAME, BaseSysConf.BLOG);
                request.setAttribute(BaseSysConf.SORT_NAME, BaseSysConf.ADMIN);
                List<MultipartFile> fileData = new ArrayList<>();
                fileData.add(file);
                this.batchUploadFile(request, fileData, systemConfig);
            }
        }
    }
}
