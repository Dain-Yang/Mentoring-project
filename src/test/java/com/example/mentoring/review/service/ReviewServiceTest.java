package com.example.mentoring.review.service;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.repository.SessionRepository;
import com.example.mentoring.member.entity.MentorProfile;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.review.dto.CreateReviewRequest;
import com.example.mentoring.review.dto.ReviewResponse;
import com.example.mentoring.review.dto.UpdateReviewRequest;
import com.example.mentoring.review.dto.UserRatingResponse;
import com.example.mentoring.review.entity.Review;
import com.example.mentoring.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID; // UUID import 추가

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private SessionRepository sessionRepository;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("리뷰 생성 성공: 멘티가 멘토에게 리뷰를 작성하고 평점이 갱신된다")
  void createReview_success() {
    // given
    User mentor = createMentor(1);
    User mentee = createMentee(2);
    // UUID 생성
    UUID sessionId = UUID.randomUUID();
    Session session = createCompletedSession(sessionId, mentor, mentee);

    CreateReviewRequest request = new CreateReviewRequest(5, "훌륭한 멘토링이었습니다!");

    // Mocking
    given(userRepository.findById(2)).willReturn(Optional.of(mentee));
    // UUID로 조회
    given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));
    given(reviewRepository.existsBySessionAndReviewer(session, mentee)).willReturn(false);
    given(reviewRepository.calculateAverageRatingByTarget(mentor)).willReturn(4.5);
    given(reviewRepository.countByTarget(mentor)).willReturn(10L);

    Review savedReview = Review.create(session, mentee, mentor, 5, "content");
    ReflectionTestUtils.setField(savedReview, "id", 1);
    given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

    // when
    // service 호출 시 UUID 전달
    ReviewResponse response = reviewService.createReview(sessionId, 2, request);

    // then
    assertThat(response.getReviewerId()).isEqualTo(mentee.getId());
    assertThat(response.getTargetId()).isEqualTo(mentor.getId());

    assertThat(mentor.getMentorProfile().getAvgRating()).isEqualTo(BigDecimal.valueOf(4.5));
    assertThat(mentor.getMentorProfile().getReviewCount()).isEqualTo(10L);

    verify(reviewRepository, times(1)).save(any(Review.class));
  }

  @Test
  @DisplayName("리뷰 생성 실패: 완료되지 않은 세션에는 리뷰를 작성할 수 없다")
  void createReview_fail_sessionNotCompleted() {
    // given
    User mentor = createMentor(1);
    User mentee = createMentee(2);
    UUID sessionId = UUID.randomUUID();

    // 진행 중인 세션 생성
    Session session = Session.builder()
        .id(sessionId)
        .mentor(mentor)
        .mentee(mentee)
        .status(Session.SessionStatus.PROGRESS) // 진행 중
        .build();

    given(userRepository.findById(2)).willReturn(Optional.of(mentee));
    given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

    CreateReviewRequest request = new CreateReviewRequest(5, "내용");

    // when & then
    assertThatThrownBy(() -> reviewService.createReview(sessionId, 2, request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("완료된 멘토링 세션에 대해서만 리뷰를 작성할 수 있습니다.");
  }

  @Test
  @DisplayName("리뷰 생성 실패: 이미 리뷰를 작성한 경우 중복 작성 불가")
  void createReview_fail_duplicate() {
    // given
    User mentor = createMentor(1);
    User mentee = createMentee(2);
    UUID sessionId = UUID.randomUUID();
    Session session = createCompletedSession(sessionId, mentor, mentee);

    given(userRepository.findById(2)).willReturn(Optional.of(mentee));
    given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));
    given(reviewRepository.existsBySessionAndReviewer(session, mentee)).willReturn(true);

    CreateReviewRequest request = new CreateReviewRequest(5, "내용");

    // when & then
    assertThatThrownBy(() -> reviewService.createReview(sessionId, 2, request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("이미 리뷰를 작성하였습니다.");
  }

  @Test
  @DisplayName("리뷰 수정 성공: 내용과 평점을 수정하고 멘토 통계를 갱신한다")
  void updateReview_success() {
    // given
    User mentor = createMentor(1);
    User mentee = createMentee(2);
    UUID sessionId = UUID.randomUUID();
    Session session = createCompletedSession(sessionId, mentor, mentee);

    Review review = Review.create(session, mentee, mentor, 3, "이전 내용");
    ReflectionTestUtils.setField(review, "id", 1);

    UpdateReviewRequest request = new UpdateReviewRequest(5, "수정된 내용");

    given(reviewRepository.findById(1)).willReturn(Optional.of(review));
    given(reviewRepository.calculateAverageRatingByTarget(mentor)).willReturn(5.0);
    given(reviewRepository.countByTarget(mentor)).willReturn(1L);

    // when
    ReviewResponse response = reviewService.updateReview(1, 2, request);

    // then
    assertThat(response.getContent()).isEqualTo("수정된 내용");
    assertThat(response.getRating()).isEqualTo(5);
  }

  @Test
  @DisplayName("리뷰 수정 실패: 본인이 작성하지 않은 리뷰는 수정할 수 없다")
  void updateReview_fail_notOwner() {
    // given
    User mentor = createMentor(1);
    User mentee = createMentee(2);
    User otherUser = createMentee(3);

    UUID sessionId = UUID.randomUUID();
    Session session = createCompletedSession(sessionId, mentor, mentee);
    Review review = Review.create(session, mentee, mentor, 3, "내용");

    given(reviewRepository.findById(1)).willReturn(Optional.of(review));

    UpdateReviewRequest request = new UpdateReviewRequest(4, "수정");

    // when & then
    assertThatThrownBy(() -> reviewService.updateReview(1, 3, request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("본인이 작성한 리뷰만 수정할 수 있습니다.");
  }

  @Test
  @DisplayName("사용자 평점 조회: 평점이 없는 경우 0.0을 반환한다")
  void getUserRating_nullSafety() {
    // 기존 코드 동일 (User ID는 Integer 유지)
    User user = createMentor(1);
    given(userRepository.findById(1)).willReturn(Optional.of(user));

    given(reviewRepository.calculateAverageRatingByTarget(user)).willReturn(0.0);
    given(reviewRepository.countByTarget(user)).willReturn(0L);

    UserRatingResponse response = reviewService.getUserRating(1);

    assertThat(response.getAverageRating()).isEqualTo(0.0);
    assertThat(response.getReviewCount()).isEqualTo(0L);
  }

  // 테스트 데이터
  private User createMentor(Integer id) {
    // ... 기존 코드 동일
    User user = User.builder()
        .id(id)
        .email("mentor" + id + "@test.com")
        .nickname("멘토" + id)
        .role(User.Role.MENTOR)
        .build();
    MentorProfile profile = MentorProfile.builder()
        .user(user)
        .fieldCode(FieldCode.BACK)
        .levelCode(LevelCode.LV3)
        .avgRating(BigDecimal.ZERO)
        .reviewCount(0L)
        .build();
    ReflectionTestUtils.setField(user, "mentorProfile", profile);
    return user;
  }

  private User createMentee(Integer id) {
    // ... 기존 코드 동일
    return User.builder()
        .id(id)
        .email("mentee" + id + "@test.com")
        .nickname("멘티" + id)
        .role(User.Role.MENTEE)
        .build();
  }

  // Integer id -> UUID id 변경
  private Session createCompletedSession(UUID id, User mentor, User mentee) {
    return Session.builder()
        .id(id)
        .mentor(mentor)
        .mentee(mentee)
        .status(Session.SessionStatus.COMPLETED)
        .startDate(LocalDateTime.now().minusDays(1))
        .endDate(LocalDateTime.now())
        .build();
  }
}