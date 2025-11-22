package com.example.mentoring.systemcode.repository;

import com.example.mentoring.systemcode.entity.SystemCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemCodeRepository extends JpaRepository<SystemCode, Integer> {
  List<SystemCode> findByType(String type);
  Optional<SystemCode> findByTypeAndValue(String type, String value);
}