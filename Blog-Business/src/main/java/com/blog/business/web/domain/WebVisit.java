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
 * Web访问记录表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:12
 */
@ApiModel(value = "Web访问记录表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_web_visit")
public class WebVisit {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 用户uid
     */
    @ApiModelProperty(value = "用户uid")
    @Excel(name = "用户uid")
    private String userUid;
    /**
     * 访问ip地址
     */
    @ApiModelProperty(value = "访问ip地址")
    @Excel(name = "访问ip地址")
    private String ip;
    /**
     * 用户行为
     */
    @ApiModelProperty(value = "用户行为")
    @Excel(name = "用户行为")
    private String behavior;
    /**
     * 模块uid（文章uid，标签uid，分类uid）
     */
    @ApiModelProperty(value = "模块uid（文章uid，标签uid，分类uid）")
    @Excel(name = "模块uid（文章uid，标签uid，分类uid）")
    private String moduleUid;
    /**
     * 附加数据(比如搜索内容)
     */
    @ApiModelProperty(value = "附加数据(比如搜索内容)")
    @Excel(name = "附加数据(比如搜索内容)")
    private String otherData;
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
    /**
     * 操作系统
     */
    @ApiModelProperty(value = "操作系统")
    @Excel(name = "操作系统")
    private String os;
    /**
     * 浏览器
     */
    @ApiModelProperty(value = "浏览器")
    @Excel(name = "浏览器")
    private String browser;
    /**
     * ip来源
     */
    @ApiModelProperty(value = "ip来源")
    @Excel(name = "ip来源")
    private String ipSource;
}
