package com.campus.trade.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.base.BaseFragment;
import com.campus.trade.model.entity.Category;
import com.campus.trade.presenter.product.CategoryContract;
import com.campus.trade.presenter.product.CategoryPresenter;
import com.campus.trade.ui.activity.ProductListActivity;
import com.campus.trade.ui.adapter.CategoryAdapter;

import java.util.List;

/**
 * 分类页
 */
public class CategoryFragment extends BaseFragment<CategoryContract.View, CategoryPresenter>
        implements CategoryContract.View, CategoryAdapter.OnCategoryClickListener {

    private CategoryAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    protected CategoryPresenter createPresenter() {
        return new CategoryPresenter();
    }

    @Override
    protected void initViews(View view) {
        RecyclerView rv = view.findViewById(R.id.rv_categories);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CategoryAdapter();
        mAdapter.setOnCategoryClickListener(this);
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mPresenter.loadCategories();
    }

    @Override
    public void onCategories(List<Category> categories) {
        if (isAdded()) {
            mAdapter.setData(categories);
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        Context ctx = getContext();
        if (ctx == null) {
            return;
        }
        startActivity(ProductListActivity.newIntent(ctx, category.getId(), category.getName(), null));
    }
}
