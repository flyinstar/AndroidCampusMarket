package com.campus.trade.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.Product;
import com.campus.trade.presenter.user.FavoritesContract;
import com.campus.trade.presenter.user.FavoritesPresenter;
import com.campus.trade.ui.adapter.ProductAdapter;

import java.util.List;

/**
 * 我的收藏页
 */
public class FavoritesActivity extends BaseActivity<FavoritesContract.View, FavoritesPresenter>
        implements FavoritesContract.View {

    private ProductAdapter mAdapter;
    private SwipeRefreshLayout srl;
    private View llEmpty;
    private RecyclerView rv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected FavoritesPresenter createPresenter() {
        return new FavoritesPresenter();
    }

    @Override
    protected void initViews() {
        srl = findViewById(R.id.srl_list);
        llEmpty = findViewById(R.id.ll_empty);
        rv = findViewById(R.id.rv_list);
        GridLayoutManager grid = new GridLayoutManager(this, 2);
        rv.setLayoutManager(grid);
        mAdapter = new ProductAdapter();
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        srl.setOnRefreshListener(() -> mPresenter.load());
        mAdapter.setOnItemClickListener(product ->
                startActivity(ProductDetailActivity.newIntent(this, product.getId())));
    }

    @Override
    protected void initData() {
        mPresenter.load();
    }

    @Override
    public void onFavorites(List<Product> products) {
        srl.setRefreshing(false);
        mAdapter.replace(products);
        llEmpty.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(mAdapter.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.load();
        }
    }
}
