package org.seckill.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chizhou on 12/17/17.
 */

/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * 通过spring-test，junit依赖
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class SeckillDaoTest {

    // 注入DAO实现依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        long id = 1000;
        int updatedCount = seckillDao.reduceNumber(id, killTime);
        System.out.println("Updated count: " + updatedCount);
    }

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        // List<Seckill> queryAll(int offset, int limit);
        // Java没有保存行参的记录：queryAll(int offset, int limit) -> queryAll(arg0, arg1)
        // 修改接口：使用@Param("offset") int offset, @Param("limit") int limit，
        // 使得mapper中#{offset}, #{limit}明白对应的变量
        List<Seckill> seckillList = seckillDao.queryAll(0, 100);
        for(Seckill seckill : seckillList) {
            System.out.println(seckill);
        }
    }

}