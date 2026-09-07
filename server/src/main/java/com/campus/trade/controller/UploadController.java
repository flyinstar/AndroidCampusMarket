package com.campus.trade.controller;

import com.campus.trade.common.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 图片上传接口（本地磁盘存储）
 */
@RestController
@RequestMapping("/v1/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private static final String[] ALLOWED_SUFFIX = {".jpg", ".jpeg", ".png", ".webp"};

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.access-url}")
    private String accessUrl;

    /** 上传目录的绝对路径（基于“服务启动目录”惰性解析，避免相对路径歧义） */
    private File uploadRoot;

    private synchronized File getUploadRoot() {
        if (uploadRoot == null) {
            uploadRoot = new File(uploadDir).getAbsoluteFile();
            if (!uploadRoot.exists()) {
                // 创建失败不阻断，写文件前还会再尝试并给出明确提示
                uploadRoot.mkdirs();
            }
            log.info("图片上传目录(绝对): {}", uploadRoot);
        }
        return uploadRoot;
    }

    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return BaseResponse.error("请选择图片文件");
        }
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null || !hasAllowedSuffix(originalName.toLowerCase())) {
                return BaseResponse.error("仅支持jpg、jpeg、png、webp格式");
            }
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            File root = getUploadRoot();
            if (!root.exists() && !root.mkdirs()) {
                log.error("创建上传目录失败: {}", root);
                return BaseResponse.error("服务器存储目录不可用: " + root);
            }
            if (!root.isDirectory() || !root.canWrite()) {
                return BaseResponse.error("上传目录不可写，请检查权限: " + root);
            }
            File dest = new File(root, fileName);
            // 绝对路径 + Files.copy 写入，规避部分环境 transferTo 的临时文件/相对路径问题
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            if (!dest.exists() || dest.length() <= 0) {
                log.error("文件写入异常: {}", dest);
                return BaseResponse.error("文件写入异常，请检查磁盘空间与目录权限: " + root);
            }
            log.info("图片已保存: {} ({} bytes)", dest, dest.length());

            String base = accessUrl.endsWith("/") ? accessUrl : accessUrl + "/";
            return BaseResponse.success(base + fileName);
        } catch (FileNotFoundException e) {
            // 常见的 Windows “系统找不到指定路径/拒绝访问” 或目录缺失都在这类异常里
            log.error("图片保存失败(FileNotFoundException): 目标目录={}", getUploadRoot(), e);
            return BaseResponse.error("图片保存失败，请检查服务端上传目录可写且存在: " + getUploadRoot());
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return BaseResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    private boolean hasAllowedSuffix(String lowerName) {
        for (String s : ALLOWED_SUFFIX) {
            if (lowerName.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
