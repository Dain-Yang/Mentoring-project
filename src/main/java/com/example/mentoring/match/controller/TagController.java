package com.example.mentoring.match.controller;

import com.example.mentoring.match.dto.CreateTagRequest;
import com.example.mentoring.match.dto.TagResponse;
import com.example.mentoring.match.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  // 태그 생성
  @PostMapping
  public ResponseEntity<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
    TagResponse response = tagService.createTag(request);
    return ResponseEntity.ok(response);
  }

  // 태그 삭제
  @DeleteMapping("/{tagId}")
  public ResponseEntity<Void> deleteTag(@PathVariable Integer tagId) {
    tagService.deleteTag(tagId);
    return ResponseEntity.noContent().build();
  }

  // 태그 상세 조회
  @GetMapping("/{tagId}")
  public ResponseEntity<TagResponse> getTag(@PathVariable Integer tagId) {
    TagResponse response = tagService.getTag(tagId);
    return ResponseEntity.ok(response);
  }

  // 모든 태그 조회
  @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
    List<TagResponse> responses = tagService.getAllTags();
    return ResponseEntity.ok(responses);
  }

  // 타입별 태그 조회
  @GetMapping("/type/{type}")
  public ResponseEntity<List<TagResponse>> getTagsByType(@PathVariable String type) {
    List<TagResponse> responses = tagService.getTagsByType(type);
    return ResponseEntity.ok(responses);
  }
}