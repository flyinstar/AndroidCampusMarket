package com.campus.trade.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.campus.trade.R;
import com.campus.trade.base.BaseFragment;
import com.campus.trade.model.entity.User;
import com.campus.trade.network.ApiClient;
import com.campus.trade.presenter.user.ProfileContract;
import com.campus.trade.presenter.user.ProfilePresenter;
import com.campus.trade.ui.activity.FavoritesActivity;
import com.campus.trade.ui.activity.LoginActivity;
import com.campus.trade.ui.activity.MyProductsActivity;
import com.campus.trade.ui.activity.OrdersActivity;
import com.campus.trade.ui.activity.ProfileEditActivity;
import com.campus.trade.utils.ImageLoader;
import com.campus.trade.utils.SharedPrefUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 我的页
 */
public class ProfileFragment extends BaseFragment<ProfileContract.View, ProfilePresenter> implements ProfileContract.View {

    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvEmail;
    private TextView tvProfileMeta;
    private boolean mProfileLoaded = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected ProfilePresenter createPresenter() {
        return new ProfilePresenter();
    }

    @Override
    protected void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvEmail = view.findViewById(R.id.tv_email);
        tvProfileMeta = view.findViewById(R.id.tv_profile_meta);
    }

    @Override
    protected void initListeners() {
        requireView().findViewById(R.id.row_my_products).setOnClickListener(v ->
                startActivity(new Intent(getContext(), MyProductsActivity.class)));
        requireView().findViewById(R.id.row_favorites).setOnClickListener(v ->
                startActivity(new Intent(getContext(), FavoritesActivity.class)));
        requireView().findViewById(R.id.row_orders).setOnClickListener(v ->
                startActivity(new Intent(getContext(), OrdersActivity.class)));
        requireView().findViewById(R.id.row_edit_profile).setOnClickListener(v ->
                startActivity(new Intent(getContext(), ProfileEditActivity.class)));
        requireView().findViewById(R.id.row_change_password).setOnClickListener(v -> showChangePasswordDialog());
        requireView().findViewById(R.id.btn_logout).setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void initData() {
        mPresenter.loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProfileLoaded && mPresenter != null) {
            mPresenter.loadProfile();
        }
    }

    @Override
    public void onProfileLoaded(User user) {
        mProfileLoaded = true;
        if (getContext() == null) {
            return;
        }
        ImageLoader.loadAvatar(getContext(), ApiClient.toAccessibleImageUrl(user.getAvatar()), ivAvatar);
        tvNickname.setText(user.getNickname());
        tvEmail.setText(user.getEmail());
        String meta = (user.getGrade() == null ? "" : user.getGrade())
                + (user.getMajor() == null ? "" : " · " + user.getMajor());
        tvProfileMeta.setText(meta.trim().isEmpty() ? "完善资料，让交易更顺畅～" : meta);
    }

    private void showChangePasswordDialog() {
        Context ctx = getContext();
        if (ctx == null) {
            return;
        }
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etOld = view.findViewById(R.id.et_old_password);
        TextInputEditText etNew = view.findViewById(R.id.et_new_password);

        new MaterialAlertDialogBuilder(ctx)
                .setTitle("修改密码")
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (d, w) ->
                        mPresenter.changePassword(etOld.getText() == null ? ""
                                        : etOld.getText().toString(),
                                etNew.getText() == null ? "" : etNew.getText().toString()))
                .show();
    }

    private void confirmLogout() {
        Context ctx = getContext();
        if (ctx == null) {
            return;
        }
        new AlertDialog.Builder(ctx)
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", (d, w) -> {
                    SharedPrefUtils.clear(ctx);
                    Intent intent = new Intent(ctx, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }
}
