package com.campus.trade.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.presenter.auth.RegisterContract;
import com.campus.trade.presenter.auth.RegisterPresenter;

/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity<RegisterContract.View, RegisterPresenter> implements RegisterContract.View {

    private EditText etNickname;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirm;
    private Button btnRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected void initViews() {
        etNickname = findViewById(R.id.et_nickname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirm = findViewById(R.id.et_confirm);
        btnRegister = findViewById(R.id.btn_register);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> mPresenter.register(
                etNickname.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString(),
                etConfirm.getText().toString()));
    }

    @Override
    public void showLoading() {
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中…");
    }

    @Override
    public void hideLoading() {
        btnRegister.setEnabled(true);
        btnRegister.setText("注 册");
    }

    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    @Override
    public void onRegisterSuccess() {
        showToast("注册成功，请登录");
        finish();
    }

    @Override
    public void setFieldError(String field, String error) {
        switch (field) {
            case "email":
                etEmail.setError(error);
                break;
            case "password":
                etPassword.setError(error);
                break;
            case "confirm":
                etConfirm.setError(error);
                break;
            default:
                showToast(error);
        }
    }
}
