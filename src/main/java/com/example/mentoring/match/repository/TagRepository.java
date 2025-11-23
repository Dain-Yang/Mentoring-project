package com.example.mentoring.match.repository;

import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.entity.TagType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
  Optional<Tag> findByNameAndType(String name, TagType type);
  List<Tag> findByType(TagType type);
}
