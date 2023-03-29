package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.taobaounion.R;

public class LoadingView extends androidx.appcompat.widget.AppCompatImageView{
    private float mDegrees=30;
    private boolean needRotate=true;
    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onAttachedToWindow() {
        needRotate=true;
        startRotate();
        super.onAttachedToWindow();
    }

    private void startRotate() {

        post(new Runnable() {
            @Override
            public void run() {
                mDegrees+=10;
                if (mDegrees>=360){
                    mDegrees=0;
                }
                invalidate();
                if (getVisibility()!=VISIBLE&&!needRotate){
                    removeCallbacks(this);
                }else {
                    postDelayed(this,10);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        needRotate=false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegrees,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
