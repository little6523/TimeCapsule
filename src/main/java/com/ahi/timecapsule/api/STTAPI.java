package com.ahi.timecapsule.api;

import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class STTAPI {

  @Value("${STT.URL}")
  private String openApiURL;

  @Value("${STT.API-KEY}")
  private String accessKey;    // 발급받은 API Key
  private String languageCode = "korean";     // 언어 코드
  private String audioFilePath = "AUDIO_FILE_PATH";  // 녹음된 음성 파일 경로
  private String audioContents = null;

  Gson gson = new Gson();

  Map<String, Object> request = new HashMap<>();
  Map<String, String> argument = new HashMap<>();

  public void SpeechToText() {
    try {
      Path path = Paths.get(audioFilePath);
      byte[] audioBytes = Files.readAllBytes(path);
      audioContents = Base64.getEncoder().encodeToString(audioBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }

    argument.put("language_code", languageCode);
    argument.put("audio", audioContents);

    request.put("argument", argument);

    URL url;
    Integer responseCode = null;
    String responBody = null;
    try {
      url = new URL(openApiURL);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setRequestProperty("Authorization", accessKey);

      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.write(gson.toJson(request).getBytes("UTF-8"));
      wr.flush();
      wr.close();

      responseCode = con.getResponseCode();
      InputStream is = con.getInputStream();
      byte[] buffer = new byte[is.available()];
      int byteRead = is.read(buffer);
      responBody = new String(buffer);

      System.out.println("[responseCode] " + responseCode);
      System.out.println("[responBody]");
      System.out.println(responBody);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
