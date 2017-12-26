package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by chizhou on 12/23/17.
 */

/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * 通过spring-test，junit依赖
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class RedisDaoTest {

    private long seckillId = 1001;

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Test
    public void testSeckill() throws Exception {
        //全局测试，测试get和put
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            System.out.println("Seckill from DB: " + seckill);
            if (seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println("Redis put return message: " + result);
                seckill = redisDao.getSeckill(seckillId);
                System.out.println("Seckill from redis: " + seckill);
            }
        }
    }
}