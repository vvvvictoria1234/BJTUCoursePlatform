package com.noa.demo.utils;




import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@Component
public class OCRUtils {
    static {
        String opencvPath = "C:/Users/32683/Desktop/course_platform/course/demo/src/main/resources/lib/opencv";
        System.setProperty("java.library.path", opencvPath);
        System.loadLibrary("opencv_java451");
    }

    private final Tesseract tesseract;

    public OCRUtils() {
        tesseract = new Tesseract();
        try {

            String dataPath = new File("src/main/resources/tessdata").getAbsolutePath();


            System.out.println("Tesseract datapath: " + dataPath); // 用于调试
            tesseract.setDatapath(dataPath);
            tesseract.setLanguage("chi_sim");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Tesseract", e);
        }
    }

    // 图像预处理
    public Mat preprocessImage(String imagePath) {
        // 读取图像
        Mat image = Imgcodecs.imread(imagePath);
        Mat processed = new Mat();

        // 转换为灰度图
        Imgproc.cvtColor(image, processed, Imgproc.COLOR_BGR2GRAY);

        // 自适应二值化
        Imgproc.adaptiveThreshold(processed, processed, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 2);

        // 去噪
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(processed, processed, Imgproc.MORPH_CLOSE, kernel);

        return processed;
    }

    // 图像矫正
    public Mat correctPerspective(Mat image) {
        // 查找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image.clone(), contours, hierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 找到最大的矩形轮廓（答题卡边框）
        double maxArea = 0;
        MatOfPoint2f maxContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = new MatOfPoint2f(contour.toArray());
            }
        }

        if (maxContour != null) {
            // 多边形逼近
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(maxContour, approx,
                    0.02 * Imgproc.arcLength(maxContour, true), true);

            // 如果是四边形，进行透视变换
            if (approx.total() == 4) {
                Point[] sortedPoints = sortPoints(approx.toArray());
                Mat dst = new Mat();
                Mat perspective_matrix = Imgproc.getPerspectiveTransform(
                        new MatOfPoint2f(sortedPoints),
                        new MatOfPoint2f(
                                new Point(0, 0),
                                new Point(image.cols(), 0),
                                new Point(image.cols(), image.rows()),
                                new Point(0, image.rows())
                        )
                );
                Imgproc.warpPerspective(image, dst, perspective_matrix,
                        new Size(image.cols(), image.rows()));
                return dst;
            }
        }
        return image;
    }

    // 定位答题区域
    public List<Mat> locateAnswerAreas(Mat image, Map<String, int[]> templateConfig) {
        List<Mat> answerAreas = new ArrayList<>();

        for (Map.Entry<String, int[]> area : templateConfig.entrySet()) {
            int[] coords = area.getValue(); // [x, y, width, height]
            Rect roi = new Rect(coords[0], coords[1], coords[2], coords[3]);
            Mat answerArea = new Mat(image, roi);
            answerAreas.add(answerArea);
        }

        return answerAreas;
    }

    // 识别选择题答案
    public String recognizeChoice(Mat answerArea) {
        // 将答案区域分成多个选项区域
        int optionCount = 4; // 假设是A/B/C/D四个选项
        int optionWidth = answerArea.width() / optionCount;

        String answer = "";
        for (int i = 0; i < optionCount; i++) {
            Rect optionRect = new Rect(i * optionWidth, 0, optionWidth, answerArea.height());
            Mat option = new Mat(answerArea, optionRect);

            // 计算填涂程度
            double blackPixelRatio = calculateBlackPixelRatio(option);
            if (blackPixelRatio > 0.5) { // 阈值可调整
                answer += (char)('A' + i);
            }
        }

        return answer;
    }

    // 计算黑色像素比例
    private double calculateBlackPixelRatio(Mat image) {
        int blackPixels = Core.countNonZero(image);
        return 1.0 - (double)blackPixels / (image.rows() * image.cols());
    }

    // 识别文字答案
    public String recognizeText(Mat answerArea) {
        try {
            // 转换Mat为BufferedImage
            BufferedImage bufferedImage = matToBufferedImage(answerArea);
            // 使用Tesseract识别文字
            return tesseract.doOCR(bufferedImage).trim();
        } catch (TesseractException e) {
            throw new RuntimeException("文字识别失败: " + e.getMessage());
        }
    }

    // Mat转BufferedImage
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    // 排序点坐标
    private Point[] sortPoints(Point[] points) {
        // 按照左上、右上、右下、左下的顺序排序点
        Arrays.sort(points, (p1, p2) -> {
            if (p1.y < p2.y) return -1;
            if (p1.y > p2.y) return 1;
            return Double.compare(p1.x, p2.x);
        });

        Point[] result = new Point[4];
        // 左上、右上
        if (points[0].x > points[1].x) {
            result[1] = points[0];
            result[0] = points[1];
        } else {
            result[0] = points[0];
            result[1] = points[1];
        }
        // 左下、右下
        if (points[2].x > points[3].x) {
            result[2] = points[2];
            result[3] = points[3];
        } else {
            result[3] = points[2];
            result[2] = points[3];
        }

        return result;
    }
}
