package com.example.mentoring.match.dto;


import com.example.mentoring.match.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
  // 게시글 응답 (상세) DTO

    private Integer id;
    private Integer userId;
    private String nickname; // 작성자 닉네임

    private String fieldCode;
    private String fieldName; // 화면 표시용

    private String levelCode;
    private String levelName; // 화면 표시용

    private String title;
    private String content;
    private Boolean status; // 모집 중 여부

    private List<TagResponse> tags; // 태그 목록 포함

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        List<TagResponse> tagResponses = post.getPostTags().stream()
            .map(pt -> TagResponse.from(pt.getTag())) // TagResponse의 from 재사용
            .collect(Collectors.toList());

        return PostResponse.builder()
            .id(post.getId())
            .userId(post.getUser().getId())
            .nickname(post.getUser().getNickname())
            .fieldCode(post.getFieldCode().name())
            .fieldName(post.getFieldCode().getDisplayName())
            .levelCode(post.getLevelCode().name())
            .levelName(post.getLevelCode().getDisplayName())
            .title(post.getTitle())
            .content(post.getContent())
            .status(post.getStatus())
            .tags(tagResponses)
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();
    }
  }
