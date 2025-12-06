package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.SessionResponse;
import com.example.mentoring.match.dto.SessionSummaryResponse;
import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.event.ApplicationApprovedEvent;
import com.example.mentoring.match.event.SessionCompletedEvent;
import com.example.mentoring.match.event.SessionCreatedEvent;
import com.example.mentoring.match.repository.SessionRepository;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  // 신청 승인 이벤트를 받아서 세션 실행
  @EventListener
  @Transactional
  public void handleApplicationApproved(ApplicationApprovedEvent event) {
    createSession(event.getApplication());
  }

  @Transactional
  public Session createSession(Application application) {
    if (sessionRepository.findByApplication(application).isPresent()) {
      // 이벤트 중복 수신 등의 케이스 방어
      return sessionRepository.findByApplication(application).get();
    }

    Session session = Session.builder()
        .application(application)
        .mentor(application.getPost().getUser())
        .mentee(application.getUser())
        .build();

    Session savedSession = sessionRepository.save(session);

    // 이벤트 발행 -> ChatService로 토스
    eventPublisher.publishEvent(new SessionCreatedEvent(savedSession));

    return savedSession;
  }

  public SessionResponse getSession(Integer sessionId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    Session session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

    if (!session.isParticipant(user)) {
      throw new IllegalStateException("세션 참여자만 조회할 수 있습니다.");
    }

    return SessionResponse.from(session);
  }

  public List<SessionSummaryResponse> getMySessions(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    return sessionRepository.findByMentorOrMenteeOrderByStartDateDesc(user, user).stream()
        .map(session -> SessionSummaryResponse.of(session, user))
        .collect(Collectors.toList());
  }

  public List<SessionSummaryResponse> getMySessionsByStatus(Integer userId, Session.SessionStatus status) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    return sessionRepository.findByMentorAndStatusOrMenteeAndStatusOrderByStartDateDesc(
            user, status, user, status
        ).stream()
        .map(session -> SessionSummaryResponse.of(session, user))
        .collect(Collectors.toList());
  }

  @Transactional
  public SessionResponse requestEndSession(Integer sessionId, Integer userId) {
    User requester = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    Session session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

    session.requestEnd(requester);
    return SessionResponse.from(session);
  }

  @Transactional
  public SessionResponse confirmEndSession(Integer sessionId, Integer userId) {
    User confirmer = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

    Session session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));

    session.confirmEnd(confirmer);

    // 세션 종료 시 채팅창 잠금
    if (session.isCompleted()) {
      eventPublisher.publishEvent(new SessionCompletedEvent(session));
    }

    return SessionResponse.from(session);
  }
}