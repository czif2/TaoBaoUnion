package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.base.IBaseInfo;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagePresenterImpl;
import com.example.taobaounion.presenter.impl.TicketPresenterImpl;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.ui.custom.AutoLoopViewPager;
import com.example.taobaounion.utils.Constant;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtils;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.utils.views.TbNestScrollView;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, HomePagerContentAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPageItemClickListener {

    private ICategoryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;
    private HomePagerContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category){
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_HOME_PAGER_TITLE,category.getTitle());
        bundle.putInt(Constant.KEY_HOME_PAGER_MATERIAL_ID,category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitle;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_nested_scroll)
    public TbNestScrollView homePagerNestedView;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void onResume() {
        super.onResume();
        looperPager.startLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
        looperPager.stopLoop();
    }

    @Override
    protected void initListener() {
        mContentAdapter.setListItemClickListener(this);
        mLooperPagerAdapter.setLooperPageItemClickListener(this);

        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homeHeaderContainer==null){
                    return;
                }
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
                LogUtils.d(this,"headerHeight----->"+headerHeight);
                homePagerNestedView.setHeadHeight(headerHeight);
                int measuredHeight = homePagerParent.getMeasuredHeight();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                layoutParams.height=measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight!=0){
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mLooperPagerAdapter.getDataSize()==0){
                    return;
                }
                int target=position%mLooperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(target);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //加载更多内容
                if (mCategoryPagePresenter!=null) {
                    mCategoryPagePresenter.loaderMore(mMaterialId);
                }
            }
        });
    }

    private void updateLooperIndicator(int target) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i==target){
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top=8;
                outRect.bottom=8;
            }
        });
        //创建适配器
        mContentAdapter = new HomePagerContentAdapter();
        //设置适配器
        mContentList.setAdapter(mContentAdapter);

        //轮播图创建适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        //设置适配器
        looperPager.setAdapter(mLooperPagerAdapter);

        //设置刷新相关属性
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = PresenterManager.getInstance().getCategoryPagePresenter();
        mCategoryPagePresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constant.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constant.KEY_HOME_PAGER_MATERIAL_ID);
        LogUtils.d(this,"title-->"+title);
        LogUtils.d(this,"id-->"+ mMaterialId);

        //加载数据
        if (mCategoryPagePresenter!=null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }

        if (currentCategoryTitle != null) {
            currentCategoryTitle.setText(title);
        }
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {

        //数据列表加载
        mContentAdapter.setData(contents);

        setUpStates(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onLoading() {

        setUpStates(State.LOADING);
    }

    @Override
    public void onError() {

        //网络错误
        setUpStates(State.ERROR);
    }

    @Override
    public void onEmpty() {

        setUpStates(State.EMPTY);
    }

    @Override
    public void onLoadMoreError() {
        ToastUtils.showToast("网络异常，请重试");
        if (mTwinklingRefreshLayout!=null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoadMoreEmpty() {
        ToastUtils.showToast("没有更多商品");
        if (mTwinklingRefreshLayout!=null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //如果还有数据，添加到适配器底部
        mContentAdapter.addData(contents);

        if (mTwinklingRefreshLayout!=null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("加载了"+contents.size()+"条记录");
    }

    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this,"size-->"+contents.size());
        mLooperPagerAdapter.setData(contents);
        looperPointContainer.removeAllViews();


        int dx=(Integer.MAX_VALUE/2)%contents.size();
        int targetCenterPosition=(Integer.MAX_VALUE/2)-dx;
        //设置中间点
        looperPager.setCurrentItem(targetCenterPosition);
        //添加轮播图的点
        for (int i = 0; i < contents.size(); i++) {
            View point=new View(getContext());
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(SizeUtils.dip2px(getContext(),8),SizeUtils.dip2px(getContext(),8));
            layoutParams.leftMargin=SizeUtils.dip2px(getContext(),5);
            layoutParams.rightMargin=SizeUtils.dip2px(getContext(),5);
            point.setLayoutParams(layoutParams);
            if (i==0){
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release() {
        if (mCategoryPagePresenter!=null) {
            mCategoryPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo data) {
        LogUtils.d(this,"click----"+data.getTitle());
        handleItemClick(data);
    }

    private void handleItemClick(IBaseInfo data) {
        TicketUtils.toTicketPage(getContext(),data);
    }

    @Override
    public void onLooperItemClick(IBaseInfo data) {
         LogUtils.d(this,"looperClick------"+data.getTitle());
        handleItemClick(data);
    }
}
