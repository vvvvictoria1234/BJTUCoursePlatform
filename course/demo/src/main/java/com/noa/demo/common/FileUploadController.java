package com.noa.demo.common;

import org.springframework.beans.factory.annotation.Value; // 修改为正确的Value导入
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;  // 使用SLF4J的Logger
import org.slf4j.LoggerFactory;  // 使用SLF4J的LoggerFactory

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);  // 正确的日志声明方式

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {  // 移除Result的类型参数
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.fail();
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (!isValidImageType(contentType)) {
                return Result.fail();
            }

            // 检查文件大小
            if (file.getSize() > 100 * 1024 * 1024) {
                return Result.fail();
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // 确保目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            File dest = new File(uploadPath + File.separator + newFilename);  // 使用File.separator来处理路径分隔符
            file.transferTo(dest);

            // 返回可访问的URL
            String fileUrl = urlPrefix + "/" + newFilename;
            return Result.success(fileUrl);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail();
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif")
        );
    }
}