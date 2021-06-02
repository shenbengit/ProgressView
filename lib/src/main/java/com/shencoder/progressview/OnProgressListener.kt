package com.shencoder.progressview

import androidx.annotation.UiThread

/**
 *
 * @author  ShenBen
 * @date    2021/6/2 15:27
 * @email   714081644@qq.com
 */
interface OnProgressListener {
    /**
     * @param current current progress
     * @param max max progress
     * @param percentage [current]/[max]
     */
    @UiThread
    fun onProgressChanged(current: Int, max: Int, percentage: Int)
}