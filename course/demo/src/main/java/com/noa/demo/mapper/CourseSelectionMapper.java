package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.CourseSelection;
import com.noa.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
    // 自定义查询方法：获取选课学生信息
    @Select("SELECT u.* FROM user u " +
            "JOIN course_selection cs ON u.id = cs.student_id " +
            "WHERE cs.course_id = #{courseId} AND cs.status = 1")
    List<User> getStudentsByCourseId(String courseId);
}
