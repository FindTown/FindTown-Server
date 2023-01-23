package yapp.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.config.Const;
import yapp.common.domain.Location;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.converter.MemberConverter;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.entitiy.MemberWishTown;
import yapp.domain.member.entitiy.WishStatus;
import yapp.domain.member.repository.MemberRepository;
import yapp.domain.member.repository.MemberWishTownRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {
  private final MemberRepository memberRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final LocationRepository locationRepository;
  private final MemberConverter memberConverter;

  public MemberService(
    MemberRepository memberRepository,
    MemberWishTownRepository memberWishTownRepository,
    LocationRepository locationRepository,
    MemberConverter memberConverter
  ) {
    this.memberRepository = memberRepository;
    this.memberWishTownRepository = memberWishTownRepository;
    this.locationRepository = locationRepository;
    this.memberConverter = memberConverter;
  }

  public MemberInfoResponse getMemberInfo(String memberId) {
    Member member = this.memberRepository.findByMemberIdAndUseStatus(memberId, Const.USE_MEMBERS)
      .orElseThrow(() -> new UsernameNotFoundException("현재 사용중인 회원이 아닙니다"));

    List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
      memberId).stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

    log.info("회원 ID : {}", member.getMemberId());
    log.info("찜 목록 조회 : {}", memberWishTownList.size());
    return memberConverter.toMemberInfo(member, memberWishTownList).get();
  }

  @Transactional
  public String memberSignUp(
    MemberSignUpRequest memberSignUpRequest,
    String memberId
  ) {
    Member signUpMember = this.memberRepository.findByMemberId(memberId)
      .orElseThrow(() -> new UsernameNotFoundException("회원 ID로 계정을 조회할 수 없다."));

    signUpMember.setSignUp(memberSignUpRequest);
    signUpMember.changeMemberStatus(Const.USE_MEMBERS);

    if (memberSignUpRequest.getObjectId() != null) {
      Location location = this.locationRepository.getLocationByObjectId(
          memberSignUpRequest.getObjectId())
        .orElseThrow(() -> new RuntimeException("입력한 동네는 현재 존재하지 않습니다."));
      this.memberWishTownRepository.save(new MemberWishTown(memberId, location, WishStatus.YES));
    }

    return this.memberRepository.save(signUpMember).getMemberId();
  }

  @Transactional
  public void memberLogout(
    String accessToken,
    String memberId
  ) {
    // 1. DB refresh 토큰 삭제하기
    this.memberRefreshTokenRepository.deleteByMemberId(memberId);

    // 2. redis에 access_token 등록
    Long expiration = authTokenProvider.getExpiration(accessToken);
    redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
  }

  public boolean checkDuplicateNickname(String nickname) {
    return this.memberRepository.existsAllByNickname(nickname);
  }
//  public Map<String, String> getKaKaoAccessToken(String accessCode) {
//    String accessToken = "";
//    String refreshToken = "";
//    String reqURL = "https://kauth.kakao.com/oauth/token";
//    HashMap<String, String> tokenInfo = new HashMap<>();
//
//    try {
//      URL url = new URL(reqURL);
//      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//      //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
//      conn.setRequestMethod("POST");
//      conn.setDoOutput(true);
//
//      //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
//      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//      StringBuilder sb = new StringBuilder();
//      sb.append("grant_type=authorization_code");
//      sb.append("&client_secret=" + clientSecret);
//      sb.append("&client_id=" + clientId);
//      sb.append("&redirect_uri=" + redirectUri);
//      sb.append("&code=" + accessCode);
//      bw.write(sb.toString());
//      bw.flush();
//
//      //결과 코드가 200이라면 성공
//      int responseCode = conn.getResponseCode();
//      System.out.println("responseCode : " + responseCode);
//      System.out.println("responseBody : " + conn.getResponseMessage());
//
//      //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
//      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//      String line = "";
//      String result = "";
//
//      while ((line = br.readLine()) != null) {
//        result += line;
//      }
//      System.out.println("response body : " + result);
//
//      //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
//      JsonParser parser = new JsonParser();
//      JsonElement element = parser.parse(result);
//
//      // 카카오에서 발급해주는 토큰
//      accessToken = element.getAsJsonObject().get("access_token").getAsString();
//      refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
//
//      System.out.println("access_token : " + accessToken);
//      System.out.println("refresh_token : " + refreshToken);
//
//      tokenInfo.put("access_token", accessToken);
//      tokenInfo.put("refresh_token", refreshToken);
//
//      br.close();
//      bw.close();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return tokenInfo;
//  }
//
//  public void getTownScoopInfo() {
//
//  }
//
//  // 카카오에서 발급해주는 토큰으로만 접근가능하다!
//  public HashMap<String, Object> getKakaoMemberInfo(String accessCode) {
//
//    HashMap<String, Object> userInfo = new HashMap<>();
//    String reqURL = "https://kapi.kakao.com/v2/user/me";
//    try {
//      URL url = new URL(reqURL);
//      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//      conn.setRequestMethod("POST");
//      conn.setDoOutput(true);
//      conn.setRequestProperty("Authorization", "Bearer " + accessCode);
//      conn.setRequestProperty("client_secret", clientSecret);
//      conn.setRequestProperty("client_id", clientId);
//      conn.setRequestProperty("grant_type", "authorization_code");
//      conn.setRequestProperty("redirect_uri", redirectUri);
//
//      int responseCode = conn.getResponseCode();
//      System.out.println("access_code : " + accessCode);
//      System.out.println("responseCode : " + responseCode);
//      System.out.println("responseResponseMessage : " + conn.getResponseMessage());
//
//      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//      String line = "";
//      String result = "";
//
//      while ((line = br.readLine()) != null) {
//        result += line;
//      }
//      System.out.println("response body : " + result);
//
//      JsonParser parser = new JsonParser();
//      JsonElement element = parser.parse(result);
//
//      JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
//      JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
//
//      String nickname = properties.getAsJsonObject().get("nickname").getAsString();
//      String email = kakao_account.getAsJsonObject().get("email").getAsString();
//      String id = element.getAsJsonObject().get("id").getAsString();
//
//      userInfo.put("nickname", nickname);
//      userInfo.put("email", email);
//      userInfo.put("id", id);
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    return userInfo;
//  }
//
////  public MemberInfoDto getTownMemberInfo() {
////
////  }
}
