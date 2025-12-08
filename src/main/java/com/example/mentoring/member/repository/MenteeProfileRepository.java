package com.example.mentoring.member.repository;

import com.example.mentoring.member.entity.MenteeProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenteeProfileRepository extends JpaRepository<MenteeProfile, UUID> {

}