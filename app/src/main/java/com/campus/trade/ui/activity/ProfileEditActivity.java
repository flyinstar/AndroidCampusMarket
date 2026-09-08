package com.campus.trade.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.base.BasePresenter;
import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.User;
import com.campus.trade.model.repository.ProductRepository;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.ApiClient;
import com.campus.trade.network.callback.ApiCallback;
import com.campus.trade.utils.ImageCompressUtil;
import com.campus.trade.utils.ImageLoader;

import java.io.File;

/**
 * 修改资料页：可更换头像（相册选图 → 压缩 → 上传），随资料一起保存
 */
public class ProfileEditActivity extends BaseActivity<BaseView, BasePresenter<BaseView>> {

    private static final int REQ_PICK_AVATAR = 200;

    private EditText etNickname;
    private EditText etPhone;
    private EditText etGrade;
    private EditText etMajor;
    private Button btnSave;
    private ImageView ivAvatar;
    private final UserRepository mRepository = new UserRepository();
    private final ProductRepository mUploadRepository = new ProductRepository();
    private boolean mLoading;
    /** 新头像上传成功后返回的 URL；null 表示本次不修改头像 */
    private String mAvatarUrl;
    private boolean mAvatarUploading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected BasePresenter<BaseView> createPresenter() {
        return null;
    }

    @Override
    protected void initViews() {
        etNickname = findViewById(R.id.et_nickname);
        etPhone = findViewById(R.id.et_phone);
        etGrade = findViewById(R.id.et_grade);
        etMajor = findViewById(R.id.et_major);
        btnSave = findViewById(R.id.btn_save);
        ivAvatar = findViewById(R.id.iv_avatar);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> save());
        findViewById(R.id.avatar_picker).setOnClickListener(v -> pickAvatar());
    }

    @Override
    protected void initData() {
        mRepository.getProfile(new ApiCallback<User>() {
            @Override
            public void onSuccess(User data) {
                etNickname.setText(data.getNickname());
                etPhone.setText(data.getPhone() == null ? "" : data.getPhone());
                etGrade.setText(data.getGrade() == null ? "" : data.getGrade());
                etMajor.setText(data.getMajor() == null ? "" : data.getMajor());
                // 仅当用户尚未选新头像时展示服务器头像（未更换则保存时不提交 avatar 字段）
                if (mAvatarUrl == null) {
                    ImageLoader.loadAvatar(ProfileEditActivity.this,
                            ApiClient.toAccessibleImageUrl(data.getAvatar()), ivAvatar);
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
    }

    /** 相册选一张图作为头像 */
    private void pickAvatar() {
        if (mAvatarUploading) {
            showToast("头像上传中，请稍候");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQ_PICK_AVATAR);
    }

    @SuppressWarnings("deprecation") // 图库单选走 onActivityResult，兼容 minSdk 23
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PICK_AVATAR || resultCode != RESULT_OK || data == null
                || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        // 头像按小图压缩（512KB 以内），本地立即预览
        File file = ImageCompressUtil.copyToCacheAndCompress(this, uri, "avatar", 512 * 1024);
        if (file == null) {
            showToast("读取图片失败，请换一张试试");
            return;
        }
        ImageLoader.loadAvatar(this, file.getAbsolutePath(), ivAvatar);
        uploadAvatar(file);
    }

    private void uploadAvatar(File file) {
        mAvatarUploading = true;
        showToast("头像上传中…");
        mUploadRepository.uploadImage(file, new ApiCallback<String>() {
            @Override
            public void onSuccess(String url) {
                mAvatarUploading = false;
                if (url == null || url.isEmpty()) {
                    showToast("头像上传失败，请重试");
                    return;
                }
                mAvatarUrl = url;
                // 预览切回网络图（与最终存库一致）
                ImageLoader.loadAvatar(ProfileEditActivity.this,
                        ApiClient.toAccessibleImageUrl(mAvatarUrl), ivAvatar);
                showToast("头像已上传，点击保存生效");
            }

            @Override
            public void onFailure(String msg) {
                mAvatarUploading = false;
                showToast(msg);
            }
        });
    }

    private void save() {
        if (mLoading) {
            return;
        }
        if (mAvatarUploading) {
            showToast("头像上传中，请稍候再保存");
            return;
        }
        String nickname = etNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            showToast("昵称不能为空");
            return;
        }
        mLoading = true;
        btnSave.setEnabled(false);
        mRepository.updateProfile(nickname, mAvatarUrl,
                etPhone.getText().toString().trim(),
                etGrade.getText().toString().trim(),
                etMajor.getText().toString().trim(),
                new ApiCallback<User>() {
                    @Override
                    public void onSuccess(User data) {
                        mLoading = false;
                        btnSave.setEnabled(true);
                        showToast("保存成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mLoading = false;
                        btnSave.setEnabled(true);
                        showToast(msg);
                    }
                });
    }
}
