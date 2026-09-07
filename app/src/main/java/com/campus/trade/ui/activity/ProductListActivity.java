package com.campus.trade.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;
import com.campus.trade.presenter.product.HomeContract;
import com.campus.trade.presenter.product.HomePresenter;
import com.campus.trade.ui.adapter.ProductAdapter;

import java.util.List;

/**
 * 商品列表页（分类浏览 / 关键词搜索共用）
 */
public class ProductListActivity extends BaseActivity<HomeContract.View, HomePresenter> implements HomeContract.View {

    private static final String EXTRA_CATEGORY_ID = "category_id";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_KEYWORD = "keyword";

    private TextView tvSortHot;
    private TextView tvSortNew;
    private SwipeRefreshLayout srlList;
    private RecyclerView rvProducts;
    private LinearLayout llEmpty;
    private ProductAdapter mAdapter;
    private Integer mCategoryId;
    private String mKeyword;

    public static Intent newIntent(Context context, Integer categoryId, String title, String keyword) {
        Intent intent = new Intent(context, ProductListActivity.class);
        if (categoryId != null) {
            intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        }
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_KEYWORD, keyword);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_list;
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void initViews() {
        tvSortHot = findViewById(R.id.tv_sort_hot);
        tvSortNew = findViewById(R.id.tv_sort_new);
        srlList = findViewById(R.id.srl_list);
        rvProducts = findViewById(R.id.rv_products);
        llEmpty = findViewById(R.id.ll_empty);

        mAdapter = new ProductAdapter();
        GridLayoutManager grid = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(grid);
        rvProducts.setAdapter(mAdapter);
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (grid.findLastVisibleItemPosition() >= mAdapter.getItemCount() - 3) {
                    mPresenter.loadMore();
                }
            }
        });
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        srlList.setOnRefreshListener(() -> mPresenter.refresh());
        tvSortHot.setOnClickListener(v -> {
            setSortUi("hot");
            mPresenter.setSort("hot");
        });
        tvSortNew.setOnClickListener(v -> {
            setSortUi("new");
            mPresenter.setSort("new");
        });
        mAdapter.setOnItemClickListener(product ->
                startActivity(ProductDetailActivity.newIntent(this, product.getId())));
    }

    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        String title = null;
        if (extras != null) {
            mCategoryId = extras.containsKey(EXTRA_CATEGORY_ID)
                    ? extras.getInt(EXTRA_CATEGORY_ID) : null;
            title = extras.getString(EXTRA_TITLE);
            mKeyword = extras.getString(EXTRA_KEYWORD);
        }
        TextView tvTitle = findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        } else if (!TextUtils.isEmpty(mKeyword)) {
            tvTitle.setText("搜索: " + mKeyword);
        } else {
            tvTitle.setText("全部商品");
        }
        mPresenter.setKeyword(mKeyword);
        if (mCategoryId != null) {
            mPresenter.setCategory(mCategoryId);
        } else {
            mPresenter.setSort("hot");
        }
    }

    private void setSortUi(String sort) {
        boolean hot = "hot".equals(sort);
        tvSortHot.setTextColor(androidx.core.content.ContextCompat.getColor(this, hot ? R.color.primary : R.color.text_second));
        tvSortNew.setTextColor(androidx.core.content.ContextCompat.getColor(this, hot ? R.color.text_second : R.color.primary));
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        // 列表页无需分类入口
    }

    @Override
    public void onProducts(List<Product> products, boolean refresh, boolean hasMore) {
        srlList.setRefreshing(false);
        if (refresh) {
            mAdapter.replace(products);
        } else {
            mAdapter.append(products);
        }
        boolean empty = mAdapter.isEmpty();
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
