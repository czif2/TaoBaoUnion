package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class AutoLoopViewPager extends ViewPager {


    public AutoLoopViewPager(@NonNull Context context) {
        super(context);
    }

    public AutoLoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isLoop=false;
    public void startLoop(){
        isLoop=true;
        post(new Runnable() {
            @Override
            public void run() {
                int currentItem = getCurrentItem();
                currentItem++;
                setCurrentItem(currentItem);
                if (isLoop){
                    postDelayed(this,3000);
                }
            }
        });
    }

    public void stopLoop(){
        isLoop=false;
    }
}
