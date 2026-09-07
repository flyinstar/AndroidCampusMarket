package com.campus.trade.ui.activity;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.base.BasePresenter;
import com.campus.trade.base.BaseView;
import com.campus.trade.ui.fragment.CategoryFragment;
import com.campus.trade.ui.fragment.HomeFragment;
import com.campus.trade.ui.fragment.MessageFragment;
import com.campus.trade.ui.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * 主界面：底部 首页/分类/消息/我的 + 悬浮发布
 */
public class MainActivity extends BaseActivity<BaseView, BasePresenter<BaseView>> {

    private Fragment[] mFragments;
    private int mCurrentIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter<BaseView> createPresenter() {
        return null;
    }

    @Override
    protected void initViews() {
        mFragments = new Fragment[]{
                new HomeFragment(),
                new CategoryFragment(),
                new MessageFragment(),
                new ProfileFragment()
        };
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        FloatingActionButton fab = findViewById(R.id.fab_publish);
        bottomNav.setOnItemSelectedListener(item -> {
            int index = item.getItemId() == R.id.tab_home ? 0
                    : item.getItemId() == R.id.tab_category ? 1
                    : item.getItemId() == R.id.tab_message ? 2 : 3;
            switchTo(index);
            return true;
        });
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, PublishActivity.class)));
        bottomNav.setSelectedItemId(R.id.tab_home);
    }

    private void switchTo(int index) {
        if (index == mCurrentIndex) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment target = mFragments[index];
        if (mCurrentIndex >= 0 && mFragments[mCurrentIndex].isAdded()) {
            ft.hide(mFragments[mCurrentIndex]);
        }
        if (target.isAdded()) {
            ft.show(target);
        } else {
            ft.add(R.id.fragment_container, target);
        }
        ft.commitAllowingStateLoss();
        mCurrentIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentIndex < 0 && mFragments != null) {
            switchTo(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragments = null;
    }
}
