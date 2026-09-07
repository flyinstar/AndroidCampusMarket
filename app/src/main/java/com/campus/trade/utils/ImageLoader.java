package com.campus.trade.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.campus.trade.R;

/**
 * Glide 图片加载封装
 */
public final class ImageLoader {

    private ImageLoader() {
    }

    /** 网络/本地 URL 通用加载 */
    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.bg_img_placeholder)
                .error(R.drawable.bg_img_placeholder)
                .into(imageView);
    }

    /** 圆角图片（商品封面等） */
    public static void loadRound(Context context, String url, ImageView imageView, int radiusDp) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.bg_img_placeholder)
                .error(R.drawable.bg_img_placeholder)
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(dp(context, radiusDp))))
                .into(imageView);
    }

    /** 圆形头像 */
    public static void loadAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .apply(new RequestOptions().circleCrop())
                .into(imageView);
    }

    private static int dp(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
