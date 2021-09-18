package com.blog.business.admin.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 管理员查询条件
 *
 * @author yujunhong
 * @date 2021/9/16 14:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminVO {
    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别(1:男2:女)
     */
    private String gender;

    /**
     * 个人头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 出生年月日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    /**
     * 手机
     */
    private String mobile;

    /**
     * QQ号
     */
    private String qqNumber;

    /**
     * 微信号
     */
    private String weChat;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 自我简介最多150字
     */
    private String summary;

    /**
     * 个人履历
     */
    private String personResume;

    /**
     * github地址
     */
    private String github;

    /**
     * gitee地址
     */
    private String gitee;

    /**
     * 角色Uid
     */
    private String roleUid;

    /**
     * 已用网盘容量
     */
    private Long storageSize;

    /**
     * 最大网盘容量
     */
    private Long maxStorageSize;
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;

}
