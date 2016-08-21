package com.ibookpa.hdbox.android.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ibookpa.hdbox.android.app.BaseApplication;
import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.dialog.CustomDialog;
import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.network.ApiService;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.Shared;
import com.ibookpa.hdbox.android.utils.DateUtil;
import com.ibookpa.hdbox.android.utils.ToastUtil;

import java.util.List;

/**
 * Created by tc on 7/7/16. 更新管理类
 */
public class UpdateManager {

    private Context mContext;
    private static UpdateManager mInstance;

    private UpdateManager() {
        mContext = BaseApplication.get().getAppContext();
    }

    public static UpdateManager getInstance() {
        if (mInstance == null) {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }

    /**
     * 检查更新
     */
    public void checkUpdate(final Context context) {

        SchoolApiService.get().checkUpdate(new HttpResultListener<CheckUpdateModel>() {
            @Override
            public void onSuccess(CheckUpdateModel checkUpdateModel) {
                if (checkUpdateModel != null && checkUpdateModel.getHasNew() == 1) {
                    CustomDialog.showNewVersionDialog(context, checkUpdateModel);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG", "--tc-->checkUpdate error:" + e.toString());
            }
        }, getVersionCode());
    }

    /**
     * 获取当前应用版本号
     */
    public String getVersionCode() {
        int code = 0;
        try {
            code = mContext.getPackageManager().getPackageInfo(Constant.PKG_NAME, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("TAG","--tc-->check update version is:"+code);
        return String.valueOf(code);
    }
}
