package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taobaounion.R;

import java.util.ArrayList;
import java.util.List;

public class TextFlowLayout  extends ViewGroup {

    public static final int DEFAULT_SPACE=10;
    private float mItemHorizontalSpace=DEFAULT_SPACE;
    private float mItemVerticalSpace=DEFAULT_SPACE;
    private int mSelfWidth;
    private onFlowTextItemClickListener mItemClickListener=null;

    public float getItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        mItemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    private List<String> mTextList=new ArrayList<>();

    public TextFlowLayout(Context context) {
        this(context,null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //拿到相关属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace=ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace,DEFAULT_SPACE);
        mItemVerticalSpace=ta.getDimension(R.styleable.FlowTextStyle_verticalSpace,DEFAULT_SPACE);
        ta.recycle();
    }

    public void setTextList(List<String> textList){
        this.mTextList=textList;
        for (String text : mTextList) {
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowTextItemClick(text);
                    }
                }
            });
            addView(item);
        }
    }


    private List<List<View>> lines=new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount()==0){
            return;
        }

        List<View> line=null;

        lines.clear();

        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            measureChild(itemView,widthMeasureSpec,heightMeasureSpec);

            if (line==null){
                line=createNewLine(itemView);
            }else {
                if (canBeAdded(itemView,line)) {
                    line.add(itemView);
                }else {
                    line=createNewLine(itemView);
                }
            }
        }
        int selfHeight= (int) (lines.size()*getChildAt(0).getMeasuredHeight()+mItemVerticalSpace*(lines.size()+1)+0.5F);
        setMeasuredDimension(mSelfWidth,selfHeight);
    }

    private List<View> createNewLine(View itemView) {
        List<View>line=new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    private boolean canBeAdded(View itemView, List<View> line) {
        int total=itemView.getMeasuredWidth();
        for (View view : line) {
            total+=itemView.getMeasuredWidth();
        }
        total+=mItemHorizontalSpace*(line.size()+1);
        if (total<=mSelfWidth){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int topOffset=(int) mItemHorizontalSpace;
        for (List<View> views:lines){
            int leftOffset=(int) mItemHorizontalSpace;
            for (View view:views){
                view.layout(leftOffset,topOffset,view.getMeasuredWidth()+leftOffset,topOffset+view.getMeasuredHeight());
                leftOffset+=view.getMeasuredWidth()+mItemHorizontalSpace;
            }
            topOffset+=getChildAt(0).getMeasuredHeight()+mItemVerticalSpace;
        }
    }

    public void setonFlowTextItemClickListener(onFlowTextItemClickListener listener){
        this.mItemClickListener=listener;
    }

    public interface onFlowTextItemClickListener{
        void onFlowTextItemClick(String text);
    }
}
