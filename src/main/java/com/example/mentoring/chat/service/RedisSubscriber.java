package com.example.mentoring.chat.service;

import com.example.mentoring.chat.dto.ChatMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

  private final ObjectMapper objectMapper;

  public void sendMessage(String publishMessage) {
    try {
      // 레디스에서 온 JSON 문자열을 DTO로 변환
      ChatMessageResponse message = objectMapper.readValue(publishMessage, ChatMessageResponse.class);

      // 현재 서버에 접속해 있는 WebSocket 세션들에게 전송
      log.info("Redis Sub message: {}", message.getContent());

    } catch (Exception e) {
      log.error("메시지 수신 실패", e);
    }
  }
}