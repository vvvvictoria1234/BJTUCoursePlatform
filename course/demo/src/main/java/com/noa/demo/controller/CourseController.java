package com.noa.demo.controller;

import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseInformation;
import com.noa.demo.entity.CourseNotice;
import com.noa.demo.entity.CourseResource;
import com.noa.demo.entity.User;
import com.noa.demo.service.CourseNoticeService;
import com.noa.demo.service.CourseResourceService;
import com.noa.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseResourceService resourceService;
    @Autowired
    private CourseNoticeService noticeService;

    // 获取课程详情
    @GetMapping("/detail/{courseId}")
    public Result getCourseDetail(@PathVariable String courseId) {
        CourseInformation course = courseService.getCourseDetail(courseId);
        return Result.suc(course);
    }

    @GetMapping("/teacher/courses/{teacherId}")
    public Result getTeacherCourses(@PathVariable String teacherId) {
        List<CourseInformation> courses = courseService.getTeacherCourses(teacherId);
        return Result.suc(courses);
    }

    @GetMapping("/student/courses/{studentId}")
    public Result getStudentCourses(@PathVariable String studentId) {
        List<CourseInformation> courses = courseService.getStudentCourses(studentId);
        return Result.suc(courses);
    }

    // 更新课程信息（教师）
    @PostMapping("/update")
    public Result updateCourse(@RequestBody CourseInformation course) {
        boolean success = courseService.updateCourseInfo(course);
        return success ? Result.suc() : Result.fail();
    }

    // 获取选课学生列表（教师）
    @GetMapping("/students/{courseId}")
    public Result getCourseStudents(@PathVariable String courseId) {
        List<User> students = courseService.getCourseStudents(courseId);
        return Result.suc(students);
    }

    // 获取课程资源列表
    @GetMapping("/resources/{courseId}")
    public Result getCourseResources(@PathVariable String courseId) {
        List<CourseResource> resources = resourceService.getCourseResources(courseId);
        return Result.suc(resources);
    }

    @PostMapping("/save")
    public Result saveCourse(@RequestBody CourseInformation course) {
        boolean success = courseService.save(course);
        return success ? Result.suc() : Result.fail();
    }

    // 上传课程资源（教师）
    @PostMapping("/resource/upload")
    public Result uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") String courseId,
            @RequestParam("fileType") String fileType) {
        return resourceService.uploadResource(file, courseId, fileType);
    }

//    // 下载课程资源
//    @GetMapping("/resource/download/{resourceId}")
//    public ResponseEntity<UrlResource> downloadResource(@PathVariable Long resourceId) {
//        UrlResource resource = resourceService.downloadResource(resourceId);
//        if (resource != null) {
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
//                    .body(resource);
//        }
//        return ResponseEntity.notFound().build();
//    }

    @GetMapping("/resource/download/{resourceId}")
    public ResponseEntity<UrlResource> downloadResource(@PathVariable Long resourceId) {
        try {
            CourseResource courseResource = resourceService.getCourseResourceById(resourceId);
            if (courseResource == null) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = resourceService.downloadResource(resourceId);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // 获取文件名和文件类型
            String filename = courseResource.getFileName();  // 使用驼峰命名的getter方法
            String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 设置Content-Type
            String contentType = getContentType(fileExtension);

            // 设置Content-Disposition，确保文件名正确编码
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 根据文件扩展名获取对应的Content-Type
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




    // 删除课程资源（教师）
    @DeleteMapping("/resource/{resourceId}")
    public Result deleteResource(@PathVariable Long resourceId) {
        boolean success = resourceService.deleteResource(resourceId);
        return success ? Result.suc() : Result.fail();
    }




    // 发布课程通知（教师）
    @PostMapping("/notice/publish")
    public Result publishNotice(@RequestBody CourseNotice notice) {
        boolean success = noticeService.publishNotice(notice);
        return success ? Result.suc() : Result.fail();
    }


    // 获取课程通知列表
    @GetMapping("/notices/{courseId}")
    public Result getCourseNotices(@PathVariable String courseId) {
        List<CourseNotice> notices = noticeService.getCourseNotices(courseId);
        return Result.suc(notices);
    }
}