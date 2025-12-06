package com.example.mentoring.chat.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.chat.dto.ChatMessageRequest;
import com.example.mentoring.chat.dto.ChatMessageResponse;
import com.example.mentoring.chat.dto.ChatRoomResponse;
import com.example.mentoring.chat.service.ChatService;
import com.example.mentoring.member.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final AuthService authService;

  // 채팅방 목록 조회
  @GetMapping("/my")
  public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms() {
    CustomUserDetails currentUser = authService.getCurrentUser();
    List<ChatRoomResponse> responses = chatService.getMyChatRooms(currentUser.getUserId());
    return ResponseEntity.ok(responses);
  }

  // 특정 채팅방 조회
  @GetMapping("/{sessionId}")
  public ResponseEntity<ChatRoomResponse> getChatRoom(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    ChatRoomResponse response = chatService.getChatRoom(sessionId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 채팅 메시지 목록 조회
  @GetMapping("/{sessionId}/messages")
  public ResponseEntity<List<ChatMessageResponse>> getMessages(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    List<ChatMessageResponse> responses = chatService.getMessages(sessionId, currentUser.getUserId());
    return ResponseEntity.ok(responses);
  }

  // 메시지 읽음 처리
  @PatchMapping("/{sessionId}/read")
  public ResponseEntity<Void> markMessagesAsRead(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    chatService.markMessagesAsRead(sessionId, currentUser.getUserId());
    return ResponseEntity.ok().build();
  }
}