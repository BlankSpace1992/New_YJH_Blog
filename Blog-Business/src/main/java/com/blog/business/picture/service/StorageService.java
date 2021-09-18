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
     * ��ʼ��������С
     *
     * @param adminUid       ����Աuid
     * @param maxStorageSize �����������
     * @return ��ʼ��������С
     * @author yujunhong
     * @date 2021/9/16 15:17
     */
    ResultBody initStorageSize(String adminUid, Long maxStorageSize);

    /**
     * �༭������С
     *
     * @param adminUid       ����Աuid
     * @param maxStorageSize �����������
     * @return ��ʼ��������С
     * @author yujunhong
     * @date 2021/9/16 15:17
     */
    ResultBody editStorageSize(String adminUid, Long maxStorageSize);

    /**
     * ͨ������Աuid����ȡ�洢��Ϣ
     *
     * @param adminUidList �û�id����
     * @return �洢��Ϣ
     * @author yujunhong
     * @date 2021/9/16 15:29
     */
    ResultBody getStorageByAdminUid(List<String> adminUidList);

}
