package com.example.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketCallback {

    private ITicketPresenter mTicketPresenter;

    private boolean hasTaoBao=false;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_back_press)
    public View backPress;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_copy_open_btn)
    public TextView mOpenOrCopyBtn;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView;

    @BindView(R.id.ticket_load_retry)
    public View retryLoadText;


    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }

        //判断是否安装淘宝
        //com.taobao.taobao

        PackageManager pm = getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            hasTaoBao=packageInfo!=null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            hasTaoBao=false;
        }
        LogUtils.d(this,"hasTaobao--->"+hasTaoBao);
        mOpenOrCopyBtn.setText(hasTaoBao?"打开淘宝领券":"复制淘口令");

    }
    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回页面
                finish();
            }
        });

        mOpenOrCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制淘口令
                String code = mTicketCode.getText().toString().trim();
                LogUtils.d(this,"zhantieticket----->"+code);
                ClipboardManager cm= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴版
                ClipData clipData = ClipData.newPlainText("ticket_code", code);
                cm.setPrimaryClip(clipData);
                //判断有没有淘宝，又就打开淘宝，没有就提示复制成功
                if (hasTaoBao){
                    Intent taobaoIntent=new Intent();
//                    taobaoIntent.setAction("android.intent.action.MAIN");
//                    taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    ComponentName componentName=new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                }else {
                    ToastUtils.showToast("已经复制,粘贴分享,或打开淘宝");
                }
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onNetWorkError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText!=null){
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (retryLoadText!=null){
            retryLoadText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText!=null){
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mCover!=null&&!TextUtils.isEmpty(cover)){
            ViewGroup.LayoutParams layoutParams = mCover.getLayoutParams();
            String coverPath = UrlUtils.getCoverPath(cover);
            Glide.with(this).load(coverPath).into(mCover);
        }
        if (result!=null&&result.getData().getTbk_tpwd_create_response()!=null){
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
    }
}
