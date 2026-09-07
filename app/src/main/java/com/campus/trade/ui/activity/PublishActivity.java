package com.campus.trade.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.Category;
import com.campus.trade.model.entity.Product;
import com.campus.trade.presenter.product.PublishContract;
import com.campus.trade.presenter.product.PublishPresenter;
import com.campus.trade.ui.adapter.PublishImageAdapter;
import com.campus.trade.utils.ImageCompressUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品页：选图(最多6张) → 压缩 → 逐张上传 → 提交
 */
public class PublishActivity extends BaseActivity<PublishContract.View, PublishPresenter>
        implements PublishContract.View, PublishImageAdapter.Listener {

    private static final int REQ_PICK_IMAGE = 100;

    private static final String[] CONDITIONS = {"全新", "几乎全新", "有使用痕迹", "老旧"};

    private EditText etTitle;
    private EditText etPrice;
    private EditText etDescription;
    private TextView tvCategory;
    private TextView tvCondition;
    private PublishImageAdapter mImageAdapter;
    private List<Category> mCategories = new ArrayList<>();
    private int mCategoryId = -1;
    private int mCondition = 1;
    private boolean mPublishing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected PublishPresenter createPresenter() {
        return new PublishPresenter();
    }

    @Override
    protected void initViews() {
        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        tvCategory = findViewById(R.id.tv_category);
        tvCondition = findViewById(R.id.tv_condition);

        RecyclerView rvImages = findViewById(R.id.rv_images);
        mImageAdapter = new PublishImageAdapter();
        mImageAdapter.setListener(this);
        GridLayoutManager grid = new GridLayoutManager(this, 3);
        rvImages.setLayoutManager(grid);
        rvImages.setAdapter(mImageAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.tv_publish).setOnClickListener(v -> startPublish());
        tvCategory.setOnClickListener(v -> pickCategory());
        tvCondition.setOnClickListener(v -> pickCondition());
    }

    @Override
    protected void initData() {
        mPresenter.loadCategories();
        tvCondition.setText("成色：" + CONDITIONS[0] + " ▾");
    }

    private void pickCategory() {
        if (mCategories.isEmpty()) {
            showToast("分类加载中，请稍候重试");
            return;
        }
        String[] names = new String[mCategories.size()];
        for (int i = 0; i < mCategories.size(); i++) {
            names[i] = mCategories.get(i).getName();
        }
        new AlertDialog.Builder(this)
                .setTitle("选择分类")
                .setItems(names, (d, which) -> {
                    mCategoryId = mCategories.get(which).getId();
                    tvCategory.setText(mCategories.get(which).getName() + " ▾");
                })
                .show();
    }

    private void pickCondition() {
        new AlertDialog.Builder(this)
                .setTitle("选择成色")
                .setItems(CONDITIONS, (d, which) -> {
                    mCondition = which + 1;
                    tvCondition.setText("成色：" + CONDITIONS[which] + " ▾");
                })
                .show();
    }

    @Override
    public void onAddImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void onRemoveImage(int index) {
        mImageAdapter.removeAt(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PICK_IMAGE || resultCode != RESULT_OK || data == null) {
            return;
        }
        List<Uri> uris = new ArrayList<>();
        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                uris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris.add(data.getData());
        }
        int remain = PublishImageAdapter.MAX_COUNT - mImageAdapter.getPaths().size();
        for (int i = 0; i < uris.size() && i < remain; i++) {
            String path = copyToCacheAndCompress(uris.get(i));
            if (path != null) {
                mImageAdapter.addPath(path);
            }
        }
    }

    /** 将 content uri 拷贝到缓存并压缩，返回本地路径 */
    private String copyToCacheAndCompress(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            if (in == null) {
                return null;
            }
            String name = queryName(uri);
            if (TextUtils.isEmpty(name)) {
                name = "img_" + System.currentTimeMillis() + ".jpg";
            } else if (!name.contains(".")) {
                name = name + ".jpg";
            }
            File cacheDir = new File(getCacheDir(), "publish");
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                return null;
            }
            File raw = new File(cacheDir, "raw_" + System.currentTimeMillis() + "_" + name);
            try (FileOutputStream out = new FileOutputStream(raw)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                in.close();
            }
            File compressed = ImageCompressUtil.compressToFile(raw.getAbsolutePath(), 1024 * 1024);
            return compressed.getAbsolutePath();
        } catch (Exception e) {
            showToast("读取图片失败: " + e.getMessage());
            return null;
        }
    }

    private String queryName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) {
                    return cursor.getString(idx);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void startPublish() {
        if (mPublishing) {
            return;
        }
        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            showToast("请填写商品标题");
            return;
        }
        String priceText = etPrice.getText().toString().trim();
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showToast("请输入正确的价格");
            return;
        }
        if (price <= 0) {
            showToast("价格必须大于0");
            return;
        }
        if (mCategoryId < 0) {
            showToast("请选择分类");
            return;
        }
        mPublishing = true;
        findViewById(R.id.tv_publish).setEnabled(false);
        mPresenter.publish(mImageAdapter.getPaths(), title,
                etDescription.getText().toString().trim(), price, mCategoryId, mCondition);
    }

    @Override
    public void onCategories(List<Category> categories) {
        if (categories != null) {
            mCategories.clear();
            mCategories.addAll(categories);
        }
    }

    @Override
    public void onUploadProgress(int current, int total) {
        showToast("图片上传中 " + current + "/" + total);
    }

    @Override
    public void onPublishSuccess(Product product) {
        mPublishing = false;
        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showError(String msg) {
        mPublishing = false;
        findViewById(R.id.tv_publish).setEnabled(true);
        super.showError(msg);
    }
}
