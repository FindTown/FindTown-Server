package yapp.domain.member.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  @Value("${social-kakao.clientId}")
  private String clientId;

  @Value("${social-kakao.clientSecret}")
  private String clientSecret;

  @Value("${social-kakao.redirectUri}")
  private String redirectUri;

  public Member getMemer(String memberId) {
    return memberRepository.findByMemberId(memberId);
  }

  public Map<String, String> getKaKaoAccessToken(String code) {
    String accessToken = "";
    String refreshToken = "";
    String reqURL = "https://kauth.kakao.com/oauth/token";
    Map<String, String> tokenInfo = new HashMap<>();

    try {
      URL url = new URL(reqURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);

      //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
      StringBuilder sb = new StringBuilder();
      sb.append("grant_type=authorization_code");
      sb.append("&client_secret=" + clientSecret);
      sb.append("&client_id=" + clientId);
      sb.append("&redirect_uri=" + redirectUri);
      sb.append("&code=" + code);
      bw.write(sb.toString());
      bw.flush();

      //결과 코드가 200이라면 성공
      int responseCode = conn.getResponseCode();
      System.out.println("responseCode : " + responseCode);
      System.out.println("responseBody : " + conn.getResponseMessage());

      //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = "";
      String result = "";

      while ((line = br.readLine()) != null) {
        result += line;
      }
      System.out.println("response body : " + result);

      //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(result);

      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

      System.out.println("access_token : " + accessToken);
      System.out.println("refresh_token : " + refreshToken);

      tokenInfo.put("access_token", accessToken);
      tokenInfo.put("refresh_token", refreshToken);

      br.close();
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tokenInfo;
  }

  public HashMap<String, Object> getKakaoMemberInfo(String accessToken) {

    HashMap<String, Object> userInfo = new HashMap<>();
    String reqURL = "https://kapi.kakao.com/v2/user/me";
    try {
      URL url = new URL(reqURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);

      int responseCode = conn.getResponseCode();
      System.out.println("responseCode : " + responseCode);
      System.out.println("responseResponseMessage : " + conn.getResponseMessage());

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";
      String result = "";

      while ((line = br.readLine()) != null) {
        result += line;
      }
      System.out.println("response body : " + result);

      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(result);

      JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
      JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

      String nickname = properties.getAsJsonObject().get("nickname").getAsString();
      String email = kakao_account.getAsJsonObject().get("email").getAsString();

      userInfo.put("nickname", nickname);
      userInfo.put("email", email);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return userInfo;
  }

//  public MemberInfoDto getTownMemberInfo() {
//
//  }
}
