package com.example.mentoring.member.repository;

import com.example.mentoring.member.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Integer> {

}