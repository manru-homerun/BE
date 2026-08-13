package com.manruhomerun.yadan.user.dto;

import java.time.LocalDate;
import java.util.Set;

import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.Gender;
import com.manruhomerun.yadan.user.domain.enums.TravelRegionCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Schema(description = "온보딩 정보 저장 요청")
public record OnboardingRequest(

        @Schema(description = "약관 동의 정보")
        @Valid
        @NotNull(message = "약관 동의 정보는 필수입니다.")
        AgreementsRequest agreements,

        @Schema(description = "사용자가 설정한 닉네임", example = "야구여행자")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 12, message = "닉네임은 12자 이하여야 합니다.")
        String nickname,

        @Schema(description = "성별", example = "MALE")
        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @Schema(description = "생년월일", example = "1998-07-15")
        @NotNull(message = "생년월일은 필수입니다.")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        LocalDate birthday,

        @Schema(description = "사용자가 선택한 응원 구단 ID", example = "1")
        @NotNull(message = "응원 구단은 필수입니다.")
        Long favoriteTeamId,

        @Schema(description = "거주 지역 이름", example = "서울")
        @NotBlank(message = "거주 지역은 필수입니다.")
        String residenceRegion,

        @Schema(description = "여행 스타일 값, 1은 자연 선호이고 7은 도시 선호", example = "4")
        @NotNull(message = "여행 스타일은 필수입니다.")
        @Min(value = 1, message = "여행 스타일 값은 1 이상이어야 합니다.")
        @Max(value = 7, message = "여행 스타일 값은 7 이하여야 합니다.")
        Integer travelStyleValue,

        @Schema(description = "사용자가 선택한 선호 여행 지역 이름 목록", example = "[\"부산\", \"창원\"]")
        @NotEmpty(message = "선호 여행 지역은 한 개 이상 선택해야 합니다.")
        Set<@NotBlank(message = "선호 여행 지역 이름은 비어 있을 수 없습니다.") String> preferredRegions

) {

    public TravelPreference toEntity(
            User user,
            TravelRegionCode residenceRegionCode,
            Set<TravelRegionCode> preferredRegionCodes
    ) {
        return TravelPreference.builder()
                .user(user)
                .travelStyleValue(travelStyleValue)
                .residenceRegionCode(residenceRegionCode)
                .preferredRegionCodes(preferredRegionCodes)
                .build();
    }
}
