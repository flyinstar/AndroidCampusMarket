package com.campus.trade.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.presenter.auth.LoginContract;
import com.campus.trade.presenter.auth.LoginPresenter;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity<LoginContract.View, LoginPresenter> implements LoginContract.View {

    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void initData() {
        etEmail.setText(mPresenter.getSavedEmail());
        etPassword.setText(mPresenter.getSavedPassword());
        cbRemember.setChecked(mPresenter.getRememberStatus());
    }

    @Override
    protected void initListeners() {
        btnLogin.setOnClickListener(v -> doLogin());
        findViewById(R.id.tv_go_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (cbRemember.isChecked()) {
            mPresenter.saveLoginInfo(email, password, true);
        } else {
            mPresenter.saveLoginInfo("", "", false);
        }
        mPresenter.login(email, password);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
    }

    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void setEmailError(String error) {
        etEmail.setError(error);
    }

    @Override
    public void setPasswordError(String error) {
        etPassword.setError(error);
    }
}
