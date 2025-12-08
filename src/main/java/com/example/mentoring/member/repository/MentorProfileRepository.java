package com.example.mentoring.member.repository;

import com.example.mentoring.member.entity.MentorProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, UUID> {
  Optional<MentorProfile> findByUserId(UUID userId);
}