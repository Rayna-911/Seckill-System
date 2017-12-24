package org.seckill.exception;

/**
 * 秒杀相关业务异常
 * Created by chizhou on 12/17/17.
 */
public class SeckillException extends RuntimeException {

    public SeckillException(final String message) {
        super(message);
    }

    public SeckillException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
