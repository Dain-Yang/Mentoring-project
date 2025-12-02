package com.example.mentoring.global.config;

import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.entity.TagType;
import com.example.mentoring.match.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TagDataInitializer implements CommandLineRunner {

  private final TagRepository tagRepository;

  @Override
  public void run(String... args) throws Exception {
    // 미리 정의된 멘토링 방식 리스트
    List<String> fixedMethods = Arrays.asList(
        "코드리뷰", "이력서첨삭", "모의면접", "커리어상담", "커피챗", "프로젝트기획"
    );

    for (String name : fixedMethods) {
      if (tagRepository.findByNameAndType(name, TagType.METHOD).isEmpty()) {
        tagRepository.save(Tag.builder()
            .name(name)
            .type(TagType.METHOD) // 멘토링 방식 타입
            .build());
      }
    }
  }
}