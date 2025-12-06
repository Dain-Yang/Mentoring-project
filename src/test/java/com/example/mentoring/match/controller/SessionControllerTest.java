package com.example.mentoring.match.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.auth.service.CustomUserDetailsService;
import com.example.mentoring.match.dto.SessionResponse;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.service.SessionService;
import com.example.mentoring.member.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SessionService sessionService;

  @MockBean
  private AuthService authService;

  @MockBean
  private com.example.mentoring.global.util.JwtUtil jwtUtil;

  @MockBean
  private CustomUserDetailsService customUserDetailsService;

  @Test
  @DisplayName("멘토링 종료 확인 요청 API 테스트")
  @WithMockUser
  void confirmEndSession() throws Exception {
    // given
    Integer sessionId = 1;
    Integer userId = 100;

    User mockUser = User.builder()
        .id(userId)
        .email("test@test.com")
        .password("password")
        .role(User.Role.MENTOR)
        .isActive(true)
        .build();

    CustomUserDetails userDetails = new CustomUserDetails(mockUser);
    given(authService.getCurrentUser()).willReturn(userDetails);

    // Service가 반환할 응답 Mocking
    SessionResponse mockResponse = SessionResponse.builder()
        .id(sessionId)
        .status(Session.SessionStatus.COMPLETED)
        .build();

    given(sessionService.confirmEndSession(eq(sessionId), eq(userId)))
        .willReturn(mockResponse);

    // when & then
    mockMvc.perform(patch("/api/sessions/{sessionId}/confirm-end", sessionId)
            .with(csrf())) // POST, PATCH, DELETE 등은 CSRF 토큰 필요
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"));
  }
}