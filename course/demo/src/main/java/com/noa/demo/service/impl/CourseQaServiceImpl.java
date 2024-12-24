package com.noa.demo.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.CourseInformation;
import com.noa.demo.entity.CourseQaRecord;
import com.noa.demo.mapper.CourseInformationMapper;
import com.noa.demo.mapper.CourseQaRecordMapper;
import com.noa.demo.service.CourseQaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CourseQaServiceImpl extends ServiceImpl<CourseQaRecordMapper, CourseQaRecord>
        implements CourseQaService {

    @Value("${chatglm.api.url}")
    private String apiUrl;

    @Value("${chatglm.api.key}")
    private String apiKey;

    @Value("${chatglm.api.model}")
    private String apiModel;

    @Autowired
    private CourseInformationMapper courseMapper; // 添加课程Mapper注入

    /**
     * AI问答
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseQaRecord askQuestion(Long courseId, String studentId, String question) {
        try {
            // 获取课程信息
            String courseName = courseMapper.getCourseName(courseId);
            if (courseName == null) {
                throw new RuntimeException("课程不存在");
            }

            // 构建带有课程背景的提示词
            String prompt = String.format("请作为在%s课程教学多年，经验丰富的教师，来回答学生的问题。\n\n学生问题：%s",
                    courseName, question);

            // 调用AI获取回答
            String answer = callLLM(prompt);

            // 创建问答记录
            CourseQaRecord record = new CourseQaRecord();
            record.setCourseId(courseId);
            record.setStudentId(studentId);
            record.setQuestion(question);  // 保存原始问题
            record.setAnswer(answer);

            // 保存记录
            baseMapper.insert(record);

            return record;
        } catch (Exception e) {
            log.error("AI问答失败: courseId={}, studentId={}, question={}",
                    courseId, studentId, question, e);
            throw new RuntimeException("AI助手暂时无法回答，请稍后重试");
        }
    }

    /**
     * 调用大模型API
     */
    private String callLLM(String prompt) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4-plus");

            // 构建messages数组
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            // 添加其他参数
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            // 打印请求参数用于调试
            log.debug("API请求参数: {}", requestBody);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            // 设置超时
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(5000000);
            factory.setReadTimeout(5000000);
            restTemplate.setRequestFactory(factory);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl,
                    entity,
                    Map.class
            );

            // 打印响应结果用于调试
            log.debug("API响应结果: {}", response.getBody());

            // 解析响应
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty() && choices.get(0).containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString();
                }
            }

            return "抱歉，AI助手暂时无法回答您的问题";

        } catch (Exception e) {
            log.error("调用大模型API失败", e);
            throw new RuntimeException("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 获取学生的问答历史记录
     */
    @Override
    public List<CourseQaRecord> getStudentHistory(String studentId, Long courseId) {
        return baseMapper.selectStudentHistory(studentId, courseId);
    }

    /**
     * 分页查询课程的问答记录
     */
    @Override
    public IPage<CourseQaRecord> getPageByCourse(Long courseId, Integer pageNum, Integer pageSize) {
        Page<CourseQaRecord> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPageByCourseId(page, courseId);
    }

    /**
     * 根据时间范围查询问答记录
     */
    @Override
    public List<CourseQaRecord> getByTimeRange(Long courseId, LocalDateTime startTime,
                                               LocalDateTime endTime) {
        return baseMapper.selectByTimeRange(courseId, startTime, endTime);
    }

    /**
     * 删除问答记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQaRecord(Long qaId) {
        return baseMapper.deleteById(qaId) > 0;
    }

    /**
     * 统计课程问答数量
     */
    @Override
    public int countByCourse(Long courseId) {
        return baseMapper.countByCourseId(courseId);
    }

    /**
     * 统计学生问答数量
     */
    @Override
    public int countByStudent(String studentId, Long courseId) {
        return baseMapper.countByStudentId(studentId, courseId);
    }


}