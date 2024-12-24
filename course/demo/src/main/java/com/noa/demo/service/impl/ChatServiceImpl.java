package com.noa.demo.service.impl;

import com.noa.demo.entity.Conversation;
import com.noa.demo.mapper.ChatMapper;
import com.noa.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    ChatMapper chatMapper;

    @Override
    public void addChat(Conversation conversation) {
        chatMapper.insert(conversation);
    }

    @Override
    public List<Conversation> searchByUsername(String username) {
        return chatMapper.getByUsername(username);
    }
}
