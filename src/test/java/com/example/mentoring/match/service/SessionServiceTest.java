package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.SessionResponse;
import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.event.SessionCompletedEvent;
import com.example.mentoring.match.repository.SessionRepository;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @InjectMocks
  private SessionService sessionService;

  @Mock
  private SessionRepository sessionRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Test
  @DisplayName("멘토링 종료 최종 확인 및 완료 이벤트 발행 테스트")
  void confirmEndSession_Complete() {
    // given
    Integer sessionId = 1;
    Integer menteeId = 20;

    User mentor = User.builder().id(10).nickname("Mentor").build();
    User mentee = User.builder().id(menteeId).nickname("Mentee").build();

    Application application = Application.builder().id(50).build();

    // 이미 종료 요청 상태이고 멘토는 이미 확인을 누른 상태
    Session session = Session.builder()
        .id(sessionId)
        .mentor(mentor)
        .mentee(mentee)
        .status(Session.SessionStatus.END_REQUESTED)
        .mentorConfirm(true)  // 멘토는 이미 확인 함
        .menteeConfirm(false) // 멘티가 아직 안 함
        .application(application)
        .build();

    given(userRepository.findById(menteeId)).willReturn(Optional.of(mentee));
    given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

    // when
    // 멘티가 종료 확인 요청
    SessionResponse response = sessionService.confirmEndSession(sessionId, menteeId);

    // then
    // 상태가 COMPLETED로 변경되었는지 확인
    assertThat(response.getStatus()).isEqualTo(Session.SessionStatus.COMPLETED);
    assertThat(response.getMenteeConfirm()).isTrue();

    // 세션 종료 이벤트 발행 확인
    verify(eventPublisher, times(1)).publishEvent(any(SessionCompletedEvent.class));
  }
}