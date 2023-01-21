package yapp.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.common.utils.CookieUtil;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.service.MemberService;

@RestController
@RequestMapping("/app/members")
@RequiredArgsConstructor
@Tag(name = "회원")
public class MemberController {

  private final MemberService memberService;

  public final static String ACCESS_TOKEN = "access_token";
  private final static String REFRESH_TOKEN = "refresh_token";

  @GetMapping("/info")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "내 정보 확인")
  @Tag(name = "[화면]-마이페이지")
  public ApiResponse getMemberInfo(
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    System.out.println("memberPricipal : " + memberPrincipal.toString());
    System.out.println("get Id : " + memberPrincipal.getUsername());
    MemberInfoResponse memberInfoResponse = this.memberService.getMemberInfo(
      memberPrincipal.getUsername());
    return ApiResponse.success("회원 정보 조회", memberInfoResponse);
  }
  
  @PostMapping("/signup")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "회원가입")
  @Tag(name = "[화면]-로그인")
  public ApiResponse socialSignUp(
    @CurrentAuthPrincipal User memberPrincipal,
    @RequestBody MemberSignUpRequest memberSignUpRequest
  ) {
    String memberId = this.memberService.memberSignUp(
      memberSignUpRequest, memberPrincipal.getUsername());
    if (StringUtils.hasText(memberId)) {
      return ApiResponse.success("회원 가입 성공", "");
    }
    return ApiResponse.signUpFail();
  }

  @GetMapping("/check/nickname")
  @Operation(summary = "닉네임 중복 확인")
  @Tag(name = "[화면]-회원가입")
  public ApiResponse checkNickname(
    @RequestParam(name = "nickname") String nickname
  ) {
    Map<String, Boolean> result = new HashMap<>();
    boolean duplicateConfirm = this.memberService.checkDuplicateNickname(nickname);
    result.put("existence", duplicateConfirm);

    return duplicateConfirm ? ApiResponse.success("이미 존재하는 닉네임 입니다.", result)
      : ApiResponse.success("등록 가능한 닉네임 입니다.", result);
  }

  @GetMapping("/logout")
  @PreAuthorize("hasRole('USER')")
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