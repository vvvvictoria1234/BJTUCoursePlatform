package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseResource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseResourceService extends IService<CourseResource> {
    // 上传课程资源
    Result uploadResource(MultipartFile file, String courseId, String fileType);
    // 获取课程资源列表
    List<CourseResource> getCourseResources(String courseId);
    // 下载课程资源
    UrlResource downloadResource(Long resourceId);

    public boolean deleteResource(Long resourceId);


    CourseResource getCourseResourceById(Long resourceId);
}
