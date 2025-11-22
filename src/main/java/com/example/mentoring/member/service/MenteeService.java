package com.example.mentoring.member.service;

import com.example.mentoring.member.dto.CreateMenteeProfileRequest;
import com.example.mentoring.member.dto.MenteeProfileResponse;
import com.example.mentoring.member.dto.UpdateMenteeProfileRequest;
import com.example.mentoring.member.entity.MenteeProfile;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.MenteeProfileRepository;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.systemcode.entity.SystemCode;
import com.example.mentoring.systemcode.repository.SystemCodeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeService {

  private final UserRepository userRepository;
  private final MenteeProfileRepository menteeProfileRepository;
  private final SystemCodeRepository systemCodeRepository;

  // 멘티 프로필 생성
  @Transactional
  public MenteeProfileResponse createMenteeProfile(Integer userId, CreateMenteeProfileRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));


    if (menteeProfileRepository.existsById(userId)) {
      throw new IllegalArgumentException("이미 멘티 프로필이 존재합니다");
    }

    SystemCode fieldCode = systemCodeRepository.findById(request.getFieldCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 코드입니다"));
    SystemCode levelCode = systemCodeRepository.findById(request.getLevelCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨 코드입니다"));

    // 멘티 프로필 생성
    MenteeProfile menteeProfile = MenteeProfile.builder()
        .user(user)
        .menteeBio(request.getMenteeBio())
        .fieldCode(fieldCode)
        .levelCode(levelCode)
        .build();

    MenteeProfile saved = menteeProfileRepository.save(menteeProfile);

    return convertToMenteeProfileResponse(saved);
  }

  // 멘티 프로필 수정
  @Transactional
  public MenteeProfileResponse updateMenteeProfile(Integer userId, UpdateMenteeProfileRequest request) {
    MenteeProfile profile = menteeProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));

    SystemCode fieldCode = request.getFieldCodeId() != null
        ? systemCodeRepository.findById(request.getFieldCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 코드입니다"))
        : profile.getFieldCode();

    SystemCode levelCode = request.getLevelCodeId() != null
        ? systemCodeRepository.findById(request.getLevelCodeId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨 코드입니다"))
        : profile.getLevelCode();

    profile.updateProfile(
        request.getMenteeBio() != null ? request.getMenteeBio() : profile.getMenteeBio(),
        fieldCode,
        levelCode
    );

    return convertToMenteeProfileResponse(profile);
  }

  // 멘티 프로필 조회
  public MenteeProfileResponse getMenteeProfile(Integer userId) {
    MenteeProfile profile = menteeProfileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));
    return convertToMenteeProfileResponse(profile);
  }

  private MenteeProfileResponse convertToMenteeProfileResponse(MenteeProfile profile) {
    return MenteeProfileResponse.builder()
        .userId(profile.getUserId())
        .menteeBio(profile.getMenteeBio())
        .fieldCode(profile.getFieldCode().getDisplayName())
        .levelCode(profile.getLevelCode().getDisplayName())
        .build();
  }
}