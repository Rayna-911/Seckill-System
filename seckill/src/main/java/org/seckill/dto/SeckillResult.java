package org.seckill.dto;

/**
 * 所有ajax请求的返回类型是json
 * 封装json结果
 * Created by chizhou on 12/19/17.
 */

public class SeckillResult<T> {

    private boolean success;

    private T data;

    private String error;

    public SeckillResult(final boolean success, final T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(final boolean success, final String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
