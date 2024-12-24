package com.noa.demo.entity;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Conversation {

    private Integer id;

    private String username;

    private String userMessage;

    private String botMessage;

    private String createTime;

}
