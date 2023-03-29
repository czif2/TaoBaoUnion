package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.IBaseInfo;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.model.domain.ILinearItemInfo;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePagerContentAdapter extends RecyclerView.Adapter<HomePagerContentAdapter.InnerHolder> {
    List<ILinearItemInfo> data=new ArrayList<>();
    private OnListItemClickListener mItemClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_content, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        ILinearItemInfo dataBean = data.get(position);
        //设置数据
        holder.setData(dataBean);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(dataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<? extends ILinearItemInfo> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinearItemInfo> contents) {
        //添加之前的size
        int oldSize=data.size();
        data.addAll(contents);
        //更新ui
        notifyItemRangeChanged(oldSize,contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView coverIv;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.good_after_off_prise)
        public TextView finalPriseTv;

        @BindView(R.id.goods_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.goods_sell_out)
        public TextView sellCountTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(ILinearItemInfo dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
//            LogUtils.d(this,"url--->"+dataBean.getPict_url());
            ViewGroup.LayoutParams layoutParams = coverIv.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            int coverSize=(width>height?width:height)/2;
            String cover=dataBean.getCover();
            if (!TextUtils.isEmpty(cover)) {
                System.out.println(UrlUtils.getCoverPath(dataBean.getCover()));
                Glide.with(context).load(UrlUtils.getCoverPath(dataBean.getCover()).trim()).into(this.coverIv);
            }else {
                coverIv.setImageResource(R.mipmap.ic_launcher);
            }

            String finalPrice = dataBean.getFinalPrise();
//            LogUtils.d(this,"final-->"+finalPrice);

            long coupon_amount = dataBean.getCouponAmount();
            float resultPrise=Float.parseFloat(finalPrice)-coupon_amount;
            finalPriseTv.setText(String.format("%.2f",resultPrise));
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_prise),dataBean.getCouponAmount()));
            originalPriseTv.setText(String.format(context.getString(R.string.text_goods_original_prise),finalPrice));
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count),dataBean.getVolume()));
        }
    }
    public void setListItemClickListener(OnListItemClickListener listener){
        this.mItemClickListener=listener;
    }
    public interface OnListItemClickListener{
        void onItemClick(IBaseInfo data);
    }
}
