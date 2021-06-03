package com.blog.picture.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.blog.constants.BaseSysConf;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Mybatis-plus自动填充
 *
 * @author yujunhong
 * @date 2021/6/3 11:33
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        setFieldValByName(BaseSysConf.CREATE_TIME, new Date(), metaObject);
        setFieldValByName(BaseSysConf.UPDATE_TIME, new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(BaseSysConf.UPDATE_TIME, new Date(), metaObject);
    }
}
