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

            String dir = uploadDir.endsWith("/") || uploadDir.endsWith("\\") ? uploadDir : uploadDir + "/";
            File dirFile = new File(dir);
            if (!dirFile.exists() && !dirFile.mkdirs()) {
                log.error("创建上传目录失败: {}", dir);
                return BaseResponse.error("服务器存储目录不可用");
            }
            File dest = new File(dir + fileName);
            file.transferTo(dest);

            String base = accessUrl.endsWith("/") ? accessUrl : accessUrl + "/";
            return BaseResponse.success(base + fileName);
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
