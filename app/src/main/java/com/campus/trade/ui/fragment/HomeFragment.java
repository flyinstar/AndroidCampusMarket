package com.campus.trade.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseFragment;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;
import com.campus.trade.presenter.product.HomeContract;
import com.campus.trade.presenter.product.HomePresenter;
import com.campus.trade.ui.activity.ProductDetailActivity;
import com.campus.trade.ui.adapter.CategoryChipAdapter;
import com.campus.trade.ui.adapter.ProductAdapter;

import java.util.List;

/**
 * 首页：搜索 / 分类快捷 / 热门与最新 / 商品双列瀑布流分页
 */
public class HomeFragment extends BaseFragment<HomeContract.View, HomePresenter>
        implements HomeContract.View, CategoryChipAdapter.OnCategoryClickListener {

    private EditText etSearch;
    private TextView tvSortHot;
    private TextView tvSortNew;
    private SwipeRefreshLayout srlHome;
    private RecyclerView rvProducts;
    private LinearLayout llEmpty;
    private CategoryChipAdapter mChipAdapter;
    private ProductAdapter mProductAdapter;
    private boolean mRefreshing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        tvSortHot = view.findViewById(R.id.tv_sort_hot);
        tvSortNew = view.findViewById(R.id.tv_sort_new);
        srlHome = view.findViewById(R.id.srl_home);
        rvProducts = view.findViewById(R.id.rv_products);
        llEmpty = view.findViewById(R.id.ll_empty);

        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        mChipAdapter = new CategoryChipAdapter();
        mChipAdapter.setOnCategoryClickListener(this);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(mChipAdapter);

        mProductAdapter = new ProductAdapter();
        GridLayoutManager grid = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(grid);
        rvProducts.setAdapter(mProductAdapter);
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int last = grid.findLastVisibleItemPosition();
                if (last >= mProductAdapter.getItemCount() - 3) {
                    mPresenter.loadMore();
                }
            }
        });
    }

    @Override
    protected void initListeners() {
        srlHome.setOnRefreshListener(() -> {
            mRefreshing = true;
            mPresenter.refresh();
        });
        tvSortHot.setOnClickListener(v -> {
            setSortUi("hot");
            mPresenter.setSort("hot");
        });
        tvSortNew.setOnClickListener(v -> {
            setSortUi("new");
            mPresenter.setSort("new");
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String keyword = etSearch.getText().toString().trim();
                mPresenter.setKeyword(TextUtils.isEmpty(keyword) ? null : keyword);
                return true;
            }
            return false;
        });
        mProductAdapter.setOnItemClickListener(product ->
                startActivity(ProductDetailActivity.newIntent(getContext(), product.getId())));
    }

    @Override
    protected void initData() {
        mPresenter.loadCategories();
        mPresenter.refresh();
    }

    @Override
    public void onCategoryClick(Integer categoryId, String name) {
        mPresenter.setCategory(categoryId);
    }

    private void setSortUi(String sort) {
        boolean hot = "hot".equals(sort);
        tvSortHot.setTextColor(getResources().getColor(hot ? R.color.primary : R.color.text_second));
        tvSortNew.setTextColor(getResources().getColor(hot ? R.color.text_second : R.color.primary));
        tvSortHot.setTypeface(null, hot ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tvSortNew.setTypeface(null, hot ? android.graphics.Typeface.NORMAL : android.graphics.Typeface.BOLD);
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        if (isAdded()) {
            mChipAdapter.setCategories(categories);
        }
    }

    @Override
    public void onProducts(List<Product> products, boolean refresh, boolean hasMore) {
        if (!isAdded()) {
            return;
        }
        if (mRefreshing) {
            mRefreshing = false;
            srlHome.setRefreshing(false);
        }
        if (refresh) {
            mProductAdapter.replace(products);
        } else {
            mProductAdapter.append(products);
        }
        boolean empty = mProductAdapter.isEmpty();
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从详情页返回时刷新（浏览量/收藏可能变化）
        if (mProductAdapter != null && !mProductAdapter.isEmpty() && mPresenter != null) {
            mPresenter.refresh();
        }
    }
}
