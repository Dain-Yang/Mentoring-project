package com.example.mentoring.chat.dto;

import com.example.mentoring.chat.entity.ChatMessage;
import com.example.mentoring.chat.entity.ChatRoom;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.member.entity.User;
import java.util.List;
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
public class ChatRoomResponse {
  // 채팅방 응답 DTO

  private UUID sessionId;
  private Boolean isLocked;
  private String partnerNickname;
  private String lastMessage;
  private LocalDateTime lastMessageTime;
  private Long unreadCount;
  private LocalDateTime createdAt;

  // unreadCount는 Repository에서 가져와야 하므로 인자로 받음
  public static ChatRoomResponse of(ChatRoom chatRoom, User currentUser, Long unreadCount) {
    Session session = chatRoom.getSession();
    User partner = session.getMentor().getId().equals(currentUser.getId())
        ? session.getMentee()
        : session.getMentor();

    // 마지막 메시지 추출
    List<ChatMessage> messages = chatRoom.getMessages();
    ChatMessage lastMsg = (messages != null && !messages.isEmpty())
        ? messages.get(messages.size() - 1)
        : null;

    return ChatRoomResponse.builder()
        .sessionId(chatRoom.getSessionId())
        .isLocked(chatRoom.getIsLocked())
        .partnerNickname(partner.getNickname())
        .lastMessage(lastMsg != null ? lastMsg.getContent() : null)
        .lastMessageTime(lastMsg != null ? lastMsg.getSendAt() : null)
        .unreadCount(unreadCount)
        .createdAt(chatRoom.getCreatedAt())
        .build();
  }
}
