package com.shelton.onelook.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.shelton.onelook.R;
import com.shelton.onelook.base.BaseActivity;

import androidx.annotation.Nullable;


@SuppressLint("Registered")
public class SwipeBackActivity extends BaseActivity {
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.left_in, 0);
        super.onCreate(savedInstanceState);
        mSwipeBackLayout = new SwipeBackLayout(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out);
    }

    public void canSwipe(boolean allowable) {
        mSwipeBackLayout.setCanTryCaptureView(allowable);
    }
}
