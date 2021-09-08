package com.blog.feign;

import com.blog.config.FeignConfiguration;
import com.blog.entity.FileVO;
import com.blog.exception.ResultBody;
import com.blog.fallback.PictureFeignFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/3 14:49
 */
@FeignClient(name = "cloud-blog-picture", configuration = FeignConfiguration.class, fallback =
        PictureFeignFallBack.class)
public interface PictureFeignClient {

    /**
     * 获取文件得信息接口
     *
     * @param fileIds 图片UID
     * @param code    分隔符
     * @return 图片信息
     * @author yujunhong
     * @date 2021/6/3 14:50
     */
    @GetMapping(value = "/file/getPicture")
    List<Map<String, Object>> getPicture(@RequestParam(value = "fileIds") String fileIds, @RequestParam(value = "code") String code);

    /**
     * 通过URL List上传图片
     *
     * @param fileVO 文件图片实体类
     * @author yujunhong
     * @date 2021/6/22 9:16
     */
    @PostMapping(value = "/upload/uploadPictureUrl")
    ResultBody uploadPictureByUrl(FileVO fileVO);

    /**
     * 初始化网盘大小
     *
     * @param adminUid       用户UID
     * @param maxStorageSize 容量大小
     * @author yujunhong
     * @date 2021/6/22 9:20
     */
    @PostMapping(value = "/storage/initStorageSize")
    void initStorageSize(@RequestParam(value = "adminUid") String adminUid,
                         @RequestParam(value = "maxStorageSize") Long maxStorageSize);

    /**
     * 修改网盘大小
     *
     * @param adminUid       用户UID
     * @param maxStorageSize 容量大小
     * @author yujunhong
     * @date 2021/6/22 9:23
     */
    @PostMapping(value = "/storage/updateStorageSize")
    void updateStorageSize(@RequestParam(value = "adminUid") String adminUid,
                           @RequestParam(value = "maxStorageSize") Long maxStorageSize);

    /**
     * 通过管理员adminUid 获取存储信息
     *
     * @param adminUidList 用户uid列表
     * @return 存储信息
     * @author yujunhong
     * @date 2021/6/22 9:25
     */
    @GetMapping(value = "/storage/getStorageByAdminUid")
    String getStorageByAdminUid(@RequestParam(value = "adminUidList") List<String> adminUidList);
}
