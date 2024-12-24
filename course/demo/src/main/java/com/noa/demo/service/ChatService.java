package com.noa.demo.service;

import com.noa.demo.entity.Conversation;

import java.util.List;

public interface ChatService {

    void addChat(Conversation conversation);

    List<Conversation> searchByUsername(String username);

}
