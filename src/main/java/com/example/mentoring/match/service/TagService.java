package com.example.mentoring.match.service;

import com.example.mentoring.match.dto.CreateTagRequest;
import com.example.mentoring.match.dto.TagResponse;
import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.entity.TagType;
import com.example.mentoring.match.repository.PostTagRepository;
import com.example.mentoring.match.repository.TagRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;

  @Transactional
  public TagResponse createTag(CreateTagRequest request) {
    // 이미 존재하는 태그인지 확인 후 중복일 시 재사용
    Optional<Tag> existingTag = tagRepository.findByNameAndType(request.getName(), request.getType());

    if (existingTag.isPresent()) {
      return TagResponse.from(existingTag.get());
    }

    Tag tag = Tag.builder()
        .name(request.getName())
        .type(request.getType())
        .build();

    Tag savedTag = tagRepository.save(tag);
    return TagResponse.from(savedTag);
  }


  @Transactional
  public void deleteTag(Integer tagId) {
    // 태그는 해당 태그를 사용한 게시글이 하나라도 있으면 삭제 불가능
    Tag tag = tagRepository.findById(tagId)
        .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));

    if (postTagRepository.existsByTag(tag)) {
      throw new IllegalStateException("해당 태그를 사용 중인 게시글이 있어 삭제할 수 없습니다.");
    }

    tagRepository.delete(tag);
  }

  public TagResponse getTag(Integer tagId) {
    Tag tag = tagRepository.findById(tagId)
        .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
    return TagResponse.from(tag);
  }

  public List<TagResponse> getAllTags() {
    return tagRepository.findAll().stream()
        .map(TagResponse::from)
        .collect(Collectors.toList());
  }

  public List<TagResponse> getTagsByType(String typeStr) {
    // URL 파라미터(String)로 넘어온 경우 안전하게 Enum 변환
    TagType type;
    try {
      type = TagType.valueOf(typeStr.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("유효하지 않은 태그 타입입니다: " + typeStr);
    }

    return tagRepository.findByType(type).stream()
        .map(TagResponse::from)
        .collect(Collectors.toList());
  }

}