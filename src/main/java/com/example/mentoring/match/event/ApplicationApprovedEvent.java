package com.example.mentoring.match.event;

import com.example.mentoring.match.entity.Application;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplicationApprovedEvent {
  private final Application application;
}