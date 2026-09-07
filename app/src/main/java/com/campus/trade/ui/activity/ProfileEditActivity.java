package com.campus.trade.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.base.BasePresenter;
import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.User;
import com.campus.trade.model.repository.UserRepository;
import com.campus.trade.network.callback.ApiCallback;

/**
 * 修改资料页
 */
public class ProfileEditActivity extends BaseActivity<BaseView, BasePresenter<BaseView>> {

    private EditText etNickname;
    private EditText etPhone;
    private EditText etGrade;
    private EditText etMajor;
    private Button btnSave;
    private final UserRepository mRepository = new UserRepository();
    private boolean mLoading;

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
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> save());
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
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
    }

    private void save() {
        if (mLoading) {
            return;
        }
        String nickname = etNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            showToast("昵称不能为空");
            return;
        }
        mLoading = true;
        btnSave.setEnabled(false);
        mRepository.updateProfile(nickname, null,
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
