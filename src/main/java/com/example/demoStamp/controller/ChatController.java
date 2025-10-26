package com.example.demoStamp.controller;

import com.example.demoStamp.model.ChatMessage;
import com.example.demoStamp.model.MessageType;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // handle chat messages
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println("📨 " + chatMessage.getSender() + ": " + chatMessage.getContent());
        return chatMessage;
    }

    // handle user joining
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType(MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + " joined the chat");
        System.out.println("👋 " + chatMessage.getSender() + " joined");
        return chatMessage;
    }
}
