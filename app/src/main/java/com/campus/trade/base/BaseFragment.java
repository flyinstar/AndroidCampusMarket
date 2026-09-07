package com.campus.trade.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment 基类：自动创建/绑定/解绑 Presenter
 *
 * @param <V> View 契约（Fragment 实现并作为 Presenter 绑定视图）
 * @param <P> Presenter
 */
public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>>
        extends Fragment implements BaseView {

    protected P mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(asView());
        }
        initViews(view);
        initListeners();
        initData();
    }

    @SuppressWarnings("unchecked")
    protected V asView() {
        return (V) this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /** 返回布局 ID */
    protected abstract int getLayoutId();

    /** 创建 Presenter（可为 null） */
    protected abstract P createPresenter();

    protected void initViews(View view) {
    }

    protected void initListeners() {
    }

    protected void initData() {
    }
}
