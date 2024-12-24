package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.entity.CourseQaRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CourseQaRecordMapper extends BaseMapper<CourseQaRecord> {
    /**
     * 根据课程ID获取课程名称
     */


    /**
     * 根据问答ID查询记录
     */
    CourseQaRecord selectByQaId(@Param("qaId") Long qaId);

    /**
     * 查询学生的问答历史记录
     */
    List<CourseQaRecord> selectStudentHistory(@Param("studentId") String studentId,
                                              @Param("courseId") Long courseId);

    /**
     * 分页查询课程的所有问答记录
     */
    IPage<CourseQaRecord> selectPageByCourseId(Page<CourseQaRecord> page,
                                               @Param("courseId") Long courseId);

    /**
     * 插入新的问答记录
     */
    int insert(CourseQaRecord record);

    /**
     * 更新问答记录
     */
    int updateById(CourseQaRecord record);

    /**
     * 删除问答记录
     */
    int deleteById(@Param("qaId") Long qaId);
    /**
     * 批量插入问答记录
     */
    int batchInsert(@Param("records") List<CourseQaRecord> records);

    /**
     * 根据时间范围查询问答记录
     */
    List<CourseQaRecord> selectByTimeRange(@Param("courseId") Long courseId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计课程问答数量
     */
    int countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计学生问答数量
     */
    int countByStudentId(@Param("studentId") String studentId,
                         @Param("courseId") Long courseId);
}