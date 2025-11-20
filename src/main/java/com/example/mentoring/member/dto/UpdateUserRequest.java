package com.example.mentoring.member.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
  // 사용자 정보 수정 요청 DTO

  @Size(min = 2, max = 10, message = "닉네임은 2-10자 사이여야 합니다")
  private String nickname;

  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
  private String password;

}
