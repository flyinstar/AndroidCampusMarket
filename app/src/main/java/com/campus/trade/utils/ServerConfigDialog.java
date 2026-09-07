package com.campus.trade.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.campus.trade.network.ApiClient;

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
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * context.getResources().getDisplayMetrics().density);
        layout.setPadding(pad, 12, pad, 0);

        final EditText etServer = new EditText(context);
        etServer.setHint("例如 http://192.168.1.100:8080/");
        etServer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        etServer.setText(ApiClient.getBaseUrl());
        etServer.setSelectAllOnFocus(true);
        layout.addView(etServer);

        new AlertDialog.Builder(context)
                .setTitle("服务器设置")
                .setMessage("填写后端服务地址（以 http:// 或 https:// 开头）。\n"
                        + "更换服务器后本地登录态将失效，需要重新登录该服务器的账号。")
                .setView(layout)
                .setNeutralButton("恢复默认", (d, w) -> apply(context, ApiClient.DEFAULT_BASE_URL, listener))
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (d, w) -> apply(context, etServer.getText().toString(), listener))
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
