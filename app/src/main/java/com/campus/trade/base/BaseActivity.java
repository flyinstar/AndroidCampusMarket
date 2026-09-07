package com.campus.trade.base;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity 基类：自动创建/绑定/解绑 Presenter
 *
 * @param <V> View 契约（Activity 实现并作为 Presenter 绑定视图）
 * @param <P> Presenter
 */
public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>>
        extends AppCompatActivity implements BaseView {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mPresenter = createPresenter();
        if (mPresenter != null) {
            // 具体子类已实现契约 V，此处做类型转换并绑定
            mPresenter.attachView(asView());
        }
        initViews();
        initListeners();
        initData();
    }

    @SuppressWarnings("unchecked")
    protected V asView() {
        return (V) this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** 返回布局 ID */
    protected abstract int getLayoutId();

    /** 创建 Presenter（可为 null） */
    protected abstract P createPresenter();

    protected void initViews() {
    }

    protected void initListeners() {
    }

    protected void initData() {
    }
}
