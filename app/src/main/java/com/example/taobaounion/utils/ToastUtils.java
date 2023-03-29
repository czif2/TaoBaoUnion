package com.example.taobaounion.utils;

import android.widget.Toast;

import com.example.taobaounion.base.BaseApplication;

public class ToastUtils {

    private static Toast sToast;

    public static void showToast(String text){
        if (sToast==null){
            sToast = Toast.makeText(BaseApplication.getAppContext(), text, Toast.LENGTH_SHORT);
        }else {
            sToast.setText(text);
        }
        sToast.show();
    }
}
