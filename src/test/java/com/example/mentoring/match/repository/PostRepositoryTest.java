package com.example.mentoring.match.repository;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.global.config.QueryDslConfig;
import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSearchCondition;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.match.entity.PostTag;
import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.entity.TagType;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class) // QueryDSL 설정 가져옴
class PostRepositoryTest {

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TagRepository tagRepository;

  @Autowired
  EntityManager em;

  User mentor;
  Tag springTag;
  Tag javaTag;

  @BeforeEach
  void setUp() {
    // 테스트에 공통으로 필요한 데이터 세팅
    mentor = User.builder()
        .email("mentor@test.com")
        .password("password")
        .nickname("멘토킴")
        .role(User.Role.MENTOR)
        .build();
    userRepository.save(mentor);

    springTag = tagRepository.save(Tag.create("Spring", TagType.STACK));
    javaTag = tagRepository.save(Tag.create("Java", TagType.STACK));
  }

  @Test
  @DisplayName("검색 조건이 없으면 모집 중인 모든 글을 조회한다")
  void search_noCondition() {
    // given
    createPost("제목1", "내용1", List.of(springTag));
    createPost("제목2", "내용2", List.of(javaTag));

    PostSearchCondition condition = new PostSearchCondition(); // 조건 없음
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<PostResponse> result = postRepository.search(condition, pageable);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  @DisplayName("키워드 검색: 제목 또는 내용에 키워드가 포함된 글을 찾는다")
  void search_keyword() {
    // given
    createPost("스프링 부트 강의", "기초부터 심화까지", List.of(springTag));
    createPost("자바의 정석", "언어 기초", List.of(javaTag));

    PostSearchCondition condition = new PostSearchCondition();
    condition.setKeyword("스프링"); // "스프링" 검색

    // when
    Page<PostResponse> result = postRepository.search(condition, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getTitle()).contains("스프링");
  }

  @Test
  @DisplayName("태그 검색: 특정 태그를 가진 글만 필터링한다")
  void search_tag() {
    // given
    createPost("글1", "내용1", List.of(springTag));
    createPost("글2", "내용2", List.of(javaTag));
    createPost("글3", "내용3", List.of(springTag, javaTag));

    PostSearchCondition condition = new PostSearchCondition();
    condition.setTagIds(List.of(springTag.getId())); // Spring 태그 선택

    // when
    Page<PostResponse> result = postRepository.search(condition, PageRequest.of(0, 10));

    // then
    // 글1, 글3이 나와야 함
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting("title")
        .containsExactlyInAnyOrder("글1", "글3");
  }

  @Test
  @DisplayName("복합 검색: 키워드와 태그 조건을 모두 만족하는 글을 찾는다")
  void search_complex() {
    // given
    createPost("스프링 백엔드", "모집합니다", List.of(springTag)); // O (스프링 + Spring태그)
    createPost("스프링 프론트", "모집합니다", List.of(javaTag));   // X (태그 불일치)
    createPost("자바 백엔드", "모집합니다", List.of(springTag));   // X (키워드 불일치)

    PostSearchCondition condition = new PostSearchCondition();
    condition.setKeyword("스프링");
    condition.setTagIds(List.of(springTag.getId()));

    // when
    Page<PostResponse> result = postRepository.search(condition, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("스프링 백엔드");
  }

  @Test
  @DisplayName("모집 마감된 글은 조회되지 않아야 한다")
  void search_excludeClosed() {
    // given
    Post post = createPost("마감된 글", "내용", List.of(springTag));
    post.closeRecruitment(); // 모집 마감 처리
    postRepository.save(post);

    createPost("모집중인 글", "내용", List.of(springTag));

    PostSearchCondition condition = new PostSearchCondition();

    // when
    Page<PostResponse> result = postRepository.search(condition, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("모집중인 글");
  }

  private Post createPost(String title, String content, List<Tag> tags) {
    Post post = Post.builder()
        .user(mentor)
        .title(title)
        .content(content)
        .fieldCode(FieldCode.BACK)
        .levelCode(LevelCode.LV0)
        .isRecruiting(true)
        .build();

    if (tags != null) {
      for (Tag tag : tags) {
        post.addPostTag(PostTag.builder().tag(tag).post(post).build());
      }
    }

    return postRepository.save(post);
  }
}