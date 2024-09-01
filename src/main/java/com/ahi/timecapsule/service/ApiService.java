package com.ahi.timecapsule.service;

import com.ahi.timecapsule.api.AlanAPI;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Service
public class ApiService {

  private final AlanAPI alanAPI;

  public ApiService(AlanAPI alanAPI) {
    this.alanAPI = alanAPI;
  }

  public SseEmitter createContent(String content) {
    return alanAPI.get(content);
  }
}
