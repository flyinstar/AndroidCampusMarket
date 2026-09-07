package com.campus.trade.presenter.product;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;
import com.campus.trade.model.entity.ProductPage;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 首页 Presenter：分类 + 商品列表（分页/筛选/排序）
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {

    private static final int PAGE_SIZE = 20;

    private final ProductRepository mRepository;

    private Integer mCategoryId;
    private String mKeyword;
    private String mSort = "hot";
    private int mCurrentPage = 0;
    private boolean mHasMore = true;
    private boolean mLoading = false;

    public HomePresenter() {
        this.mRepository = new ProductRepository();
    }

    @Override
    public void loadCategories() {
        mRepository.categories(new ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                HomeContract.View v = getView();
                if (v != null) {
                    v.onCategoriesLoaded(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                // 分类加载失败不阻塞列表
            }
        });
    }

    @Override
    public void setCategory(Integer categoryId) {
        // 首页快捷栏用 id=0 表示“全部”，0/null 都视为不过滤
        mCategoryId = (categoryId == null || categoryId == 0) ? null : categoryId;
        refresh();
    }

    @Override
    public void setKeyword(String keyword) {
        mKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        refresh();
    }

    @Override
    public void setSort(String sort) {
        if ("new".equals(sort)) {
            mSort = "new";
        } else {
            mSort = "hot";
        }
        refresh();
    }

    @Override
    public void refresh() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        mCurrentPage = 0;
        loadPage(1, true);
    }

    @Override
    public void loadMore() {
        if (mLoading || !mHasMore) {
            return;
        }
        mLoading = true;
        loadPage(mCurrentPage + 1, false);
    }

    private void loadPage(int page, boolean refresh) {
        mRepository.products(mCategoryId, mKeyword, page, PAGE_SIZE, mSort,
                new ApiCallback<ProductPage>() {
                    @Override
                    public void onSuccess(ProductPage data) {
                        mLoading = false;
                        if (data == null || data.getList() == null) {
                            mHasMore = false;
                            return;
                        }
                        mCurrentPage = data.getPage();
                        long total = data.getTotal();
                        mHasMore = (long) mCurrentPage * data.getSize() < total;
                        List<Product> list = data.getList();
                        HomeContract.View v = getView();
                        if (v != null) {
                            v.onProducts(list, refresh, mHasMore);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mLoading = false;
                        HomeContract.View v = getView();
                        if (v != null) {
                            v.showError(msg);
                        }
                    }
                });
    }
}
