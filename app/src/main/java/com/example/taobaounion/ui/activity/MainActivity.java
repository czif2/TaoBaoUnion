package com.example.taobaounion.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.OnSellFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.example.taobaounion.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{

    private static final String TAG = "MainActivity";
    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private SearchFragment mSearchFragment;
    private OnSellFragment mRetPacketFragment;
    private SelectedFragment mSelectedFragment;
    private FragmentManager mFm;




    @Override
    protected void initView() {
        initFragment();
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    private void initFragment() {
        mHomeFragment = new HomeFragment();
        mSearchFragment = new SearchFragment();
        mRetPacketFragment = new OnSellFragment();
        mSelectedFragment = new SelectedFragment();
        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);
    }

    public void switchToSearch(){
        switchFragment(mSearchFragment);
        mNavigationView.setSelectedItemId(R.id.search);
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Log.d(TAG,"item-->"+item.getTitle()+"id-->"+item.getItemId());
                if (item.getItemId()==R.id.home){
                   LogUtils.d(this,"切换到主页");
                   switchFragment(mHomeFragment);
                }else if (item.getItemId()==R.id.selected){
                    LogUtils.d(this,"切换到精选");
                    switchFragment(mSelectedFragment);
                }else if (item.getItemId()==R.id.red_packet){
                    LogUtils.d(this,"切换到特惠");
                    switchFragment(mRetPacketFragment);
                }else if (item.getItemId()==R.id.search){
                    LogUtils.d(this,"切换到搜索");
                    switchFragment(mSearchFragment);
                }
                return true;
            }
        });
    }
    private BaseFragment lastFragment=null;

    private void switchFragment(BaseFragment targetFragment) {
        FragmentTransaction fragmentTransaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()){
            fragmentTransaction.add(R.id.main_page_container,targetFragment);
        }else {
            fragmentTransaction.show(targetFragment);
        }
        if (lastFragment!=null){
            fragmentTransaction.hide(lastFragment);
        }
        if (lastFragment==targetFragment){
            return;
        }else lastFragment=targetFragment;
        fragmentTransaction.commit();
    }

//    private void initView() {
//        HomeFragment homeFragment=new HomeFragment();
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.add(R.id.main_page_container,homeFragment);
//        transaction.commit();
//    }
}