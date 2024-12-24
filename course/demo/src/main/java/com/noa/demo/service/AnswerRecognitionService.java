package com.noa.demo.service;


import java.util.List;
import java.util.Map;

public interface AnswerRecognitionService {
    /**
     * 处理答题卡
     * @param answerId 答卷ID
     * @param imagePath 答题卡图片路径
     * @param ocrConfig OCR配置JSON
     * @return 处理是否成功
     */
    boolean processAnswerSheet(Long answerId, String imagePath, String ocrConfig);

    /**
     * 批量处理答题卡
     * @param answerIds 答卷ID列表
     * @param imagePaths 答题卡图片路径列表
     * @param ocrConfig OCR配置JSON
     * @return 处理结果，key为answerId，value为处理是否成功
     */
    Map<Long, Boolean> processAnswerSheetBatch(List<Long> answerIds, List<String> imagePaths, String ocrConfig);

    /**
     * 评分并返回详细结果
     * @param answerId 答卷ID
     * @return 评分详细信息
     */
    Map<String, Object> getRecognitionResult(Long answerId);
}
