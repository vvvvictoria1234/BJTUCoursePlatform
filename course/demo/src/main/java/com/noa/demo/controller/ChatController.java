package com.noa.demo.controller;

import com.noa.demo.entity.Conversation;
import com.noa.demo.service.ChatService;
import com.noa.demo.utils.QianfanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("chat")
public class ChatController {



    @Autowired
    QianfanUtil qianfanUtil;

    @Autowired
    ChatService chatService;

    private String format(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }



    @GetMapping("/list")
    public List<Conversation> getChat(HttpServletRequest request){
        //String username= (String) request.getSession().getAttribute("username");

        //这里直接将名字硬编码进去，后续需要去改一下要自动获取到当前用户的姓名
        String username="李四";
        List<Conversation> conversations=chatService.searchByUsername(username);
        conversations.forEach(System.out::println);

        //model.addAttribute("conversations",conversations);
        return conversations;
    }



    @PostMapping("/chat")
    public String chat(HttpServletRequest httpServletRequest,@RequestBody Map<String, String> request){
        //String username= (String) request.getSession().getAttribute("username");

        //这里直接将名字硬编码进去，后续需要去改一下要自动获取到当前用户的姓名
        String username="李四";
        String content = request.get("content");

        System.out.println(content);

//        String res="回复";
//        Conversation conversation = new Conversation(id++,username, content, res, format(new Date());
//        conversations.add(conversation);

         String res = qianfanUtil.addMessage(content);
        Conversation conversation = new Conversation(null, username, content, res, format(new Date()));
        chatService.addChat(conversation);

        return "redirect:list";
    }


}
