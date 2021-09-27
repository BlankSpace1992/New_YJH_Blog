package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.WebConfigVO;
import com.blog.business.web.domain.WebConfig;
import com.blog.exception.ResultBody;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebConfigService extends IService<WebConfig> {

    /**
     * 通过显示列表获取配置信息
     *
     * @return 配置信息
     * @author yujunhong
     * @date 2021/6/2 13:58
     */
    WebConfig getWebConfigByShowList();

    /**
     * 是否开启该登录方式【账号密码、码云、Github、QQ、微信】
     *
     * @param loginType 登陆方式
     * @return 是否开启该登录方式
     * @author yujunhong
     * @date 2021/6/2 13:58
     */
    Boolean isOpenLoginType(String loginType);

    /**
     * 获取网站名称
     *
     * @return 获取网站名称
     * @author yujunhong
     * @date 2021/9/18 14:41
     */
    ResultBody getWebSiteName();

    /**
     * 获取网站配置
     *
     * @return 获取网站配置
     * @author yujunhong
     * @date 2021/9/27 11:33
     */
    WebConfig getWebConfig();

    /**
     * 修改网站配置
     *
     * @param webConfigVO 修改实体
     * @return 修改网站配置
     * @author yujunhong
     * @date 2021/9/27 11:33
     */
    ResultBody editWebConfig(WebConfigVO webConfigVO);
}
