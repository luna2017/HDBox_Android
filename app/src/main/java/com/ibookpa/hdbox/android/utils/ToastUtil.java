package com.ibookpa.hdbox.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by tc on 7/25/16. 吐司提示类
 */
public class ToastUtil {


    /**
     * 显示默认的提示,短时间
     *
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
