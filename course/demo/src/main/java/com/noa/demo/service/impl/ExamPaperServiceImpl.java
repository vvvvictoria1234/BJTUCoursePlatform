package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.ExamPaper;
import com.noa.demo.mapper.ExamPaperMapper;
import com.noa.demo.service.ExamPaperService;
import com.noa.demo.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {

    @Value("F:/CourseUploads")
    private String uploadPath;

    @Override
    @Transactional
    public ExamPaper uploadPaperTemplate(Long examId, MultipartFile paperFile, MultipartFile answerFile) {
        // 保存文件到指定路径
        String paperPath = saveFile(paperFile, "paper");
        String answerPath = saveFile(answerFile, "answer");

        // 创建试卷记录
        ExamPaper paper = new ExamPaper();
        paper.setExamId(examId);
        paper.setPaperTemplatePath(paperPath);
        paper.setAnswerTemplatePath(answerPath);

        save(paper);
        return paper;
    }

    @Override
    public boolean configureOCR(Long paperId, String ocrConfig) {
        return lambdaUpdate()
                .eq(ExamPaper::getPaperId, paperId)
                .set(ExamPaper::getOcrConfig, ocrConfig)
                .update();
    }

    private String saveFile(MultipartFile file, String prefix) {
        try {
            String fileName = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadPath + File.separator + fileName;
            file.transferTo(new File(filePath));
            return fileName;
        } catch (IOException e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }
}
