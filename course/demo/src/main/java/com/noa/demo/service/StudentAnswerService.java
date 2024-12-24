package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.StudentAnswer;
import org.springframework.web.multipart.MultipartFile;

public interface StudentAnswerService extends IService<StudentAnswer> {
    // 上传答题卡
    StudentAnswer uploadAnswer(Long examId, String studentId, MultipartFile answerFile);

    // 使用OCR识别答案
    boolean processAnswerWithOCR(Long answerId);

    // 自动评分
    boolean autoGrade(Long answerId);
}
