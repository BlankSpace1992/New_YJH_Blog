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
 * 收藏表
 *
 * @author yujunhong
 * @date 2021/06/01 11:08:59
 */
@ApiModel(value = "收藏表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_collect")
public class Collect {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 用户的uid
     */
    @ApiModelProperty(value = "用户的uid")
    @Excel(name = "用户的uid")
    private String userUid;
    /**
     * 博客的uid
     */
    @ApiModelProperty(value = "博客的uid")
    @Excel(name = "博客的uid")
    private String blogUid;
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
