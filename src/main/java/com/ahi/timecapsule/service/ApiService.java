package com.ahi.timecapsule.service;

import com.ahi.timecapsule.api.AlanAPI;
import com.ahi.timecapsule.api.STTAPI;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Service
public class ApiService {

  private final AlanAPI alanAPI;
  private final STTAPI sttAPI;

  public ApiService(AlanAPI alanAPI, STTAPI sttAPI) {
    this.alanAPI = alanAPI;
    this.sttAPI = sttAPI;
  }

  public SseEmitter createContent(String content) {
    return alanAPI.get(content);
  }

  public void changeSpeechToText() {
    sttAPI.SpeechToText();
  }
}
