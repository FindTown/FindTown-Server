package yapp.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.config.Const;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.response.ApiResponse;
import yapp.common.response.ApiResponseHeader;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.common.utils.HeaderUtil;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.service.MemberService;

@RestController
@RequestMapping("/app/members")
@Tag(name = "회원")
public class MemberController {

  private final MemberService memberService;
  private final AuthTokenProvider tokenProvider;

  public MemberController(
    MemberService memberService,
    AuthTokenProvider tokenProvider
  ) {
    this.memberService = memberService;
    this.tokenProvider = tokenProvider;
  }

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
    return ApiResponse.success("member_info", memberInfoResponse);
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
      return ApiResponse.success("signup", true);
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

    return duplicateConfirm ? ApiResponse.success("exist_confirm", duplicateConfirm)
      : ApiResponse.success("exist_confirm", result);
  }

  @GetMapping("/logout")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "로그 아웃")
  @Tag(name = "[화면]-마이페이지")
  public ApiResponse logout(
    HttpServletRequest request,
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    String accessToken = HeaderUtil.getAccessToken(request);
    AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
    if (!authToken.validate()) {
      return ApiResponse.invalidAccessToken();
    }

    this.memberService.memberLogout(accessToken, memberPrincipal.getUsername());
    return ApiResponse.success("logout", true);
  }
}