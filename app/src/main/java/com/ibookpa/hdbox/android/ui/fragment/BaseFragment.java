package com.ibookpa.hdbox.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibookpa.hdbox.android.ui.listener.OnDataRefreshListener;

/**
 * Created by tc on 6/25/16. Fragment 基础类
 */
public abstract class BaseFragment extends Fragment {

    abstract public void refreshData();

    abstract int setViewId();

    abstract void initViews(View view);

    protected OnDataRefreshListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setViewId(), container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnDataRefreshListener(OnDataRefreshListener listener) {
        mListener = listener;
    }

    protected void startRefresh() {
        if (mListener != null) {
            mListener.onStartRefresh();
        }
    }

    protected void stopRefresh() {
        if (mListener != null) {
            mListener.onStopRefresh();
        }
    }


    protected void showLoading(){

    }

    protected void showPleaseLogin(){

    }

    protected void showDataView(){

    }
}
