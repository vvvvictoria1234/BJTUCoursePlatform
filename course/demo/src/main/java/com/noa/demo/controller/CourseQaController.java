package com.noa.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseQaRecord;
import com.noa.demo.service.CourseQaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/course/qa")
@RequiredArgsConstructor
@Api(tags = "课程AI问答接口")
@Validated
public class CourseQaController {

    private final CourseQaService courseQaService;

    @PostMapping("/ask")
    @ApiOperation("发起AI问答")
    public Result askQuestion(
            @RequestParam Long courseId,
            @RequestParam String studentId,
            @RequestParam String question) {
        CourseQaRecord record = courseQaService.askQuestion(courseId, studentId, question);
        return Result.suc(record);
    }

    @GetMapping("/history")
    @ApiOperation("获取学生问答历史")
    public Result getStudentHistory(
            @RequestParam String studentId,
            @RequestParam Long courseId) {
        List<CourseQaRecord> history = courseQaService.getStudentHistory(studentId, courseId);
        return Result.suc(history);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询课程问答记录")
    public Result getPageByCourse(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<CourseQaRecord> page = courseQaService.getPageByCourse(courseId, pageNum, pageSize);
        return Result.suc(page.getRecords(), page.getTotal());
    }

    @GetMapping("/range")
    @ApiOperation("根据时间范围查询问答记录")
    public Result getByTimeRange(
            @RequestParam Long courseId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        List<CourseQaRecord> records = courseQaService.getByTimeRange(courseId, startTime, endTime);
        return Result.suc(records);
    }

    @DeleteMapping("/{qaId}")
    @ApiOperation("删除问答记录")
    public Result deleteQaRecord(@PathVariable Long qaId) {
        boolean success = courseQaService.deleteQaRecord(qaId);
        return success ? Result.suc() : Result.fail();
    }

    @GetMapping("/count/course/{courseId}")
    @ApiOperation("统计课程问答数量")
    public Result countByCourse(@PathVariable Long courseId) {
        int count = courseQaService.countByCourse(courseId);
        return Result.suc(count);
    }

    @GetMapping("/count/student")
    @ApiOperation("统计学生问答数量")
    public Result countByStudent(
            @RequestParam String studentId,
            @RequestParam Long courseId) {
        int count = courseQaService.countByStudent(studentId, courseId);
        return Result.suc(count);
    }
}