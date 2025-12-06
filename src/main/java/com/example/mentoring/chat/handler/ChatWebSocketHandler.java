package com.example.mentoring.chat.handler;

import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.chat.dto.ChatMessageRequest;
import com.example.mentoring.chat.dto.ChatMessageResponse;
import com.example.mentoring.chat.service.ChatService;
import com.example.mentoring.chat.service.RedisPublisher;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private final ChatService chatService;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final RedisPublisher redisPublisher;

  // sessionId -> Map<WebSocketSessionId, WebSocketSession>
  private final Map<Integer, Map<String, WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Integer sessionId = (Integer) session.getAttributes().get("sessionId");
    User user = getUserFromSession(session);

    if (user == null || sessionId == null) {
      session.close(CloseStatus.BAD_DATA);
      return;
    }

    log.info("WebSocket 연결 수립: sessionId={}, userId={}, nickname={}", sessionId, user.getId(), user.getNickname());

    chatRooms.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
        .put(session.getId(), session);

    // 연결 시 읽음 처리
    chatService.markMessagesAsRead(sessionId, user.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Integer sessionId = (Integer) session.getAttributes().get("sessionId");
    User user = getUserFromSession(session);

    if (user == null) return; // 방어 코드

    String payload = message.getPayload();
    ChatMessageRequest request = objectMapper.readValue(payload, ChatMessageRequest.class);

    // 메시지 저장 및 DB 처리
    ChatMessageResponse response = chatService.sendMessage(sessionId, user.getId(), request);

    redisPublisher.publish(response);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    Integer sessionId = (Integer) session.getAttributes().get("sessionId");

    if (sessionId != null) {
      log.info("WebSocket 연결 종료: sessionId={}", sessionId);
      Map<String, WebSocketSession> sessions = chatRooms.get(sessionId);
      if (sessions != null) {
        sessions.remove(session.getId());
        if (sessions.isEmpty()) {
          chatRooms.remove(sessionId);
        }
      }
    }
  }

  private void broadcastMessage(Integer sessionId, ChatMessageResponse message) {
    Map<String, WebSocketSession> sessions = chatRooms.get(sessionId);
    if (sessions == null) return;

    String messageJson;
    try {
      messageJson = objectMapper.writeValueAsString(message);
    } catch (Exception e) {
      log.error("메시지 직렬화 실패", e);
      return;
    }

    sessions.values().forEach(session -> {
      try {
        if (session.isOpen()) {
          session.sendMessage(new TextMessage(messageJson));
        }
      } catch (IOException e) {
        log.error("메시지 전송 실패: sessionId={}", session.getId(), e);
      }
    });
  }

  private User getUserFromSession(WebSocketSession session) {
    // Interceptor에서 넣어준 Principal 추출
    Principal principal = (Principal) session.getAttributes().get("userPrincipal");

    if (principal instanceof UsernamePasswordAuthenticationToken) {
      CustomUserDetails userDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
      // DB에서 최신 유저 정보 조회 (for 영속성 컨텍스트 관리)
      return userRepository.findById(userDetails.getUserId()).orElse(null);
    }

    return null;
  }
}