package com.example.mentoring.chat.config;

import jakarta.servlet.http.HttpServletRequest;
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
      String sessionId = pathSegments[pathSegments.length - 1];

      try {
        attributes.put("sessionId", Integer.parseInt(sessionId));
      } catch (NumberFormatException e) {
        return false; // 세션 ID가 숫자가 아니면 연결 거부
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