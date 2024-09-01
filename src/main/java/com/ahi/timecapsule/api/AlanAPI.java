package com.ahi.timecapsule.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Component
@EnableAsync
public class AlanAPI {

  private final WebClient webClient;

  private final String ALAN_API_URL = "https://kdt-api-function.azurewebsites.net";

  private final String END_POINT = "/api/v1/question/sse-streaming";

  private final String CLIENT_ID = "65cef0e5-ce7a-4655-a5a8-5f6414f55d03";

  public AlanAPI() {
    this.webClient = WebClient.builder().baseUrl(ALAN_API_URL).build();
  }

  // ExecutorService 생성: 고정된 스레드 풀 사용
  private final ExecutorService executorService = Executors.newFixedThreadPool(10);

  public SseEmitter get(String content) {
    SseEmitter emitter = new SseEmitter();

    // 외부 API로 GET 요청을 보냄
    Flux<String> responseFlux =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(END_POINT)
                        .queryParam("content", content)
                        .queryParam("client_id", CLIENT_ID)
                        .build())
            .retrieve()
            .bodyToFlux(String.class);

    // Flux 데이터 구독
    responseFlux.subscribe(
        data -> {
          // 데이터를 받았을 때마다 비동기 작업으로 emitter.send 호출
          executorService.submit(() -> sendData(emitter, data));
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        },
        error -> {
          executorService.submit(() -> emitter.completeWithError(error)); // 에러 발생 시 처리
        },
        () -> {
          executorService.submit(emitter::complete); // 데이터 전송이 완료되면 완료 신호 전송
        });

    return emitter;
  }

  // 데이터를 클라이언트로 전송하는 메소드
  private void sendData(SseEmitter emitter, String data) {
    try {
      emitter.send(data); // JSON 데이터를 클라이언트로 전송
    } catch (Exception e) {
      emitter.completeWithError(e); // 에러 발생 시 처리
    }
  }
}
