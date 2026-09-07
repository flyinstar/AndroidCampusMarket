package com.campus.trade.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.campus.trade.R;
import com.campus.trade.network.ApiClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 服务器地址设置对话框（入口在登录页）。
 * 未登录时若连不上内置默认服务器，可先在此切换后端地址再登录。
 */
public final class ServerConfigDialog {

    public interface OnServerChangedListener {
        /** 服务器地址切换成功后被回调，参数为新地址 */
        void onServerChanged(String newUrl);
    }

    private ServerConfigDialog() {
    }

    public static void show(Context context, OnServerChangedListener listener) {
        if (context == null) {
            return;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server, null);
        TextInputEditText etServer = view.findViewById(R.id.et_server_url);
        etServer.setText(ApiClient.getBaseUrl());
        etServer.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etServer.selectAll();
            }
        });

        new MaterialAlertDialogBuilder(context)
                .setTitle("服务器设置")
                .setView(view)
                .setNeutralButton("恢复默认", (d, w) -> apply(context, ApiClient.DEFAULT_BASE_URL, listener))
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (d, w) -> apply(context, etServer.getText() == null
                        ? "" : etServer.getText().toString(), listener))
                .show();
    }

    private static void apply(Context context, String raw, OnServerChangedListener listener) {
        String normalized = ApiClient.normalize(raw);
        if (normalized == null) {
            Toast.makeText(context, "地址无效，请输入 http(s)://主机[:端口]/", Toast.LENGTH_SHORT).show();
            return;
        }
        if (normalized.equalsIgnoreCase(ApiClient.getBaseUrl())) {
            Toast.makeText(context, "当前已是该服务器", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiClient.switchServer(normalized);
        Toast.makeText(context, "已切换到 " + normalized, Toast.LENGTH_LONG).show();
        if (listener != null) {
            listener.onServerChanged(normalized);
        }
    }
}
