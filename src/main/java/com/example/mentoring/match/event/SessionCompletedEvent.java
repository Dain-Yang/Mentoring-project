package com.example.mentoring.match.event;

import com.example.mentoring.match.entity.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionCompletedEvent {
  private final Session session;
}