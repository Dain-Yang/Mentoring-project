package com.example.mentoring.match.repository;

import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.entity.Session.SessionStatus;
import com.example.mentoring.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

  // Application으로 세션 조회
  Optional<Session> findByApplication(Application application);

  // 내가 멘토이거나 멘티인 세션 목록 조회 (최신순)
  List<Session> findByMentorOrMenteeOrderByStartDateDesc(User mentor, User mentee);

  // 특정 상태이면서 내가 참여 중인 세션 조회 (최신순)
  List<Session> findByMentorAndStatusOrMenteeAndStatusOrderByStartDateDesc(
      User mentor, SessionStatus mentorStatus,
      User mentee, SessionStatus menteeStatus
  );
}