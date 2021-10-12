package com.blog.business.admin.domain.vo.ServerInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统相关信息
 *
 * @author yujunhong
 * @date 2021/10/11 16:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sys {
    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器Ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;
}
