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
import yapp.exception.base.member.MemberException.MemberSignUpFail;

@RestController
@RequestMapping("/app/members")
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
    MemberInfoResponse memberInfoResponse = this.memberService.getMemberInfo(
      memberPrincipal.getUsername());
    return ApiResponse.success("member_info", memberInfoResponse);
  }

  @PostMapping("/signup")
  @Operation(summary = "회원가입")
  @Tag(name = "[화면]-로그인/회원가입")
  public ApiResponse socialSignUp(
    @RequestBody MemberSignUpRequest memberSignUpRequest
  ) {
    String memberId = this.memberService.memberSignUp(memberSignUpRequest);

    if (StringUtils.hasText(memberId)) {
      return ApiResponse.success("signup", true);
    } else {
      throw new MemberSignUpFail("회원 가입에 실패하셨습니다.");
    }
  }

  @DeleteMapping("/resign")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "회원 탈퇴")
  @Tag(name = "[화면]-마이페이지")
  public ApiResponse resignMember(
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    this.memberService.removeMember(memberPrincipal.getUsername());
    return ApiResponse.success("resign_member", true);
  }

  @GetMapping("/check/nickname")
  @Operation(summary = "닉네임 중복 확인")
  @Tag(name = "[화면]-로그인/회원가입")
  public ApiResponse checkNickname(
    @RequestParam(name = "nickname") String nickname
  ) {
    boolean duplicateConfirm = this.memberService.checkDuplicateNickname(nickname);

    return duplicateConfirm ? ApiResponse.success("exist_confirm", true)
      : ApiResponse.success("exist_confirm", false);
  }

  @GetMapping("/check/register")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "회원 가입 여부")
  @Tag(name = "[화면]-로그인/회원가입")
  public ApiResponse checkRegisterMember(
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    Map<String, Integer> result = new HashMap<>();
    int isRegister = this.memberService.checkRegister(memberPrincipal.getUsername());
    switch (isRegister) {
      case 0:
        result.put("register_check", Const.NON_MEMBERS);
        return new ApiResponse(new ApiResponseHeader(200, "비회원 입니다"), result);
      case 1:
        result.put("register_check", Const.USE_MEMBERS);
        return new ApiResponse(new ApiResponseHeader(200, "회원 입니다"), result);
      default:
        result.put("register_check", Const.QUIT_MEMBERS);
        return new ApiResponse(new ApiResponseHeader(200, "탈퇴(휴면)회원 입니다"), result);
    }
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

  @PostMapping("/wishtown")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "찜 등록/해제")
  @Tag(name = "[찜]")
  public ApiResponse setMemberWishTown(
    @CurrentAuthPrincipal User memberPrincipal,
    @RequestParam String object_id
  ){
    this.memberService.setMemberWishTown(object_id, memberPrincipal.getUsername());

    return ApiResponse.success("wishTown", true);
  }


}