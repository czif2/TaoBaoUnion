package com.example.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.base.IBaseInfo;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.example.taobaounion.ui.adapter.SearchResultAdapter;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.TicketUtils;
import com.example.taobaounion.utils.keyboardUtils;
import com.example.taobaounion.view.ISearchViewCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchViewCallback, TextFlowLayout.onFlowTextItemClickListener {

    @BindView(R.id.search_recommend)
    public TextFlowLayout mTextFlowLayout;

    @BindView(R.id.search_history)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoryContainer;

    @BindView(R.id.search_delete_history)
    public View mHistoryDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mSearchCleanBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;


    private ISearchPresenter mSearchPresenter;
    private HomePagerContentAdapter mSearchResultAdapter;

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //获取推荐词
        mSearchPresenter.getRecommendWords();
//        mSearchPresenter.doSearch("排球");
        mSearchPresenter.getHistory();
    }

    @Override
    protected void release() {
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout,container,false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initListener() {
        mHistoriesView.setonFlowTextItemClickListener(this);
        mTextFlowLayout.setonFlowTextItemClickListener(this);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchInputBox.getText().toString().trim().length() > 0) {
                    if (mSearchPresenter != null) {
                        mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        keyboardUtils.hide(getContext(),view);
                    }
                }else {

                }

            }
        });
        mSearchCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchInputBox.setText("");
                mRefreshContainer.setVisibility(View.GONE);
                mHistoryContainer.setVisibility(View.VISIBLE);
                mRecommendContainer.setVisibility(View.VISIBLE);
            }
        });

        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchCleanBtn.setVisibility(charSequence.length()>0?View.VISIBLE:View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH&&mSearchPresenter!=null){
                    String keyword = textView.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)){
                        return false;
                    }
                    mSearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchPresenter.deleteHistory();
            }
        });
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });
        mSearchResultAdapter.setListItemClickListener(new HomePagerContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo data) {
                TicketUtils.toTicketPage(getContext(),data);
            }
        });
    }

    protected void initView(View rootView) {

        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchResultAdapter = new HomePagerContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
    }

    @Override
    public void onNetWorkError() {
        setUpStates(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpStates(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpStates(State.EMPTY);
    }

    @Override
    public void onHistoryLoad(Histories history) {
        if (history==null||history.getHistories().size()==0){
            mHistoryContainer.setVisibility(View.GONE);
        }else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(history.getHistories());
        }
    }

    @Override
    public void onHistoryDelete() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistory();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpStates(State.SUCCESS);
        mRecommendContainer.setVisibility(View.GONE);
        mHistoryContainer.setVisibility(View.GONE);
        mRefreshContainer.setVisibility(View.VISIBLE);
        try{
            mSearchResultAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());

        }catch (Exception e){
            e.printStackTrace();
            setUpStates(State.EMPTY);
        }


        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top=8;
                outRect.bottom=8;
            }
        });

    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addData(moreData);

    }

    @Override
    public void onMoreLoadedError() {
        mRefreshContainer.finishLoadmore();
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshContainer.finishLoadmore();
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {

        LogUtils.d(this,"推荐——————————>"+ recommendWords.size());
        List<String> recommendKeywords=new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            recommendKeywords.add(item.getKeyword());
        }
        mTextFlowLayout.setTextList(recommendKeywords);
        mRecommendContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFlowTextItemClick(String text) {
        if (mSearchPresenter != null) {

            mSearchPresenter.doSearch(text);
        }
        keyboardUtils.hide(getContext(),mHistoriesView);
    }
}
