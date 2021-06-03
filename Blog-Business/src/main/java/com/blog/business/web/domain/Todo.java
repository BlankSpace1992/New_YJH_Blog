package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 代办事项表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:08
 */
@ApiModel(value = "代办事项表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_todo")
public class Todo {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    @Excel(name = "内容")
    private String text;
    /**
     * 表示事项是否完成（0：未完成 1：已完成）
     */
    @ApiModelProperty(value = "表示事项是否完成（0：未完成 1：已完成）")
    @Excel(name = "表示事项是否完成（0：未完成 1：已完成）")
    private Object done;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;
}
