package yapp.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.response.ApiResponse;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.service.MemberService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping
  public ApiResponse getUser() {
    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Member member = memberService.getMemer(principal.getUsername());

    return ApiResponse.success("member", member);
  }
}
