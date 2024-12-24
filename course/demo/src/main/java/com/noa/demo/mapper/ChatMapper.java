package com.noa.demo.mapper;

import com.noa.demo.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {


    List<Conversation> getByUsername(String username);

    void insert(Conversation conversation);

}
