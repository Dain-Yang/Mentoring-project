package com.example.mentoring.member.service;

import com.example.mentoring.member.dto.SignUpRequest;
import com.example.mentoring.member.dto.UpdateUserRequest;
import com.example.mentoring.member.dto.UserResponse;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // 회원가입
  @Transactional
  public UserResponse signUp(SignUpRequest request) {
    // 중복 체크
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다");
    }
    if (userRepository.existsByNickname(request.getNickname())) {
      throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
    }

    // 사용자 생성
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .role(request.getRole())
        .isActive(true)
        .build();

    User savedUser = userRepository.save(user);

    return convertToUserResponse(savedUser);
  }

  // 사용자 정보 수정
  @Transactional
  public UserResponse updateUser(Integer userId, UpdateUserRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

    if (request.getNickname() != null) {
      if (userRepository.existsByNickname(request.getNickname())
          && !user.getNickname().equals(request.getNickname())) {
        throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
      }
      user.updateNickname(request.getNickname());
    }

    if (request.getPassword() != null) {
      user.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    return convertToUserResponse(user);
  }

  // 사용자 정보 조회
  public UserResponse getUserById(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
    return convertToUserResponse(user);
  }

  private UserResponse convertToUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole().name())
        .isActive(user.getIsActive())
        .createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        .build();
  }
}