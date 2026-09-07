package com.campus.trade.base;

import java.lang.ref.WeakReference;

/**
 * MVP - Presenter 基类（弱引用防止内存泄漏）
 */
public abstract class BasePresenter<V extends BaseView> {

    private WeakReference<V> mViewRef;

    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    protected V getView() {
        return mViewRef == null ? null : mViewRef.get();
    }

    protected boolean isViewAttached() {
        return getView() != null;
    }
}
