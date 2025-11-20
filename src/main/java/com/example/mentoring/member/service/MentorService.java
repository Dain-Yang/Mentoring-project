package com.example.mentoring.member.service;

import com.example.mentoring.member.dto.CreateMentorProfileRequest;
import com.example.mentoring.member.dto.MentorProfileResponse;
import com.example.mentoring.member.dto.UpdateMentorProfileRequest;
import com.example.mentoring.member.entity.MentorProfile;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.MentorProfileRepository;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.systemcode.entity.SystemCode;
import com.example.mentoring.systemcode.repository.SystemCodeRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

  private final UserRepository userRepository;
  private final MentorProfileRepository mentorProfileRepository;
  private final SystemCodeRepository systemCodeRepository;

  // 멘토 프로필 생성
  @Transactional
  public MentorProfileResponse createMentorProfile(Integer userId, CreateMentorProfileRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    // 역할 검증
    if (user.getRole() != User.Role.MENTOR) {
      throw new IllegalArgumentException("멘토 권한이 없습니다");
    }

    if (mentorProfileRepository.existsById(userId)) {
      throw new IllegalArgumentException("이미 멘토 프로필이 존재합니다");
    }

    SystemCode fieldCode = systemCodeRepository.findById(request.getFieldCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 코드입니다"));
    SystemCode levelCode = systemCodeRepository.findById(request.getLevelCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨 코드입니다"));

    // 멘토 프로필 생성
    MentorProfile mentorProfile = MentorProfile.builder()
        .user(user)
        .mentorBio(request.getMentorBio())
        .careerYears(request.getCareerYears())
        .company(request.getCompany())
        .fieldCode(fieldCode)
        .levelCode(levelCode)
        .avgRating(BigDecimal.ZERO)
        .build();

    MentorProfile saved = mentorProfileRepository.save(mentorProfile);

    return convertToMentorProfileResponse(saved);
  }

  // 멘토 프로필 수정
  @Transactional
  public MentorProfileResponse updateMentorProfile(Integer userId, UpdateMentorProfileRequest request) {
    MentorProfile profile = mentorProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘토 프로필이 존재하지 않습니다"));

    SystemCode fieldCode = request.getFieldCodeId() != null
        ? systemCodeRepository.findById(request.getFieldCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 코드입니다"))
        : profile.getFieldCode();

    SystemCode levelCode = request.getLevelCodeId() != null
        ? systemCodeRepository.findById(request.getLevelCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨 코드입니다"))
        : profile.getLevelCode();

    profile.updateProfile(
        request.getMentorBio() != null ? request.getMentorBio() : profile.getMentorBio(),
        request.getCareerYears() != null ? request.getCareerYears() : profile.getCareerYears(),
        request.getCompany() != null ? request.getCompany() : profile.getCompany(),
        fieldCode,
        levelCode
    );

    return convertToMentorProfileResponse(profile);
  }

  // 멘토 프로필 조회
  public MentorProfileResponse getMentorProfile(Integer userId) {
    MentorProfile profile = mentorProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘토 프로필이 존재하지 않습니다"));
    return convertToMentorProfileResponse(profile);
  }

  private MentorProfileResponse convertToMentorProfileResponse(MentorProfile profile) {
    return MentorProfileResponse.builder()
        .userId(profile.getUserId())
        .mentorBio(profile.getMentorBio())
        .avgRating(Double.valueOf(profile.getAvgRating().toString()))
        .careerYears(profile.getCareerYears())
        .company(profile.getCompany())
        .fieldCode(profile.getFieldCode().getDisplayName())
        .levelCode(profile.getLevelCode().getDisplayName())
        .build();
  }
}