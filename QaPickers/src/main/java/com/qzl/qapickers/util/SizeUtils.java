package com.qzl.qapickers.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * @ClassName SizeUtils
 * @Description
 * @Author swan
 * @Date 2022/6/8 18:59
 **/
public class SizeUtils {
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
