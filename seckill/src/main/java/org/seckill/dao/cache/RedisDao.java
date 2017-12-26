package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by chizhou on 12/23/17.
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool; //类似数据库连接池ConnectionPool

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //不用访问DB, 直接通过Redis拿到Seckill
    public Seckill getSeckill(long seckillId) {
        //Redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "seckill:" + seckillId;
                //不同于Memcached，Redis/Jedis并没有实现内部序列化操作
                //逻辑为：get -> byte[] -> 反序列化 -> Object(Seckill)
                //采用自定义序列化: protostuff; see comparison at https://github.com/eishay/jvm-serializers/wiki
                //protostuff: pojo. pojo是一个class，但不能是String，Long这种的class，
                //pojo的class应该是有自己属性的class，如Seckill

                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //说明在缓存中找到
                    //空对象
                    Seckill seckill = schema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
                    //seckill被反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    //缓存没有时候，putSeckill
    public String putSeckill(Seckill seckill) {
        //逻辑为：set Object(Seckill) -> 序列化 -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate
                        (LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60; //一小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
