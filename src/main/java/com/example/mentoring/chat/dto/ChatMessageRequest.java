package com.example.mentoring.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
  // 채팅 메시지 전송 요청 DTO

  private String content;
}