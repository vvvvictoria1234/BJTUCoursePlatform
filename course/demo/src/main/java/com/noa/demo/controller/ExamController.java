package com.noa.demo.controller;



import com.noa.demo.common.Result;
import com.noa.demo.entity.*;
import com.noa.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ExamArrangementService arrangementService;

    @Autowired
    private ExamPaperService paperService;

    @Autowired
    private StudentAnswerService studentAnswerService;

    // 创建新考试
    @PostMapping("/create")
    public Result createExam(@RequestBody ExamInformation exam) {
        return examService.createExam(exam) ? Result.suc() : Result.fail();
    }

    // 更新考试信息
    @PostMapping("/update")
    public Result updateExam(@RequestBody ExamInformation exam) {
        return examService.updateExam(exam) ? Result.suc() : Result.fail();
    }

    // 获取教师创建的所有考试
    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherExams(@PathVariable String teacherId) {
        List<ExamInformation> exams = examService.getTeacherExams(teacherId);
        return Result.suc(exams);
    }

    // 获取课程的所有考试
    @GetMapping("/course/{courseId}")
    public Result getCourseExams(@PathVariable Long courseId) {
        List<ExamInformation> exams = examService.getCourseExams(courseId);
        return Result.suc(exams);
    }

    // 获取可用教室列表
    @GetMapping("/classroom/available")
    public Result getAvailableClassrooms() {
        List<Classroom> classrooms = classroomService.getAvailableClassrooms();
        return Result.suc(classrooms);
    }

    // 创建考场安排
    @PostMapping("/arrangement/create")
    public Result createArrangement(@RequestBody ExamArrangement arrangement) {
        return arrangementService.createArrangement(arrangement) ? Result.suc() : Result.fail();
    }


    // 生成随机座位安排
    @PostMapping("/arrangement/{arrangementId}/random")
    public Result generateRandomSeating(
            @PathVariable Long arrangementId,
            @RequestBody List<String> studentIds) {
        return arrangementService.generateRandomSeating(arrangementId, studentIds) ?
                Result.suc() : Result.fail();
    }

    // 生成顺序座位安排
    @PostMapping("/arrangement/{arrangementId}/ordered")
    public Result generateOrderedSeating(
            @PathVariable Long arrangementId,
            @RequestBody List<String> studentIds) {
        return arrangementService.generateOrderedSeating(arrangementId, studentIds) ?
                Result.suc() : Result.fail();
    }

    // 获取考试的考场安排
    @GetMapping("/arrangement/{examId}")
    public Result getExamArrangements(@PathVariable Long examId) {
        List<ExamArrangement> arrangements = arrangementService.getExamArrangements(examId);
        return Result.suc(arrangements);
    }


    @PostMapping("/paper/upload")
    public Result uploadPaperTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long examId) {  // 移除type参数，简化接口
        ExamPaper paper = paperService.uploadPaperTemplate(examId, file, null);
        return Result.suc(paper);
    }

    @PostMapping("/answer/upload")  // 新增一个答题卡专用接口
    public Result uploadAnswerTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long examId) {
        ExamPaper paper = paperService.uploadPaperTemplate(examId, null, file);
        return Result.suc(paper);
    }

    // 配置OCR识别参数
    @PostMapping("/paper/{paperId}/ocr")
    public Result configureOCR(
            @PathVariable Long paperId,
            @RequestBody String ocrConfig) {
        return paperService.configureOCR(paperId, ocrConfig) ? Result.suc() : Result.fail();
    }

    // 学生提交答题卡
    @PostMapping("/answer/submit")
    public Result submitAnswer(
            @RequestParam("examId") Long examId,
            @RequestParam("studentId") String studentId,
            @RequestParam("answerFile") MultipartFile answerFile) {
        StudentAnswer answer = studentAnswerService.uploadAnswer(examId, studentId, answerFile);
        return Result.suc(answer);
    }

    // 处理答题卡OCR识别
    @PostMapping("/answer/{answerId}/process")
    public Result processAnswer(@PathVariable Long answerId) {
        return studentAnswerService.processAnswerWithOCR(answerId) ? Result.suc() : Result.fail();
    }
    @Autowired
    private ExamSeatingService examSeatingService;

    @GetMapping("/seating/{arrangementId}")
    public Result getArrangementSeating(@PathVariable Long arrangementId) {
        List<Map<String, Object>> seatingList = examSeatingService.getArrangementSeating(arrangementId);
        return Result.suc(seatingList);
    }

    @GetMapping("/seating/student")
    public Result getStudentSeating(
            @RequestParam String studentId,
            @RequestParam Long examId) {
        ExamSeating seating = examSeatingService.getStudentSeating(studentId, examId);
        return Result.suc(seating);
    }

    // 自动评分
    @PostMapping("/answer/{answerId}/grade")
    public Result autoGrade(@PathVariable Long answerId) {
        return studentAnswerService.autoGrade(answerId) ? Result.suc() : Result.fail();
    }
}
