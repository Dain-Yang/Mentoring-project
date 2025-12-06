package com.example.mentoring.chat.service;

import com.example.mentoring.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ChannelTopic channelTopic;

  public void publish(ChatMessageResponse message) {
    redisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }
}