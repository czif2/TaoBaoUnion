package com.example.taobaounion.ui.adapter;

import android.graphics.Paint;
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
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellContentAdapter extends RecyclerView.Adapter<OnSellContentAdapter.InnerHolder> {
    private List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData=new ArrayList<>();
    private OnSellPageClickListener mContentClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean = mData.get(position);
        holder.setData(mapDataBean);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContentClickListener != null) {
                    mContentClickListener.onSellItemClick(mapDataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(OnSellContent result) {
        this.mData.clear();
        this.mData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public void onMoreLoaded(OnSellContent moreResult) {
        int oldSize=mData.size();
        List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mapData = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        this.mData.addAll(mapData);
        notifyItemRangeChanged(oldSize-1,mapData.size() );
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.on_sell_cover)
        public ImageView cover;

        @BindView(R.id.on_sell_content_title)
        public TextView title;

        @BindView(R.id.on_sell_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.on_sell_off_prise)
        public TextView offPriseTv;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean) {
            title.setText(mapDataBean.getTitle());
            String coverPath = UrlUtils.getCoverPath(mapDataBean.getPict_url());
            Glide.with(cover.getContext()).load(coverPath).into(cover);
            String originalPrise=mapDataBean.getZk_final_price();
            originalPriseTv.setText("￥"+originalPrise+" ");
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            int coupon_amount = mapDataBean.getCoupon_amount();
            float v = Float.parseFloat(originalPrise);
            float finalPrise=v-coupon_amount;
            offPriseTv.setText("券后价："+String.format("%.2f",finalPrise));
        }
    }
    public void setOnSellPageItemClickListener(OnSellPageClickListener listener){
        this.mContentClickListener=listener;
    }

    public interface OnSellPageClickListener{
        void onSellItemClick(IBaseInfo dataBean);
    }
}
