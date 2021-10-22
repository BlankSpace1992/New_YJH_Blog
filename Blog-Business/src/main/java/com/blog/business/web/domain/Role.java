package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yujunhong
 * @date 2021/06/01 11:09:03
 */
@ApiModel(value = "角色表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_role")
public class Role {
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    @Excel(name = "角色id")
    @TableId(value = "uid", type = IdType.ASSIGN_UUID)
    private String uid;
    /**
     * 角色名
     */
    @ApiModelProperty(value = "角色名")
    @Excel(name = "角色名")
    private String roleName;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
    /**
     * 角色介绍
     */
    @ApiModelProperty(value = "角色介绍")
    @Excel(name = "角色介绍")
    private String summary;
    /**
     * 角色管辖的菜单的UID
     */
    @ApiModelProperty(value = "角色管辖的菜单的UID")
    @Excel(name = "角色管辖的菜单的UID")
    private String categoryMenuUids;
}
