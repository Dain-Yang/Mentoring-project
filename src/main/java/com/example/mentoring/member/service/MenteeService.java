package com.example.mentoring.member.service;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.member.dto.CreateMenteeProfileRequest;
import com.example.mentoring.member.dto.MenteeProfileResponse;
import com.example.mentoring.member.dto.UpdateMenteeProfileRequest;
import com.example.mentoring.member.entity.MenteeProfile;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.MenteeProfileRepository;
import com.example.mentoring.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeService {

  private final UserRepository userRepository;
  private final MenteeProfileRepository menteeProfileRepository;

  // 멘티 프로필 생성
  @Transactional
  public MenteeProfileResponse createMenteeProfile(Integer userId, CreateMenteeProfileRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    if (menteeProfileRepository.existsById(userId)) {
      throw new IllegalArgumentException("이미 멘티 프로필이 존재합니다");
    }

    FieldCode fieldCode = FieldCode.fromValue(request.getFieldCode());
    LevelCode levelCode = LevelCode.fromValue(request.getLevelCode());

    // 멘티 프로필 생성
    MenteeProfile menteeProfile = MenteeProfile.builder()
        .user(user)
        .menteeBio(request.getMenteeBio())
        .fieldCode(fieldCode)
        .levelCode(levelCode)
        .build();

    MenteeProfile saved = menteeProfileRepository.save(menteeProfile);

    return MenteeProfileResponse.from(saved);
  }

  // 멘티 프로필 수정
  @Transactional
  public MenteeProfileResponse updateMenteeProfile(Integer userId, UpdateMenteeProfileRequest request) {
    MenteeProfile profile = menteeProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));

    // 요청값이 있으면 Enum 변환, 없으면 기존 값 유지
    FieldCode fieldCode = (request.getFieldCode() != null && !request.getFieldCode().isEmpty())
        ? FieldCode.fromValue(request.getFieldCode())
        : profile.getFieldCode();

    LevelCode levelCode = (request.getLevelCode() != null && !request.getLevelCode().isEmpty())
        ? LevelCode.fromValue(request.getLevelCode())
        : profile.getLevelCode();

    profile.updateProfile(
        request.getMenteeBio() != null ? request.getMenteeBio() : profile.getMenteeBio(),
        fieldCode,
        levelCode
    );

    return MenteeProfileResponse.from(profile);
  }

  // 멘티 프로필 조회
  public MenteeProfileResponse getMenteeProfile(Integer userId) {
    MenteeProfile profile = menteeProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));
    return MenteeProfileResponse.from(profile);
  }

}