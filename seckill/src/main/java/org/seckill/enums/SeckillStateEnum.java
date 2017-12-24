package org.seckill.enums;

/**
 * 使用枚举表示常量数据字段
 * Created by chizhou on 12/18/17.
 */
public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT(-1, "重复秒杀"),
    INTERNAL_ERROR(-2, "系统异常"),
    REWRITE(-3, "数据篡改");

    private int state;

    private String stateInfo;

    SeckillStateEnum(final int state, final String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStateEnum stateOf(final int index) {
        for (SeckillStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }

        return null;
    }
}
