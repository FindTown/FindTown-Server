package yapp.domain.member.converter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import yapp.common.config.Const;
import yapp.common.domain.Location;
import yapp.common.oauth.entity.RoleType;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.LocationInfo;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.Resident;
import yapp.domain.member.entity.YN;
import yapp.domain.town.entity.TownResident;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MemberInfoResponse toMemberInfo(
            Member member,
            List<Location> locationList,
            List<TownResident> townResidentList
    ) {
        return MemberInfoResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .resident(
                        townResidentList.stream().map(townResident ->
                                new Resident(
                                        townResident.getResidentAddress(),
                                        townResident.getResidentReview(),
                                        townResident.getMoods().split(","),
                                        townResident.getResidentYear(),
                                        townResident.getResidentMonth()
                                )
                        ).collect(Collectors.toList()))
                .useAgreeYn(member.getUseAgreeYn().getValue())
                .privacyAgreeYn(member.getPrivacyAgreeYn().getValue())
                .providerType(member.getProviderType())
                .locationList(locationList
                        .stream()
                        .map(location -> {
                            return new LocationInfo(location.getObjectId(), location.getSidoNm(),
                                    location.getSggNm(),
                                    location.getAdmNm()
                            );
                        })
                        .collect(Collectors.toList())
                )
                .build();
    }

    public Member toEntity(
            MemberSignUpRequest memberSignUpRequest
    ) {
        Member member = Member.builder()
                .memberId(memberSignUpRequest.getMemberId())
                .email(
                        StringUtils.hasText(memberSignUpRequest.getEmail())
                                ? memberSignUpRequest.getEmail()
                                : Const.DEFAULT_EMAIL)
                .nickname(memberSignUpRequest.getNickname())
                .providerType(memberSignUpRequest.getProviderType())
                .useAgreeYn(YN.of(memberSignUpRequest.isUseAgreeYn()))
                .privacyAgreeYn(YN.of(memberSignUpRequest.isPrivacyAgreeYn()))
                .useStatus(Const.USE_MEMBERS)
                .roleType(RoleType.USER)
                .build();
        member.encodeDefaultPassword(passwordEncoder);
        return member;
    }
}
