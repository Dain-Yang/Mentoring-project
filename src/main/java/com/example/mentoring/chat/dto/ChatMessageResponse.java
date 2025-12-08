package com.example.mentoring.chat.dto;

import com.example.mentoring.chat.entity.ChatMessage;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
  // 채팅 메시지 응답 DTO

  private UUID id;
  private UUID sessionId;
  private UUID senderId;
  private String senderNickname;
  private String content;
  private LocalDateTime sendAt;
  private Boolean isRead;

  public static ChatMessageResponse from(ChatMessage message) {
    return ChatMessageResponse.builder()
        .id(message.getId())
        .sessionId(message.getChatRoom().getSessionId())
        .senderId(message.getSender().getId())
        .senderNickname(message.getSender().getNickname())
        .content(message.getContent())
        .sendAt(message.getSendAt())
        .isRead(message.getIsRead())
        .build();
  }
}
