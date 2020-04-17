package com.shelton.onelook.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.DisplayMetrics;


public class MyUtil {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @SuppressWarnings("unused")
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断软键盘是否弹出
     *
     * @return *
     */
    public static boolean isSoftInputMethodShowing(Activity context) {

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;   //这个方法获取可能不是真实屏幕的高度(可能有虚拟导航栏)

        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return usableHeight - rect.bottom != 0;
    }

    public static int getNavigationBarHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        return realHeight - usableHeight;
    }

    public static void createDialog(Context context, String title, String message, String positiveButtonText,
                                    DialogInterface.OnClickListener onPositiveListener,
                                    DialogInterface.OnClickListener onNegativeListener) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        AlertDialog dialog = normalDialog.setIcon(android.R.drawable.ic_menu_info_details)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, onPositiveListener)
                .setNegativeButton("取消", onNegativeListener).show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

}
