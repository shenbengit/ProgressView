package com.shencoder.progressview;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 由于Kotlin对{@link IntDef}存在编译期间不检查问题，改用Java注解
 *
 * @author ShenBen
 * @date 2021/6/8 9:55
 * @email 714081644@qq.com
 */
@IntDef(value = {CircleProgressViewStyle.RING, CircleProgressViewStyle.SCALE, CircleProgressViewStyle.WAVE})
@Retention(RetentionPolicy.SOURCE)
public @interface CircleProgressViewStyle {
    /**
     * 圆环样式
     */
    int RING = 1;
    /**
     * 刻度尺样式
     */
    int SCALE = 2;
    /**
     * 波浪样式
     */
    int WAVE = 3;
}
