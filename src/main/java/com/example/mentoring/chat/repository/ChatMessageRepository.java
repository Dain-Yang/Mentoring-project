package com.example.mentoring.chat.repository;

import com.example.mentoring.chat.entity.ChatMessage;
import com.example.mentoring.chat.entity.ChatRoom;
import com.example.mentoring.member.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

  // 채팅방의 메시지를 시간순으로 조회
  List<ChatMessage> findByChatRoomOrderBySendAtAsc(ChatRoom chatRoom);

  //읽지 않은 메시지 조회 (상대방이 보낸 메시지 중 읽지 않은 것)
  List<ChatMessage> findByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User user);

  //읽지 않은 메시지 개수 조회
  Long countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User user);
}