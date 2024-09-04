package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.service.ApiService;
import java.io.IOException;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // 모든 도메인에서 접근 허용
public class ApiController {

  private ApiService apiService;

  public ApiController(ApiService apiService) {
    this.apiService = apiService;
  }

  @GetMapping(path = "/story", produces = "text/event-stream")
  public ResponseEntity<SseEmitter> createStory() throws IOException, ParseException, InterruptedException {
    apiService.post();
//    Thread.sleep(10000);
    List<String> contents = apiService.get();

    System.out.println(contents.get(0));

    return ResponseEntity.ok(apiService.createContent(contents));
  }

  @GetMapping("/stt/auth")
  public ResponseEntity<String> test() throws Exception {
    apiService.auth();

    return ResponseEntity.ok("확인되었어요!");
  }

  @GetMapping("/stt/post")
  public ResponseEntity<String> post() throws ParseException {
    try {
      apiService.post();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.ok("확인되었어요!");
  }

  @GetMapping("stt/get")
  public ResponseEntity<String> get() throws IOException, ParseException, InterruptedException {
    apiService.get();

    return ResponseEntity.ok("확인되었어요!");
  }
}
