package com.ahi.timecapsule.api;

import com.ahi.timecapsule.dto.request.StoryOptionDTO;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Component
public class AlanAPI {

  private WebClient webClient;

  @Value("${Alan.URL}")
  private String ALAN_API_URL;

  @Value("${Alan.END-POINT}")
  private String END_POINT;

  @Value("${Alan.API-KEY}")
  private String CLIENT_ID;

  public AlanAPI() {}

  // ExecutorService 생성: 고정된 스레드 풀 사용
  private final ExecutorService executorService = Executors.newFixedThreadPool(2);

  public SseEmitter get(List<String> contents, StoryOptionDTO storyOptionDTO) {
    SseEmitter emitter = new SseEmitter();
    StringBuilder content = new StringBuilder();
    for (String s : contents) {
      content.append(s);
    }

    if (storyOptionDTO != null) {
      content.append("사투리: ").append(storyOptionDTO.getDialect());
      content.append("화자: ").append(storyOptionDTO.getSpeaker());
      content.append("위 글의 내용을 해당 화자가 해당 사투리로 말하는 것처럼해서 하나의 스토리로 작성해줘. 변경한 내용의 글 이외에는 필요없어");
    }

    content.append("위 글의 내용을 하나의 스토리로 작성해줘. 변경한 내용의 글 이외에는 필요없어");

    webClient = WebClient.builder().baseUrl(ALAN_API_URL).build();

    // 외부 API로 GET 요청을 보냄
    Flux<String> responseFlux =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(END_POINT)
                        .queryParam("content", content.toString())
                        .queryParam("client_id", CLIENT_ID)
                        .build())
            .retrieve()
            .bodyToFlux(String.class);

    // Flux 데이터 구독
    responseFlux.subscribe(
        data -> {
          // 데이터를 받았을 때마다 비동기 작업으로 emitter.send 호출
          executorService.submit(() -> sendData(emitter, data));
          System.out.println("보낼 Data: " + data);
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
