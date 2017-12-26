package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecutor;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by chizhou on 12/19/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})

public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("List = {}", seckillList);
    }

    @Test
    public void getById() throws Exception {
        long seckillId = 1000;
        Seckill seckill = seckillService.getById(seckillId);
        logger.info("seckill = {}", seckill);
    }

    //集成测试代码完整逻辑，可重复执行
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1002;
        Exposer exposer = seckillService.exposeSeckillUrl(id);

        if (exposer.isExposed()) {
            logger.info("exposer = {}", exposer);

            long phone = 12343562346L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecutor seckillExecutor = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecutor = {}", seckillExecutor);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            //秒杀未开启
            logger.warn("exposer = {}", exposer);
        }
    }

    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1005;
        long userphone = 12347789987L;
        Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecutor seckillExecutor = seckillService.executeSeckillProcedure(seckillId,
                    userphone, md5);
            logger.info(seckillExecutor.getStateInfo());
        }

    }
}