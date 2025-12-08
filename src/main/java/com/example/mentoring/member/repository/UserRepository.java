package com.example.mentoring.member.repository;

import com.example.mentoring.member.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
  Optional<User> findByNickname(String nickname);
  boolean existsByEmail(String email);
  boolean existsByNickname(String nickname);
  List<User> findByIsActiveTrue();
}