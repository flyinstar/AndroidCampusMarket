package com.campus.trade.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.Comment;
import com.campus.trade.model.entity.Product;
import com.campus.trade.network.ApiClient;
import com.campus.trade.presenter.product.ProductDetailContract;
import com.campus.trade.presenter.product.ProductDetailPresenter;
import com.campus.trade.ui.adapter.CommentAdapter;
import com.campus.trade.utils.DateTimeUtil;
import com.campus.trade.utils.ImageLoader;
import com.campus.trade.utils.SharedPrefUtils;

import java.util.List;

/**
 * 商品详情页
 */
public class ProductDetailActivity extends BaseActivity<ProductDetailContract.View, ProductDetailPresenter>
        implements ProductDetailContract.View {

    private static final String EXTRA_PRODUCT_ID = "product_id";

    private TextView tvPrice;
    private TextView tvCondition;
    private TextView tvTitle;
    private TextView tvMeta;
    private TextView tvDescription;
    private TextView tvSellerName;
    private TextView tvSellerMeta;
    private TextView tvChatSeller;
    private ImageView ivAvatar;
    private HorizontalScrollView hsvImages;
    private LinearLayout llImages;
    private TextView tvImgEmpty;
    private RecyclerView rvComments;
    private EditText etComment;
    private ImageButton btnSendComment;
    private ImageButton btnFavoriteTop;
    private ImageButton btnFavoriteBottom;
    private Button btnActionPrimary;
    private Button btnActionSecondary;

    private CommentAdapter mCommentAdapter;
    private Product mProduct;
    private boolean mFavorited;

    public static Intent newIntent(Context context, int productId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected ProductDetailPresenter createPresenter() {
        return new ProductDetailPresenter();
    }

    @Override
    protected void initViews() {
        tvPrice = findViewById(R.id.tv_price);
        tvCondition = findViewById(R.id.tv_condition);
        tvTitle = findViewById(R.id.tv_title);
        tvMeta = findViewById(R.id.tv_meta);
        tvDescription = findViewById(R.id.tv_description);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerMeta = findViewById(R.id.tv_seller_meta);
        tvChatSeller = findViewById(R.id.tv_chat_seller);
        ivAvatar = findViewById(R.id.iv_avatar);
        hsvImages = findViewById(R.id.hsv_images);
        llImages = findViewById(R.id.ll_images);
        tvImgEmpty = findViewById(R.id.tv_img_empty);
        rvComments = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);
        btnFavoriteTop = findViewById(R.id.btn_favorite_top);
        btnFavoriteBottom = findViewById(R.id.btn_favorite_bottom);
        btnActionPrimary = findViewById(R.id.btn_action_primary);
        btnActionSecondary = findViewById(R.id.btn_action_secondary);

        mCommentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(mCommentAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        View.OnClickListener fav = v -> mPresenter.toggleFavorite(mProduct == null ? 0 : mProduct.getId());
        btnFavoriteTop.setOnClickListener(fav);
        btnFavoriteBottom.setOnClickListener(fav);

        btnSendComment.setOnClickListener(v -> submitComment());

        View.OnClickListener chat = v -> {
            if (mProduct != null) {
                startActivity(ChatActivity.newIntent(this,
                        mProduct.getUserId(), mProduct.getSellerName()));
            }
        };
        tvChatSeller.setOnClickListener(chat);
        btnActionSecondary.setOnClickListener(chat);

        btnActionPrimary.setOnClickListener(v -> onPrimaryAction());
    }

    private void onPrimaryAction() {
        if (mProduct == null) {
            return;
        }
        int currentUserId = SharedPrefUtils.getUserId(this);
        if (mProduct.getUserId() == currentUserId) {
            // 卖家：上架/下架
            int target = mProduct.getStatus() == Product.STATUS_OFF
                    ? Product.STATUS_ON : Product.STATUS_OFF;
            mPresenterUpdateStatus(target);
        } else if (mProduct.getStatus() == Product.STATUS_ON) {
            // 买家：下单
            showOrderDialog();
        }
    }

    private void mPresenterUpdateStatus(int status) {
        // 状态操作复用 Presenter 直连仓库（避免契约膨胀）
        new com.campus.trade.model.repository.ProductRepository().updateStatus(
                mProduct.getId(), status, new com.campus.trade.network.callback.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        showToast(data);
                        mProduct.setStatus(status);
                        bindActionBar(mProduct);
                        bindMeta(mProduct);
                    }

                    @Override
                    public void onFailure(String msg) {
                        showToast(msg);
                    }
                });
    }

    private void showOrderDialog() {
        final EditText remarkEt = new EditText(this);
        remarkEt.setHint("备注（选填），如交易地点建议");
        remarkEt.setPadding(24, 16, 24, 16);
        new AlertDialog.Builder(this)
                .setTitle("确认下单")
                .setMessage("下单后该商品将被标记为“已预约”，请与卖家联系约定线下交易。")
                .setView(remarkEt)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认下单", (d, w) ->
                        mPresenter.createOrder(mProduct.getId(),
                                remarkEt.getText().toString().trim()))
                .show();
    }

    private void submitComment() {
        String content = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("请输入评论内容");
            return;
        }
        mPresenter.addComment(mProduct.getId(), content);
    }

    @Override
    protected void initData() {
        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, 0);
        mPresenter.loadDetail(productId);
        mPresenter.loadComments(productId);
    }

    @Override
    public void onDetailLoaded(Product product) {
        mProduct = product;
        mFavorited = product.isFavorite();
        tvPrice.setText("¥" + product.priceText());
        tvTitle.setText(product.getTitle());
        tvDescription.setText(TextUtils.isEmpty(product.getDescription())
                ? "卖家没有填写描述～" : product.getDescription());
        tvSellerName.setText(product.getSellerName());
        tvSellerMeta.setText("信用 " + product.getSellerCredit());
        ImageLoader.loadAvatar(this, ApiClient.toAccessibleImageUrl(product.getSellerAvatar()), ivAvatar);

        bindImages(product.getImageUrls());
        bindMeta(product);
        bindActionBar(product);
        bindFavoriteUi();
    }

    private void bindImages(List<String> urls) {
        llImages.removeAllViews();
        if (urls == null || urls.isEmpty()) {
            tvImgEmpty.setVisibility(View.VISIBLE);
            hsvImages.setVisibility(View.GONE);
            return;
        }
        tvImgEmpty.setVisibility(View.GONE);
        hsvImages.setVisibility(View.VISIBLE);
        for (String url : urls) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) getResources().getDisplayMetrics().density * 280,
                    (int) getResources().getDisplayMetrics().density * 280));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(R.drawable.bg_img_placeholder);
            llImages.addView(imageView);
            ImageLoader.load(this, ApiClient.toAccessibleImageUrl(url), imageView);
        }
    }

    private void bindMeta(Product p) {
        tvCondition.setText("成色:" + Product.conditionText(p.getCondition()));
        tvMeta.setText(p.getCategoryName() + " · " + DateTimeUtil.friendlyTime(p.getCreatedAt())
                + " · " + p.getViewCount() + " 次浏览 · " + p.getFavoriteCount() + " 人收藏");
    }

    private void bindActionBar(Product p) {
        int currentUserId = SharedPrefUtils.getUserId(this);
        boolean mine = p.getUserId() == currentUserId;
        tvChatSeller.setVisibility(mine ? View.INVISIBLE : View.VISIBLE);
        btnFavoriteTop.setVisibility(mine ? View.INVISIBLE : View.VISIBLE);
        btnFavoriteBottom.setVisibility(mine ? View.INVISIBLE : View.VISIBLE);

        if (mine) {
            btnActionPrimary.setVisibility(View.VISIBLE);
            btnActionSecondary.setVisibility(View.GONE);
            if (p.getStatus() == Product.STATUS_ON) {
                btnActionPrimary.setText("下架商品");
            } else if (p.getStatus() == Product.STATUS_OFF) {
                btnActionPrimary.setText("重新上架");
            } else {
                btnActionPrimary.setText(Product.statusText(p.getStatus()));
                btnActionPrimary.setEnabled(false);
            }
            return;
        }

        btnActionPrimary.setVisibility(View.VISIBLE);
        btnActionSecondary.setVisibility(View.VISIBLE);
        btnActionSecondary.setText("联系卖家");
        switch (p.getStatus()) {
            case Product.STATUS_ON:
                btnActionPrimary.setText("立即下单");
                btnActionPrimary.setEnabled(true);
                break;
            case Product.STATUS_RESERVED:
                btnActionPrimary.setText("已被预约");
                btnActionPrimary.setEnabled(false);
                break;
            case Product.STATUS_SOLD:
                btnActionPrimary.setText("已售出");
                btnActionPrimary.setEnabled(false);
                break;
            default:
                btnActionPrimary.setText("已下架");
                btnActionPrimary.setEnabled(false);
        }
    }

    private void bindFavoriteUi() {
        int res = mFavorited ? R.drawable.ic_heart_filled : R.drawable.ic_heart;
        btnFavoriteTop.setImageResource(res);
        btnFavoriteBottom.setImageResource(res);
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {
        if (comments != null) {
            mCommentAdapter.replace(comments);
        }
    }

    @Override
    public void onCommentAdded(Comment comment) {
        etComment.setText("");
        mCommentAdapter.addFirst(comment);
    }

    @Override
    public void onFavoriteChanged(boolean favorited) {
        mFavorited = favorited;
        bindFavoriteUi();
        if (mProduct != null) {
            mProduct.setFavorite(mFavorited);
            if (mFavorited) {
                mProduct.setFavoriteCount(mProduct.getFavoriteCount() + 1);
            } else {
                mProduct.setFavoriteCount(Math.max(0, mProduct.getFavoriteCount() - 1));
            }
        }
        showToast(favorited ? "已收藏" : "已取消收藏");
    }

    @Override
    public void onOrderCreated() {
        new AlertDialog.Builder(this)
                .setTitle("下单成功")
                .setMessage("已向卖家发出交易申请，可在“我的订单”中查看状态。")
                .setPositiveButton("查看订单", (d, w) ->
                        startActivity(new Intent(this, OrdersActivity.class)))
                .setNegativeButton("继续逛逛", null)
                .show();
    }
}
