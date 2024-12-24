package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamPaper;
import org.springframework.web.multipart.MultipartFile;

public interface ExamPaperService extends IService<ExamPaper> {
    // 上传试卷模板
    ExamPaper uploadPaperTemplate(Long examId, MultipartFile paperFile, MultipartFile answerFile);

    // 配置OCR识别参数
    boolean configureOCR(Long paperId, String ocrConfig);
}
