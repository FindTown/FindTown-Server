package yapp.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.utils.CookieUtil;
import yapp.domain.member.dto.MemberAccountDto;
import yapp.domain.member.dto.MemberInfoDto;
import yapp.domain.member.service.MemberService;

@RestController
@RequestMapping("/app/members")
@RequiredArgsConstructor
@Tag(name = "회원")
public class MemberController {

  private final MemberService memberService;

  public final static String ACCESS_TOKEN = "access_token";
  private final static String REFRESH_TOKEN = "refresh_token";

  @GetMapping("/kakao")
  @Operation(summary = "로그인", description = "http://{서버IP}:8080/oauth2/authorization/kakao?redirect_uri=http://{서버IP}:8080/app/members/kakao 으로 최초 접속해야만 토큰 발행")
  @Tag(name = "[화면]-로그인")
  public ApiResponse getTokenBykakaoLogin(@RequestParam("code") String code) {
    Map<String, String> kakaoToken = memberService.getKaKaoAccessToken(code);
//    HashMap<String, Object> memberInfo = memberService.getUserInfo(kakaoToken.get("access_token"));
    return ApiResponse.success("AcessToken, RefreshToken", kakaoToken);
  }

  // 회원 정보 조회
  @GetMapping("/info")
  @Operation(summary = "내 정보 확인")
  @Tag(name = "[화면]-마이페이지")
  public ApiResponse getMemberInfo(
    @RequestBody MemberAccountDto memberAccountDto
  ) {
//    loadUserByUsername
//    memberService.getMemberInfo(memberAccountDto);
//    HashMap<String, Object> tokenPrivider = memberService
    return ApiResponse.success("회원 정보 조회", "");
  }

  //회원 가입
  @PostMapping("/signup")
  @Operation(summary = "회원가입")
  @Tag(name = "[화면]-로그인")
  public ApiResponse kakaoLoginAccessToken(
    @RequestHeader("access_token") String accessToken,
    @RequestBody MemberInfoDto memberInfoDto
  ) {
    Map<String, String> token = memberService.getKaKaoAccessToken(accessToken);

    return ApiResponse.success("회원 가입 성공", token);
  }

  @GetMapping("/logout")
  @Operation(summary = "로그 아웃")
  @Tag(name = "[화면]-마이페이지")
  public ApiResponse logout(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);

    return ApiResponse.success("로그 아웃", "");
  }

}
