package org.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
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

public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long id = 1000L;
        long userPhone = 123456789012L;
        int insertedCount = successKilledDao.insertSuccessKilled(id, userPhone);
        System.out.println(insertedCount);
    }

    @Test
    public void queryByIdWithSuccessKill() throws Exception {
        long id = 1000;
        long userPhone = 123456789012L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSuccessKill(id, userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}