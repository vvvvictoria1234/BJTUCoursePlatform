package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("classroom")
public class Classroom {
    @TableId(type = IdType.AUTO)
    private Long classroomId;

    private String buildingName;
    private String roomNumber;
    private Integer capacity;
    private Integer rowCount;
    private Integer columnCount;
    private Integer status;
}
