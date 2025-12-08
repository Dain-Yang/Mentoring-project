package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {
  // 게시글 목록 응답 (간략) DTO

  private Integer id;
  private String nickname;
  private String fieldName; // 목록에서는 한글명만 보여주기
  private String levelName;
  private String title;
  private Boolean status;
  private Integer applicationCount; // 신청 수 - DB에서 count 쿼리로 가져오기
  private LocalDateTime createdAt;

  public static PostSummaryResponse from(Post post) {
    return PostSummaryResponse.builder()
        .id(post.getId())
        .nickname(post.getUser().getNickname())
        .fieldName(post.getFieldCode().getDisplayName())
        .levelName(post.getLevelCode().getDisplayName())
        .title(post.getTitle())
        .status(post.isRecruiting())
        .applicationCount(post.getApplications().size())
        .createdAt(post.getCreatedAt())
        .build();
  }
}
