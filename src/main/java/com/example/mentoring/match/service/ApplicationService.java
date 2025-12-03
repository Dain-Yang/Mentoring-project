package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.ApplicationResponse;
import com.example.mentoring.match.dto.ApplicationSummaryResponse;
import com.example.mentoring.match.dto.CreateApplicationRequest;
import com.example.mentoring.match.dto.UpdateApplicationRequest;
import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.match.repository.ApplicationRepository;
import com.example.mentoring.match.repository.PostRepository;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository; // User 조회를 위해 추가

  @Transactional
  public ApplicationResponse createApplication(Integer postId, Integer userId, CreateApplicationRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.isRecruiting()) {
      throw new IllegalStateException("모집이 마감된 게시글입니다.");
    }

    if (applicationRepository.existsByPostAndUser(post, user)) {
      throw new IllegalStateException("이미 신청한 게시글입니다.");
    }

    if (post.getUser().getId().equals(userId)) {
      throw new IllegalStateException("본인의 게시글에는 신청할 수 없습니다.");
    }

    Application application = Application.builder()
        .post(post)
        .user(user)
        .content(request.getContent())
        .build();

    Application savedApplication = applicationRepository.save(application);
    return ApplicationResponse.from(savedApplication);
  }

  @Transactional
  public ApplicationResponse updateApplication(Integer applicationId, Integer userId, UpdateApplicationRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));

    if (!application.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 제출한 신청서만 수정할 수 있습니다.");
    }

    application.updateContent(request.getContent());
    return ApplicationResponse.from(application);
  }

  @Transactional
  public void deleteApplication(Integer applicationId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));

    if (!application.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 제출한 신청서만 삭제할 수 있습니다.");
    }

    applicationRepository.delete(application);
  }

  public ApplicationResponse getApplication(Integer applicationId) {
    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));
    return ApplicationResponse.from(application);
  }

  // 특정 모집글에 달린 신청서 목록 조회 (작성자=멘토만 가능)
  public List<ApplicationResponse> getApplicationsByPost(Integer postId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인의 게시글에 대한 신청서만 조회할 수 있습니다.");
    }

    // Repository에서 Fetch Join으로 조회한다고 가정 (N+1 방지)
    return applicationRepository.findByPostOrderByAppliedAtDesc(post).stream()
        .map(ApplicationResponse::from)
        .collect(Collectors.toList());
  }

  // 내가 쓴 신청서 목록 조회 (마이페이지)
  public List<ApplicationSummaryResponse> getMyApplications(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    return applicationRepository.findByUserOrderByAppliedAtDesc(user).stream()
        .map(ApplicationSummaryResponse::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public ApplicationResponse approveApplication(Integer applicationId, Integer userId) {
    User mentor = userRepository.findById(userId) // userId는 곧 승인을 시도하는 멘토의 ID
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));

    if (!application.getPost().getUser().getId().equals(mentor.getId())) {
      throw new IllegalStateException("본인의 게시글에 대한 신청서만 승인할 수 있습니다.");
    }

    application.approve();
    return ApplicationResponse.from(application);
  }

  @Transactional
  public ApplicationResponse rejectApplication(Integer applicationId, Integer userId) {
    User mentor = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));

    if (!application.getPost().getUser().getId().equals(mentor.getId())) {
      throw new IllegalStateException("본인의 게시글에 대한 신청서만 거절할 수 있습니다.");
    }

    application.reject();
    return ApplicationResponse.from(application);
  }

}