package com.ahi.timecapsule.api;

import com.ahi.timecapsule.util.FileUtil;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class STTAPI {

  private final String JWTFilePath;

  private final String audioFilePath;

  @Value("${STT.URL}")
  private String URL;

  @Value("${STT.AUTH-URL}")
  private String AUTH_URL;

  @Value("${STT.CLIENT-ID}")
  private String CLIENT_ID;

  @Value("${STT.CLIENT-SECRET}")
  private String CLIENT_SECRET;

  private String id;

  private String jwt;

  public STTAPI() throws Exception {
    this.JWTFilePath = Paths.get(System.getProperty("user.home"), "files", "JWT.txt").toString();
    this.audioFilePath =
        Paths.get(System.getProperty("user.home"), "files") + "/sounds/recording.ogg";

    File file = new File(JWTFilePath);
    if (file.exists()) {
      this.jwt = FileUtil.decryptFile();
    }
  }

  // STT 사용을 위해 API로부터 JWT토큰을 발급받는 메소드
  // 매 6시마다 메소드 실행
  @Scheduled(cron = "0 0 */6 * * *")
  public void auth() throws Exception {
    URL url = new URL(AUTH_URL);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setRequestMethod("POST");
    httpConn.setRequestProperty("accept", "application/json");
    httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    httpConn.setDoOutput(true);

    String data = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

    byte[] out = data.getBytes(StandardCharsets.UTF_8);

    OutputStream stream = httpConn.getOutputStream();
    stream.write(out);

    InputStream responseStream =
        httpConn.getResponseCode() / 100 == 2
            ? httpConn.getInputStream()
            : httpConn.getErrorStream();
    Scanner s = new Scanner(responseStream).useDelimiter("\\A");
    String response = s.hasNext() ? s.next() : "";
    s.close();
    System.out.println(response);

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
    this.jwt = (String) jsonObject.get("access_token");

    FileUtil.encryptFile(jwt);
  }

  // 저장되어 있는 음성 파일을 해당 API로 전송하는 메소드
  public void post() throws IOException, ParseException {
    URL url = new URL(URL);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setRequestMethod("POST");
    httpConn.setRequestProperty("accept", "application/json");
    httpConn.setRequestProperty("Authorization", "Bearer " + jwt);
    httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=authsample");
    httpConn.setDoOutput(true);

    File file = new File(audioFilePath);

    DataOutputStream outputStream;
    outputStream = new DataOutputStream(httpConn.getOutputStream());

    outputStream.writeBytes("--authsample\r\n");
    outputStream.writeBytes(
        "Content-Disposition: form-data; name=\"file\";filename=\"" + file.getName() + "\"\r\n");
    outputStream.writeBytes(
        "Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n");
    outputStream.writeBytes("Content-Transfer-Encoding: binary" + "\r\n");
    outputStream.writeBytes("\r\n");

    FileInputStream in = new FileInputStream(file);
    byte[] buffer = new byte[(int) file.length()];
    int bytesRead = -1;
    while ((bytesRead = in.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
      outputStream.writeBytes("\r\n");
      outputStream.writeBytes("--authsample\r\n");
    }
    outputStream.writeBytes("\r\n");
    outputStream.writeBytes("--authsample\r\n");
    outputStream.writeBytes("Content-Disposition: form-data; name=\"config\"\r\n");
    outputStream.writeBytes("Content-Type: application/json\r\n");
    outputStream.writeBytes("\r\n");
    outputStream.writeBytes("{}");
    outputStream.writeBytes("\r\n");
    outputStream.writeBytes("--authsample\r\n");
    outputStream.flush();
    outputStream.close();

    InputStream responseStream =
        httpConn.getResponseCode() / 100 == 2
            ? httpConn.getInputStream()
            : httpConn.getErrorStream();
    Scanner s = new Scanner(responseStream).useDelimiter("\\A");
    String response = s.hasNext() ? s.next() : "";
    s.close();
    System.out.println(response);

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
    this.id = (String) jsonObject.get("id");
  }

  // 요청에 대한 결과를 받는 메소드
  public List<String> get() throws IOException, ParseException, InterruptedException {
    String response;
    Scanner s;
    InputStream responseStream;

    while (true) {
      URL url = new URL(URL + "/" + id);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setRequestMethod("GET");
      httpConn.setRequestProperty("accept", "application/json");
      httpConn.setRequestProperty("Authorization", "Bearer " + jwt);

      if (httpConn.getResponseCode() / 100 == 2) {
        responseStream = httpConn.getInputStream();
      } else {
        responseStream = httpConn.getErrorStream();
      }

      s = new Scanner(responseStream).useDelimiter("\\A");
      response = s.hasNext() ? s.next() : "";

      // STT 작업이 완료된 상태이면 결과 반환
      // 아니라면 10초 주기로 Polling
      if (response.contains("completed")) {
        break;
      } else {
        System.out.println("응답 결과" + response);
        Thread.sleep(10000);
      }
    }

    s.close();
    System.out.println(response);

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(response);

    JSONObject results = (JSONObject) jsonObject.get("results");
    JSONArray utterances = (JSONArray) results.get("utterances");

    List<String> strs = new ArrayList<>();
    for (int i = 0; i < utterances.size(); i++) {
      JSONObject utterance = (JSONObject) utterances.get(i);
      strs.add((String) utterance.get("msg"));
    }
    return strs;
  }
}
