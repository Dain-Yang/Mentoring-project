package com.example.mentoring.chat.service;

import com.example.mentoring.chat.dto.ChatMessageRequest;
import com.example.mentoring.chat.dto.ChatMessageResponse;
import com.example.mentoring.chat.dto.ChatRoomResponse;
import com.example.mentoring.chat.entity.ChatMessage;
import com.example.mentoring.chat.entity.ChatRoom;
import com.example.mentoring.chat.repository.ChatMessageRepository;
import com.example.mentoring.chat.repository.ChatRoomRepository;
import com.example.mentoring.match.event.SessionCreatedEvent;
import com.example.mentoring.match.event.SessionCompletedEvent;
import org.springframework.context.event.EventListener;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;

  // 세션 생성 이벤트를 받아서 채팅방 생성
  @EventListener
  @Transactional
  public void handleSessionCreated(SessionCreatedEvent event) {
    createChatRoom(event.getSession());
  }

  // 세션 종료 이벤트를 받아서 채팅방 잠금
  @EventListener
  @Transactional
  public void handleSessionCompleted(SessionCompletedEvent event) {
    lockChatRoom(event.getSession().getId());
  }

  @Transactional
  public ChatRoom createChatRoom(Session session) {
    if (chatRoomRepository.existsById(session.getId())) {
      return chatRoomRepository.findById(session.getId()).get();
    }

    ChatRoom chatRoom = ChatRoom.builder().session(session).build();
    return chatRoomRepository.save(chatRoom);
  }

  public ChatRoomResponse getChatRoom(Integer sessionId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    ChatRoom chatRoom = chatRoomRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    if (!chatRoom.getSession().isParticipant(user)) {
      throw new IllegalStateException("채팅방 참여자만 조회할 수 있습니다.");
    }

    Long unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, user);
    return ChatRoomResponse.of(chatRoom, user, unreadCount);
  }

  public List<ChatRoomResponse> getMyChatRooms(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    return chatRoomRepository.findBySession_MentorOrSession_MenteeOrderByCreatedAtDesc(user, user).stream()
        .map(chatRoom -> {
          Long unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, user);
          return ChatRoomResponse.of(chatRoom, user, unreadCount);
        })
        .collect(Collectors.toList());
  }

  @Transactional
  public ChatMessageResponse sendMessage(Integer sessionId, Integer userId, ChatMessageRequest request) {
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    ChatRoom chatRoom = chatRoomRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    if (!chatRoom.getSession().isParticipant(sender)) {
      throw new IllegalStateException("채팅방 참여자만 메시지를 보낼 수 있습니다.");
    }

    if (!chatRoom.canSendMessage()) {
      throw new IllegalStateException("잠긴 채팅방에는 메시지를 보낼 수 없습니다.");
    }

    ChatMessage message = ChatMessage.builder()
        .chatRoom(chatRoom)
        .sender(sender)
        .content(request.getContent())
        .build();

    ChatMessage savedMessage = chatMessageRepository.save(message);
    return ChatMessageResponse.from(savedMessage);
  }

  public List<ChatMessageResponse> getMessages(Integer sessionId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    ChatRoom chatRoom = chatRoomRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    if (!chatRoom.getSession().isParticipant(user)) {
      throw new IllegalStateException("채팅방 참여자만 메시지를 조회할 수 있습니다.");
    }

    return chatMessageRepository.findByChatRoomOrderBySendAtAsc(chatRoom).stream()
        .map(ChatMessageResponse::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public void markMessagesAsRead(Integer sessionId, Integer userId) {
    User reader = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    ChatRoom chatRoom = chatRoomRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    if (!chatRoom.getSession().isParticipant(reader)) {
      throw new IllegalStateException("채팅방 참여자만 읽음 처리할 수 있습니다.");
    }

    List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomAndSenderNotAndIsReadFalse(chatRoom, reader);
    unreadMessages.forEach(ChatMessage::markAsRead);
  }

  @Transactional
  public void lockChatRoom(Integer sessionId) {
    ChatRoom chatRoom = chatRoomRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    chatRoom.lock();
  }
}