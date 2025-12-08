package com.example.mentoring.chat.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
      HttpServletRequest httpRequest = servletRequest.getServletRequest();

      // URL에서 PathVariable (sessionId) 추출
      String path = httpRequest.getRequestURI();
      String[] pathSegments = path.split("/");
      String sessionIdString = pathSegments[pathSegments.length - 1];

      try {
        UUID sessionId = UUID.fromString(sessionIdString);
        attributes.put("sessionId", sessionId);
      } catch (IllegalArgumentException e) { // UUID 형식이 잘못되었을 때 발생하는 예외
        // 세션 ID 형식이 유효하지 않으면 연결 거부
        return false;
      }

      // Spring Security의 인증 정보를 WebSocket 세션으로 전달
      if (request.getPrincipal() != null) {
        attributes.put("userPrincipal", request.getPrincipal());
      } else {
        // 인증되지 않은 사용자는 연결 거부
        return false;
      }
    }
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
    // 핸드쉐이크 후 처리 (비워둠)
  }
}