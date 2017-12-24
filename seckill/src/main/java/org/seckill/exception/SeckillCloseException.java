package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by chizhou on 12/17/17.
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(final String message) {
        super(message);
    }

    public SeckillCloseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
