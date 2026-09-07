package com.campus.trade.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.Product;
import com.campus.trade.presenter.product.MyProductsContract;
import com.campus.trade.presenter.product.MyProductsPresenter;
import com.campus.trade.ui.adapter.MyProductsAdapter;

import java.util.List;

/**
 * 我的发布页：按状态筛选、上下架、删除
 */
public class MyProductsActivity extends BaseActivity<MyProductsContract.View, MyProductsPresenter>
        implements MyProductsContract.View, MyProductsAdapter.Listener {

    private MyProductsAdapter mAdapter;
    private SwipeRefreshLayout srl;
    private View llEmpty;
    private int mFilterStatus = -1; // -1 全部
    private TextView tvAll;
    private TextView tvOn;
    private TextView tvOff;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_products;
    }

    @Override
    protected MyProductsPresenter createPresenter() {
        return new MyProductsPresenter();
    }

    @Override
    protected void initViews() {
        tvAll = findViewById(R.id.tv_filter_all);
        tvOn = findViewById(R.id.tv_filter_on);
        tvOff = findViewById(R.id.tv_filter_off);
        srl = findViewById(R.id.srl_list);
        llEmpty = findViewById(R.id.ll_empty);
        RecyclerView rv = findViewById(R.id.rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyProductsAdapter();
        mAdapter.setListener(this);
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        srl.setOnRefreshListener(() -> mPresenter.load(mFilterStatus));
        tvAll.setOnClickListener(v -> selectFilter(-1, tvAll));
        tvOn.setOnClickListener(v -> selectFilter(Product.STATUS_ON, tvOn));
        tvOff.setOnClickListener(v -> selectFilter(Product.STATUS_OFF, tvOff));
    }

    private void selectFilter(int status, TextView selected) {
        mFilterStatus = status;
        setFilterStyle(tvAll, status == -1);
        setFilterStyle(tvOn, status == Product.STATUS_ON);
        setFilterStyle(tvOff, status == Product.STATUS_OFF);
        mPresenter.load(status);
    }

    private void setFilterStyle(TextView tv, boolean selected) {
        tv.setTextColor(getResources().getColor(selected ? R.color.primary : R.color.text_second));
        tv.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    @Override
    protected void initData() {
        mPresenter.load(mFilterStatus);
    }

    @Override
    public void onProducts(List<Product> products) {
        srl.setRefreshing(false);
        mAdapter.setData(products);
        llEmpty.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProductChanged() {
        mPresenter.load(mFilterStatus);
    }

    @Override
    public void onToggleStatus(Product product) {
        int target = product.getStatus() == Product.STATUS_ON
                ? Product.STATUS_OFF : Product.STATUS_ON;
        mPresenter.changeStatus(product, target);
    }

    @Override
    public void onDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("删除商品")
                .setMessage("删除后不可恢复，确定删除“" + product.getTitle() + "”吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (d, w) -> mPresenter.delete(product))
                .show();
    }

    @Override
    public void onItemClick(Product product) {
        startActivity(ProductDetailActivity.newIntent(this, product.getId()));
    }
}
