package com.ibookpa.hdbox.android.manager;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ibookpa.hdbox.android.app.Constant;

/**
 * Created by tc on 7/25/16. 权限管理类
 */
public class PermissionManager {

    /**
     * 检查权限,如果没有权限,则申请权限,返回 false,如果有权限了,则返回 true
     *
     * @return true if has permission,false else
     */
    public static boolean checkPermission(Activity mAty, String permission) {
        if (ContextCompat.checkSelfPermission(mAty, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mAty, new String[]{permission}, Constant.PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }
}
