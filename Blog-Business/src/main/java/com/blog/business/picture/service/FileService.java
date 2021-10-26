package com.blog.business.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.picture.domain.File;
import com.blog.entity.FileVO;
import com.blog.entity.SystemConfigCommon;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/3 11:57
 */
public interface FileService extends IService<File> {

    /**
     * 截图上传
     *
     * @param multipartFileList 文件集合
     * @author yujunhong
     * @date 2021/7/7 16:52
     * @return 图片信息
     */
    List<File> cropperPicture(List<MultipartFile> multipartFileList);

    /**
     * 批量图片上传
     *
     * @param request           请求
     * @param multipartFileList 图片文件集合
     * @param systemConfig      系统配置文件
     * @author yujunhong
     * @date 2021/7/8 13:43
     * @return
     */
    List<File> batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList,
                               SystemConfigCommon systemConfig);

    /**
     * 获取文件的信息接口
     *
     * @param fileIds 文件id集合
     * @param code    切割符
     * @return 文件信息
     * @author yujunhong
     * @date 2021/7/30 14:44
     */
    List<Map<String, Object>> getPicture(String fileIds, String code);


    /**
     * 通过url将图片上传到服务器中
     *
     * @param fileVO 文件图片类
     * @author yujunhong
     * @date 2021/7/30 15:11
     * @return
     */
    List<File> uploadPictureByUrl(FileVO fileVO);

    /**
     * ckEditor 文本编辑器 图片上传
     *
     * @param request 请求
     * @author yujunhong
     * @date 2021/7/30 15:56
     * @return
     */
    Map<String, Object> ckEditorUploadFile(HttpServletRequest request);

    /**
     * 复制得图片上传
     *
     * @return map
     * @author yujunhong
     * @date 2021/8/2 10:30
     */
    Map<String, Object> ckEditorUploadCopyFile();

    /**
     * ckEditor工具栏 插入\编辑超链接的文件上传
     *
     * @author yujunhong
     * @date 2021/8/2 14:08
     */
    void ckEditorUploadToolFile();
}
