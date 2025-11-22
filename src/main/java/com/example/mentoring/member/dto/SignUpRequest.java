package com.example.mentoring.member.dto;

import com.example.mentoring.member.entity.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
  // 회원가입 요청 DTO

  @NotBlank(message = "이메일은 필수입니다")
  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다")
  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
  private String password;

  @NotNull(message = "닉네임은 필수입니다")
  @Size(min = 2, max = 10, message = "닉네임은 2-10자 사이여야 합니다")
  private String nickname;

  @NotNull(message = "역할은 필수입니다")
  private Role role;
}