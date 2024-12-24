package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.StudentAnswer;
import com.noa.demo.mapper.StudentAnswerMapper;
import com.noa.demo.service.StudentAnswerService;
import com.noa.demo.service.ExamPaperService;
import com.noa.demo.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer>
        implements StudentAnswerService {

    @Autowired
    private ExamPaperService paperService;

    @Value("${file.upload.path}")
    private String uploadPath; // 文件上传路径，需要在配置文件中定义

    @Override
    @Transactional
    public StudentAnswer uploadAnswer(Long examId, String studentId, MultipartFile answerFile) {
        // 检查是否已提交
        boolean exists = lambdaQuery()
                .eq(StudentAnswer::getExamId, examId)
                .eq(StudentAnswer::getStudentId, studentId)
                .count() > 0;    // 修改这里，使用 count() > 0 替代 exists()

        if (exists) {
            throw new ServiceException("已提交过答卷，不能重复提交");
        }

        // 保存答题卡文件
        String answerPath = saveAnswerFile(answerFile, examId, studentId);

        // 创建答卷记录
        StudentAnswer answer = new StudentAnswer();
        answer.setExamId(examId);
        answer.setStudentId(studentId);
        answer.setAnswerPaperPath(answerPath);
        answer.setStatus(0); // 待批改

        save(answer);
        return answer;
    }

    @Override
    @Transactional
    public boolean processAnswerWithOCR(Long answerId) {
        StudentAnswer answer = getById(answerId);
        if (answer == null) {
            throw new ServiceException("答卷不存在");
        }

        // TODO: 调用OCR服务识别答案
        // 1. 获取试卷模板和OCR配置
        // 2. 处理答题卡图片
        // 3. 识别答案
        // 4. 保存识别结果

        return true;
    }

    @Override
    @Transactional
    public boolean autoGrade(Long answerId) {
        // TODO: 实现自动评分逻辑
        // 1. 获取标准答案
        // 2. 对比学生答案
        // 3. 计算得分
        // 4. 保存评分结果

        return true;
    }

    private String saveAnswerFile(MultipartFile file, Long examId, String studentId) {
        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = "answer_" + examId + "_" + studentId + "_" +
                    System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadPath + File.separator + fileName;
            file.transferTo(new File(filePath));
            return fileName;
        } catch (IOException e) {
            throw new ServiceException("答题卡上传失败: " + e.getMessage());
        }
    }
}