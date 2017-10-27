package com.github.chengang.library;

/**
 * Created by 陈岗不姓陈 on 2017/10/27.
 * <p>
 * 将外部的接口抽象出来
 */

public interface TickCheckable {

    /**
     * 改变状态
     *
     * @param checked 选中还是未选中
     */
    void setChecked(boolean checked);

    /**
     * @return 当前状态是否选中
     */
    boolean isChecked();

    /**
     * 改变当前的状态
     */
    void toggle();
}
