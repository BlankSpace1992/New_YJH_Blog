package com.blog.config.redis;


import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yujunhong
 * @date 2021/4/25 10:55
 */
@Component
public class RedisUtil {
    @Autowired
    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisUtil(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  redis key
     * @param time 过期时间
     * @return 是否指定成功
     * @author yujunhong
     * @date 2021/4/25 10:55
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key redis key
     * @return 过期时间
     * @author yujunhong
     * @date 2021/4/25 14:23
     */
    public long getExpireTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key redis key
     * @return 存在/不存在
     * @author yujunhong
     * @date 2021/4/25 14:26
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key redis key 可多个
     * @author yujunhong
     * @date 2021/4/25 14:28
     */
    @SuppressWarnings("unchecked")
    public void delete(String... key) {
        if (StringUtils.isNotNull(key) && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================//

    /**
     * 普通缓存取出
     *
     * @param key redis key
     * @return value值
     * @author yujunhong
     * @date 2021/4/25 15:03
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   redis key
     * @param value value值
     * @return 是否放入成功
     * @author yujunhong
     * @date 2021/4/25 15:07
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 普通缓存放入并设置过期时间
     *
     * @param key        redis key
     * @param value      value值
     * @param expireTime 过期时间
     * @return 是否放入成功
     * @author yujunhong
     * @date 2021/4/25 15:12
     */
    public boolean set(String key, Object value, long expireTime) {
        try {
            if (expireTime > 0) {
                redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
            }
            // 如果time小于等于0 将设置无限期
            else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   redis key
     * @param delta 递增因子
     * @return 递增的返回值
     * @author yujunhong
     * @date 2021/4/25 15:15
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   redis key
     * @param delta 递减因子
     * @return 递减的返回值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    //================================Map=================================//

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public Object hashGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public Map<Object, Object> hashMapGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean hashMapSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean hashMapSet(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                return expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean hashSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置过期时间
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean hashSet(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public void hashDelete(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean hashHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 递增返回值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public double hashIncrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return 递减返回值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public double hashDecrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================//

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return set结果
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public Set<Object> setGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean setHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long setSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long setSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return 缓存长度
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long setGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return 集合对象
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public List<Object> listGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return 缓存长度
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long listGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 返回对应索引的值
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public Object listGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将值放入list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 放入成功/失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean listSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存并设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 放入成功/失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean listSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 放入成功/失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean listSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存并设置缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 放入成功/失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean listSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 修改成功/失败
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public boolean listUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public long listRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 模糊查询获取key值
     *
     * @param pattern 正则表达式
     * @return 结果set
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public Set keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 使用Redis的消息队列
     *
     * @param channel 通道
     * @param message 消息内容
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public void convertAndSend(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }


    /**
     * 根据起始结束序号遍历Redis中的list
     *
     * @param listKey
     * @param start   起始序号
     * @param end     结束序号
     * @return
     * @author yujunhong
     * @date 2021/4/25 15:19
     */
    public List<Object> rangeList(String listKey, long start, long end) {
        //绑定操作
        BoundListOperations<Object, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        //查询数据
        return boundValueOperations.range(start, end);
    }

    /**
     * 弹出右边的值 --- 并且移除这个值
     *
     * @param listKey
     */
    public Object rightPop(String listKey) {
        //绑定操作
        BoundListOperations<Object, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        return boundValueOperations.rightPop();
    }

}

