package com.github.chengang.library;

/**
 * Created by 陈岗不姓陈 on 2017/10/30.
 * <p>
 */

public interface TickAnimatorListener {
    void onAnimationStart(TickView tickView);

    void onAnimationEnd(TickView tickView);

    abstract class TickAnimatorListenerAdapter implements TickAnimatorListener {
        @Override
        public void onAnimationStart(TickView tickView) {

        }

        @Override
        public void onAnimationEnd(TickView tickView) {

        }
    }
}
