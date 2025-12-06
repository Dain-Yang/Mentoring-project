package com.example.mentoring.chat.repository;

import com.example.mentoring.chat.entity.ChatRoom;
import com.example.mentoring.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

  //내가 참여 중인 세션의 채팅방 목록 조회 (생성일 최신순)
  List<ChatRoom> findBySession_MentorOrSession_MenteeOrderByCreatedAtDesc(User mentor, User mentee);
}