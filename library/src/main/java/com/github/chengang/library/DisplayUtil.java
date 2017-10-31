package com.github.chengang.library;

import android.content.Context;

/**
 * Created by 陈岗不姓陈 on 2017/10/30.
 * <p>
 */

class DisplayUtil {
    static int dp2px(Context context, float dpValue) {
        if (context == null) return (int) (dpValue * 1.5f + 0.5f);
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
