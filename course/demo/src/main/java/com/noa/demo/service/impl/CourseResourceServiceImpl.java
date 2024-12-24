package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseResource;
import com.noa.demo.mapper.CourseResourceMapper;
import com.noa.demo.service.CourseResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource> implements CourseResourceService {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private CourseResourceMapper courseResourceMapper;

    public boolean deleteResource(Long resourceId) {
        // 1. 先获取资源信息
        CourseResource resource = this.getById(resourceId);
        if (resource == null) {
            return false;
        }

        try {
            // 2. 删除物理文件
            String filePath = resource.getFilePath();  // 假设你的实体类中有 filePath 字段
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            // 3. 删除数据库记录
            return this.removeById(resourceId);
        } catch (Exception e) {
            log.error("删除课程资源失败", e);
            return false;
        }
    }
    @Override
    public CourseResource getCourseResourceById(Long resourceId) {
        return courseResourceMapper.selectById(resourceId);
    }

    @Override
    public Result uploadResource(MultipartFile file, String courseId, String fileType) {
        try {
            if (file.isEmpty()) {
                return Result.fail();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File(uploadPath + File.separator + newFilename);
            file.transferTo(dest);

            CourseResource resource = new CourseResource();
            resource.setCourseId(courseId);
            resource.setFileName(originalFilename);
            resource.setFilePath(newFilename);
            resource.setFileType(fileType);

            this.save(resource);
            return Result.suc(resource);
        } catch (IOException e) {
            return Result.fail();
        }
    }

    @Override
    public List<CourseResource> getCourseResources(String courseId) {
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseResource::getCourseId, courseId);
        return this.list(wrapper);
    }

    @Override
    public UrlResource downloadResource(Long resourceId) {
        CourseResource resource = this.getById(resourceId);
        if (resource != null) {
            try {
                Path path = Paths.get(uploadPath + File.separator + resource.getFilePath());
                return new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("文件不存在");
            }
        }
        return null;
    }
}

