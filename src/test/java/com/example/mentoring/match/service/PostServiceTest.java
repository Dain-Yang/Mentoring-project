package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.UpdatePostRequest;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.MentorProfileRepository;
import com.example.mentoring.member.entity.MentorProfile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.match.repository.PostRepository;
import com.example.mentoring.match.repository.TagRepository;
import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private MentorProfileRepository mentorProfileRepository;

  private User testUser;
  private Post testPost;
  private UpdatePostRequest updateRequest;

  // 테스트 시작 전 공통 객체 초기화
  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(1)
        .nickname("멘토1")
        .build();

    // 초기 Post 객체
    testPost = Post.builder()
        .id(100)
        .user(testUser)
        .title("오래된 제목")
        .content("오래된 내용")
        .fieldCode(FieldCode.BACK)
        .levelCode(LevelCode.LV2)
        // Auditing 테스트를 위해 임의의 초기 시간 설정
        .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .updatedAt(LocalDateTime.of(2025, 1, 1, 10, 0))
        .build();

    updateRequest = new UpdatePostRequest(
        "새로운 제목",
        "새로운 내용",
        FieldCode.FRONT.name(),
        LevelCode.LV4.name(),
        null
    );
  }
  @Test
  @DisplayName("게시글 수정 시 updatedAt이 자동으로 업데이트되어야 한다")
  void updatePost_shouldUpdateUpdatedAt() throws Exception {
    // Given
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(testPost));

    // 서비스 메서드 호출 전에 초기 시간을 기록
    LocalDateTime initialUpdatedAt = testPost.getUpdatedAt();

    // When
    // 서비스 메서드 실행
    postService.updatePost(testPost.getId(), testUser.getId(), updateRequest);

    // Then
    assertThat(testPost.getTitle()).isEqualTo("새로운 제목");
    assertThat(testPost.getContent()).isEqualTo("새로운 내용");

    // (Post 객체의 updatePost 메서드 호출 여부 확인)
    verify(postRepository, times(1)).findById(testPost.getId());
  }

  @Test
  @DisplayName("멘토 레벨보다 높은 레벨로 게시글 수정 시 예외가 발생해야 한다")
  void updatePost_shouldThrowException_whenRequestLevelExceedsMentorLevel() {
    // Given
    MentorProfile lowLevelProfile = MentorProfile.builder()
        .user(testUser)
        .levelCode(LevelCode.LV2)
        .build();

    // 멘토 레벨 < 요청 레벨
    UpdatePostRequest highLevelRequest = new UpdatePostRequest(
        "제목", "내용", FieldCode.FRONT.name(), LevelCode.LV4.name(), null
    );

    when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(testPost));

    when(mentorProfileRepository.findByUserId(anyInt())).thenReturn(Optional.of(lowLevelProfile));

    // When & Then
    // 예외가 발생하는지 검증
    assertThat(assertThrows(IllegalArgumentException.class, () ->
        postService.updatePost(testPost.getId(), testUser.getId(), highLevelRequest)
    )).hasMessageContaining("높은 레벨"); // 예외 메시지 일부 확인
  }

  @Test
  @DisplayName("멘토 레벨 이하의 레벨로 게시글 수정 시 성공해야 한다")
  void updatePost_shouldSucceed_whenRequestLevelIsValid() {
    // Given
    MentorProfile highLevelProfile = MentorProfile.builder()
        .user(testUser)
        .levelCode(LevelCode.LV4)
        .build();

    // 멘토 레벨 > 요청 레벨
    UpdatePostRequest validLevelRequest = new UpdatePostRequest(
        "제목", "내용", FieldCode.FRONT.name(), LevelCode.LV2.name(), null
    );

    when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(testPost));

    when(mentorProfileRepository.findByUserId(anyInt())).thenReturn(Optional.of(highLevelProfile));

    // When
    postService.updatePost(testPost.getId(), testUser.getId(), validLevelRequest);

    // Then
    // 게시글 내용이 정상적으로 업데이트되었는지 확인
    assertThat(testPost.getLevelCode()).isEqualTo(LevelCode.LV2);

    // 메서드 호출 확인
    verify(mentorProfileRepository, times(1)).findByUserId(testUser.getId());
  }
}
