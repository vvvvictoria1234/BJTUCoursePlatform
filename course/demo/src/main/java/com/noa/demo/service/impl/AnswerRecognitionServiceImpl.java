package com.noa.demo.service.impl;

import com.noa.demo.entity.*;
import com.noa.demo.service.AnswerRecognitionService;
import com.noa.demo.service.ExamQuestionService;
import com.noa.demo.service.QuestionScoreService;
import com.noa.demo.service.StudentAnswerService;
import com.noa.demo.utils.OCRUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AnswerRecognitionServiceImpl implements AnswerRecognitionService {

    @Autowired
    private OCRUtils ocrUtils;

    @Autowired
    private ExamQuestionService questionService;

    @Autowired
    private QuestionScoreService scoreService;

    @Autowired
    private StudentAnswerService studentAnswerService;

    @Value("${ocr.image.threshold:0.8}")
    private double recognitionThreshold;

    @Override
    @Transactional
    public boolean processAnswerSheet(Long answerId, String imagePath, String ocrConfig) {
        try {
            log.info("开始处理答题卡，answerId: {}", answerId);

            // 1. 获取学生答卷信息
            StudentAnswer answer = studentAnswerService.getById(answerId);
            if (answer == null) {
                log.error("答卷不存在，answerId: {}", answerId);
                return false;
            }

            // 2. 图像预处理和矫正
            Mat processedImage = ocrUtils.preprocessImage(imagePath);
            Mat correctedImage = ocrUtils.correctPerspective(processedImage);

            // 3. 解析OCR配置
            JSONObject config = new JSONObject(ocrConfig);
            List<ExamQuestion> questions = questionService.getQuestionsByExamId(answer.getExamId());

            // 4. 处理每个题目
            List<QuestionScore> scores = processQuestions(correctedImage, config, questions, answerId);

            // 5. 计算总分
            double totalScore = scores.stream()
                    .mapToDouble(QuestionScore::getScore)
                    .sum();

            // 6. 更新答卷状态和总分
            answer.setTotalScore(totalScore);
            answer.setStatus(2); // 已批改
            studentAnswerService.updateById(answer);

            // 7. 保存得分记录
            boolean success = scoreService.saveBatch(scores);

            log.info("答题卡处理完成，answerId: {}, totalScore: {}", answerId, totalScore);
            return success;

        } catch (Exception e) {
            log.error("答题卡处理失败，answerId: " + answerId, e);
            throw new RuntimeException("答题卡处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Long, Boolean> processAnswerSheetBatch(List<Long> answerIds, List<String> imagePaths, String ocrConfig) {
        Map<Long, Boolean> results = new HashMap<>();
        for (int i = 0; i < answerIds.size(); i++) {
            try {
                results.put(answerIds.get(i),
                        processAnswerSheet(answerIds.get(i), imagePaths.get(i), ocrConfig));
            } catch (Exception e) {
                log.error("批量处理答题卡失败，answerId: " + answerIds.get(i), e);
                results.put(answerIds.get(i), false);
            }
        }
        return results;
    }

    @Override
    public Map<String, Object> getRecognitionResult(Long answerId) {
        Map<String, Object> result = new HashMap<>();

        // 获取答卷信息
        StudentAnswer answer = studentAnswerService.getById(answerId);
        if (answer == null) {
            return result;
        }

        // 获取得分详情
        List<QuestionScore> scores = scoreService.getAnswerScores(answerId);

        // 统计数据
        int totalQuestions = scores.size();
        int correctCount = (int) scores.stream()
                .filter(s -> s.getIsCorrect() != null && s.getIsCorrect() == 1)
                .count();

        // 组装结果
        result.put("answerId", answerId);
        result.put("studentId", answer.getStudentId());
        result.put("totalScore", answer.getTotalScore());
        result.put("totalQuestions", totalQuestions);
        result.put("correctCount", correctCount);
        result.put("scores", scores);
        result.put("accuracy", totalQuestions > 0 ?
                (double) correctCount / totalQuestions : 0);

        return result;
    }

    private List<QuestionScore> processQuestions(Mat image, JSONObject config,
                                                 List<ExamQuestion> questions, Long answerId) {
        List<QuestionScore> scores = new ArrayList<>();
        JSONArray answerAreaConfigs = config.getJSONArray("answerAreas");

        for (ExamQuestion question : questions) {
            // 查找题目对应的答题区域配置
            JSONObject areaConfig = findQuestionAreaConfig(answerAreaConfigs, question.getQuestionId());
            if (areaConfig == null) {
                log.warn("未找到题目的答题区域配置，questionId: {}", question.getQuestionId());
                continue;
            }

            // 提取答题区域
            Map<String, int[]> areaCoords = new HashMap<>();
            areaCoords.put("answer", new int[]{
                    areaConfig.getInt("x"),
                    areaConfig.getInt("y"),
                    areaConfig.getInt("width"),
                    areaConfig.getInt("height")
            });

            List<Mat> extractedAreas = ocrUtils.locateAnswerAreas(image, areaCoords);
            if (extractedAreas.isEmpty()) {
                log.warn("未能定位答题区域，questionId: {}", question.getQuestionId());
                continue;
            }

            // 识别答案
            Mat answerArea = extractedAreas.get(0);
            String studentAnswer = recognizeAnswer(answerArea, question.getQuestionType());

            // 评分
            QuestionScore score = calculateScore(question, studentAnswer);
            score.setAnswerId(answerId);
            score.setQuestionId(question.getQuestionId());
            scores.add(score);
        }

        return scores;
    }

    private JSONObject findQuestionAreaConfig(JSONArray answerAreas, Long questionId) {
        for (int i = 0; i < answerAreas.length(); i++) {
            JSONObject area = answerAreas.getJSONObject(i);
            if (area.getLong("questionId") == questionId) {
                return area;
            }
        }
        return null;
    }

    private String recognizeAnswer(Mat answerArea, Integer questionType) {
        switch (questionType) {
            case 1: // 单选题
            case 2: // 多选题
                return ocrUtils.recognizeChoice(answerArea);
            case 3: // 判断题
                String choice = ocrUtils.recognizeChoice(answerArea);
                return choice.equals("A") ? "√" : "×";
            case 4: // 填空题
            case 5: // 主观题
                return ocrUtils.recognizeText(answerArea);
            default:
                return "";
        }
    }

    private QuestionScore calculateScore(ExamQuestion question, String studentAnswer) {
        QuestionScore score = new QuestionScore();
        score.setStudentAnswer(studentAnswer);

        switch (question.getQuestionType()) {
            case 1: // 单选题
            case 3: // 判断题
                boolean isCorrect = question.getStandardAnswer().equalsIgnoreCase(studentAnswer);
                score.setScore(isCorrect ? question.getScore() : 0.0);
                score.setIsCorrect(isCorrect ? 1 : 0);
                break;

            case 2: // 多选题
                Set<String> standardAnswers = new HashSet<>(Arrays.asList(
                        question.getStandardAnswer().split("")));
                Set<String> studentAnswers = new HashSet<>(Arrays.asList(
                        studentAnswer.split("")));
                boolean isMultiCorrect = standardAnswers.equals(studentAnswers);
                score.setScore(isMultiCorrect ? question.getScore() : 0.0);
                score.setIsCorrect(isMultiCorrect ? 1 : 0);
                break;

            case 4: // 填空题
                double similarity = calculateTextSimilarity(
                        question.getStandardAnswer(), studentAnswer);
                score.setScore(similarity * question.getScore());
                score.setIsCorrect(similarity >= recognitionThreshold ? 1 : 0);
                break;

            case 5: // 主观题
                score.setScore(0.0);
                score.setIsCorrect(null); // 需要人工评分
                break;
        }

        return score;
    }

    private double calculateTextSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }

        // 使用编辑距离计算相似度
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        int maxLength = Math.max(str1.length(), str2.length());
        return 1 - (double) dp[str1.length()][str2.length()] / maxLength;
    }
}
