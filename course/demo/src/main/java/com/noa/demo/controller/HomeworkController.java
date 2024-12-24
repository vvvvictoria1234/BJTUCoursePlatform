package com.noa.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseHomework;
import com.noa.demo.entity.HomeworkSubmission;
import com.noa.demo.service.CourseHomeworkService;
import com.noa.demo.service.HomeworkSubmissionService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/homework")

public class HomeworkController {
    @Autowired
    private CourseHomeworkService homeworkService;
    @Autowired
    private HomeworkSubmissionService submissionService;

    // 统一的类型转换方法
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    // 上传作业附件
    @PostMapping("/upload")
    public Result uploadHomework(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.fail();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;

            // 指定文件存储路径
            String uploadDir = "F:/CourseUploads/uploads/homework/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            String filePath = uploadDir + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // 返回文件路径
            return Result.suc(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail();
        }
    }


    @GetMapping("/download/{attachmentPath}")
    public ResponseEntity<UrlResource> downloadHomework(@PathVariable String attachmentPath) {
        try {
            if (attachmentPath == null) {
                return ResponseEntity.notFound().build();
            }

            // 解码路径
            String decodedPath = URLDecoder.decode(attachmentPath, StandardCharsets.UTF_8.toString());

            // 验证文件路径安全性
            Path filePath = Paths.get(decodedPath).normalize();
            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            // 创建文件资源
            UrlResource resource = new UrlResource(filePath.toUri());

            // 获取原始文件名
            String filename = filePath.getFileName().toString();
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 设置响应头
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 根据文件扩展名获取Content-Type
    private String getContentType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }

    @PostMapping("/assign")
    public Result assignHomework(@RequestBody CourseHomework homework) {
        try {
            // 处理courseId的类型转换
            if (homework.getCourseId() != null) {
                homework.setCourseId(convertToLong(homework.getCourseId()));
            }

            // 确保必填字段不为空
            if (homework.getTitle() == null || homework.getCourseId() == null
                    || homework.getDescription() == null || homework.getDeadline() == null) {
                return Result.fail();
            }

            homework.setStartTime(LocalDateTime.now());
            homework.setStatus(homework.getStatus() == null ? 0 : homework.getStatus());
            homework.setTotalScore(homework.getTotalScore() == null ? 100 : homework.getTotalScore());

            return homeworkService.save(homework) ? Result.suc() : Result.fail();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/list")
    public Result listHomework(@RequestBody QueryPageParam query) {
        try {
            HashMap<String, Object> param = query.getParam();
            Long courseId = convertToLong(param.get("courseId"));

            Page<CourseHomework> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<CourseHomework> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(courseId != null, CourseHomework::getCourseId, courseId);

            IPage<CourseHomework> result = homeworkService.page(page, wrapper);
            return Result.suc(result.getRecords(), result.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/submissions")
    public Result listSubmissions(@RequestBody QueryPageParam query) {
        try {
            HashMap<String, Object> param = query.getParam();
            Long homeworkId = convertToLong(param.get("homeworkId"));

            if (homeworkId == null) {
                return Result.fail();
            }

            Page<HomeworkSubmission> page = new Page<>(query.getPageNum(), query.getPageSize());
            LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId);

            IPage<HomeworkSubmission> result = submissionService.page(page, wrapper);
            return Result.suc(result.getRecords(), result.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/submit")
    public Result submitHomework(@RequestBody HomeworkSubmission submission) {
        try {
            // 处理homeworkId的类型转换
            if (submission.getHomeworkId() != null) {
                submission.setHomeworkId(convertToLong(submission.getHomeworkId()));
            }
            return submissionService.submitHomework(submission);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }
    @GetMapping("/detail/{homeworkId}")
    public Result getHomeworkDetail(@PathVariable Long homeworkId) {
        try {
            if (homeworkId == null) {
                return Result.fail();
            }

            // 获取作业信息
            CourseHomework homework = homeworkService.getById(homeworkId);
            if (homework == null) {
                return Result.fail();
            }

            // 将作业信息封装成Map，以便添加额外信息
            Map<String, Object> result = new HashMap<>();
            result.put("homeworkId", homework.getHomeworkId());
            result.put("courseId", homework.getCourseId());
            result.put("title", homework.getTitle());
            result.put("description", homework.getDescription());
            result.put("attachmentPath", homework.getAttachmentPath());
            result.put("startTime", homework.getStartTime());
            result.put("deadline", homework.getDeadline());
            result.put("totalScore", homework.getTotalScore());
            result.put("status", homework.getStatus());

            return Result.suc(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/grade")
    public Result gradeHomework(@RequestBody HomeworkSubmission submission) {
        try {
            if (submission.getSubmissionId() != null) {
                submission.setSubmissionId(convertToLong(submission.getSubmissionId()));
            }
            if (submission.getScore() == null || submission.getScore() < 0 || submission.getScore() > 100) {
                return Result.fail();
            }
            return submissionService.gradeHomework(submission);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }
    @GetMapping("/submission/detail/{homeworkId}")
    public Result getSubmissionDetailByHomeworkId(@PathVariable Long homeworkId,
                                                  @RequestParam String studentId) {
        try {
            if (homeworkId == null || studentId == null) {
                return Result.fail();
            }

            // 构建查询条件
            LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId)
                    .eq(HomeworkSubmission::getStudentId, studentId);

            // 查询提交记录
            HomeworkSubmission submission = submissionService.getOne(wrapper);
            if (submission == null) {
                // 如果没有提交记录，返回空数据但不是错误
                return Result.suc(new HashMap<>());
            }

            // 获取提交详情（包含用户信息等）
            Map<String, Object> detail = submissionService.getSubmissionDetail(submission.getSubmissionId());
            return Result.suc(detail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @GetMapping("/stats/{homeworkId}")
    public Result getHomeworkStats(@PathVariable String homeworkId) {
        try {
            Long id = convertToLong(homeworkId);
            if (id == null) {
                return Result.fail();
            }
            return homeworkService.getHomeworkStats(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }
}