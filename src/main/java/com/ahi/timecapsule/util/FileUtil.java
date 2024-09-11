package com.ahi.timecapsule.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class FileUtil {

  private static final String basicFilePath =
      Paths.get(System.getProperty("user.home"), "files").toString();

  private static final String JWTFilePath =
      Paths.get(System.getProperty("user.home"), "files", "JWT.txt").toString();

  // 고정된 AES 키를 사용하여 SecretKey 생성
  // AES를 사용한 이유: 클라이언트와 파일을 주고 받는 것이 아닌, 단순 JWT 토큰(STT)을 로컬에 저장하는 것
  // 즉, 키가 노출될 위험이 적은 상황이기 때문에 안정성과 속도가 빠른 AES 선택
  private static SecretKey secretKey = new SecretKeySpec("myServiceKey9876".getBytes(), "AES");

  // 파일 암호화 (결과를 파일로 저장)
  public static void encryptFile(String input) throws Exception {

    // Cipher 객체를 사용해 AES 암호화 수행
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] encryptedBytes = cipher.doFinal(input.getBytes());

    // 암호화된 데이터를 파일로 저장
    Files.write(Paths.get(JWTFilePath), encryptedBytes);

    System.out.println("파일 암호화 완료: " + JWTFilePath);
  }

  // 파일 복호화 (결과를 파일로 저장)
  public static String decryptFile() throws Exception {
    // 암호화된 파일을 바이트 배열로 읽음
    byte[] encryptedBytes = Files.readAllBytes(Paths.get(JWTFilePath));

    // Cipher 객체를 사용해 AES 복호화 수행
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

    System.out.println("파일 복호화 완료: " + JWTFilePath);

    return new String(decryptedBytes, "UTF-8");
  }
}
