package com.manruhomerun.yadan.user.dto;

import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.entity.UserAgreement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "온보딩 약관 동의 요청")
public record AgreementsRequest(

        @Schema(description = "서비스 이용약관 동의 여부", example = "true")
        @NotNull(message = "서비스 이용약관 동의 여부는 필수입니다.")
        Boolean serviceTerms,

        @Schema(description = "개인정보 수집 및 이용 동의 여부", example = "true")
        @NotNull(message = "개인정보 수집 및 이용 동의 여부는 필수입니다.")
        Boolean privacyPolicy,

        @Schema(description = "마케팅 알림 수신 동의 여부", example = "false")
        Boolean marketing

) {

    public UserAgreement toEntity(User user) {
        return UserAgreement.builder()
                .user(user)
                .serviceTerms(serviceTerms)
                .privacyPolicy(privacyPolicy)
                .marketing(Boolean.TRUE.equals(marketing))
                .build();
    }
}
