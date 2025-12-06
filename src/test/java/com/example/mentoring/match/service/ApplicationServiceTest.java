package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.ApplicationResponse;
import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.match.event.ApplicationApprovedEvent;
import com.example.mentoring.match.repository.ApplicationRepository;
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
class ApplicationServiceTest {

  @InjectMocks
  private ApplicationService applicationService;

  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ApplicationEventPublisher eventPublisher; // 이벤트 발행기 Mock

  @Test
  @DisplayName("멘토링 신청 승인 성공 및 이벤트 발행 테스트")
  void approveApplication_Success() {
    // given
    Integer applicationId = 1;
    Integer mentorId = 10;
    Integer menteeId = 20;

    User mentor = User.builder().id(mentorId).nickname("Mentor").build();
    User mentee = User.builder().id(menteeId).nickname("Mentee").build();
    Post post = Post.builder().id(100).user(mentor).build(); // 게시글 작성자 = 멘토

    Application application = Application.builder()
        .id(applicationId)
        .post(post)
        .user(mentee)
        .status(Application.ApplicationStatus.PENDING) // 초기 상태 대기
        .build();

    given(userRepository.findById(mentorId)).willReturn(Optional.of(mentor));
    given(applicationRepository.findById(applicationId)).willReturn(Optional.of(application));

    // when
    ApplicationResponse response = applicationService.approveApplication(applicationId, mentorId);

    // then
    // 1. 상태 변경 확인
    assertThat(response.getStatus()).isEqualTo(Application.ApplicationStatus.APPROVED);

    // 2. 이벤트 발행 확인 (이벤트를 1번 발행했는지)
    verify(eventPublisher, times(1)).publishEvent(any(ApplicationApprovedEvent.class));
  }
}