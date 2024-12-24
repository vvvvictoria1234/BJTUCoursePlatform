package com.noa.demo.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.ExamSeating;
import com.noa.demo.mapper.ExamSeatingMapper;
import com.noa.demo.service.ExamSeatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ExamSeatingServiceImpl extends ServiceImpl<ExamSeatingMapper, ExamSeating>
        implements ExamSeatingService {

    @Override
    public List<Map<String, Object>> getArrangementSeating(Long arrangementId) {
        return baseMapper.getArrangementSeating(arrangementId);
    }

    @Override
    public ExamSeating getStudentSeating(String studentId, Long examId) {
        return baseMapper.getStudentSeating(studentId, examId);
    }

    @Override
    @Transactional
    public boolean generateSeating(Long arrangementId, List<String> studentIds, boolean isRandom) {
        // 先删除已有的座位安排
        this.lambdaUpdate()
                .eq(ExamSeating::getArrangementId, arrangementId)
                .remove();

        // 如果是随机座位，打乱学生顺序
        if (isRandom) {
            Collections.shuffle(studentIds);
        }

        // 生成座位安排
        List<ExamSeating> seatings = new ArrayList<>();
        int studentCount = studentIds.size();
        int maxRow = (int) Math.ceil(Math.sqrt(studentCount)); // 计算需要的行数
        int maxCol = maxRow; // 默认使用正方形布局

        for (int i = 0; i < studentCount; i++) {
            ExamSeating seating = new ExamSeating();
            seating.setArrangementId(arrangementId);
            seating.setStudentId(studentIds.get(i));

            // 计算行列号（从1开始）
            seating.setSeatRow(i / maxCol + 1);
            seating.setSeatColumn(i % maxCol + 1);

            // 生成试题版本号（A、B、C三种）
            seating.setQuestionVersion(generateQuestionVersion(i));
            System.out.println("Generated version: " + seating.getQuestionVersion());

            seatings.add(seating);
        }

        // 批量保存座位安排
        return this.saveBatch(seatings);
    }

    /**
     * 生成试题版本号
     * @param index 学生序号
     * @return 试题版本（A、B、C）
     */
    private String generateQuestionVersion(int index) {
        // 按顺序循环分配A、B、C三种试卷版本
        String[] versions = {"A", "B", "C"};
        return versions[index % versions.length];
    }
}
