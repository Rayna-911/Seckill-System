package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecutor;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * Created by chizhou on 12/18/17.
 */

//Spring 注解包括@Commponent @Service @Dao @Controller

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Spring依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //MD5盐值字符串， 用于混淆MD5
    private final String slat = "dfsadsghpoivmlkwejri$#$%^dsfSAFRT2426568";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,7);
    }

    public Seckill getById(final long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exposeSeckillUrl(final long seckillId) {

        //优化点:缓存优化：超时的基础上维护一致性
        //1: 访问Redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2:访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3:放入Redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date now = new Date();

        if (startTime.getTime() > now.getTime() || endTime.getTime() < now.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime
                    .getTime());
        }

        //转化特定字符串的过程， 不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    /**
     * 使用注解控制事务方法的优点
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外
     * 3：不是所有方法都需要事务，如只有一条修改操作、只读操作不需要事务控制
     */
    public SeckillExecutor executeSeckill(final long seckillId, final long userPhone, final String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("Seckill data has been rewritten.");
        }

        Date now = new Date();

        try {

            //先记录购买行为，再减库存，这样可以减少行级锁持有时间，因为insert冲突的可能性低

            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("Seckill is repeated.");
            } else {
                //减库存,热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, now);
                if (updateCount <= 0 ) {
                    //没有更新到记录，秒杀结束，rollback
                    throw new SeckillCloseException("Seckill has closed");
                } else {
                    //秒杀成功，commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSuccessKill
                            (seckillId, userPhone);
                    return new SeckillExecutor(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译异常转化为运行期异常
            throw new SeckillException("Seckill internal error: " + e.getMessage());
        }
    }

    public SeckillExecutor executeSeckillProcedure(final long seckillId, final long userPhone,
                                                   final String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecutor(seckillId, SeckillStateEnum.REWRITE);
        }

        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSuccessKill
                        (seckillId, userPhone);
                return new SeckillExecutor(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecutor(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecutor(seckillId, SeckillStateEnum.INTERNAL_ERROR);
        }
    }
}
