package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.service.ApiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // 모든 도메인에서 접근 허용
public class ApiController {

  private ApiService apiService;

  public ApiController(ApiService apiService) {
    this.apiService = apiService;
  }

  @GetMapping(path = "/alan", produces = "text/event-stream")
  public ResponseEntity<SseEmitter> createContent(@RequestParam String content) {

    System.out.println(content);

    return ResponseEntity.ok(apiService.createContent(content));
  }
}
