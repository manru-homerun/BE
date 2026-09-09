package com.manruhomerun.yadan.user.dto;

import java.time.LocalDate;

import com.manruhomerun.yadan.user.domain.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Schema(description = "나의 프로필 수정 요청")
public record UserProfileUpdateRequest(

        @Schema(description = "프로필 이미지 URL, 이미지가 없으면 null", example = "https://example.com/profile.png")
        @Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다.")
        String profileImageUrl,

        @Schema(description = "닉네임", example = "야구 여행자")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname,

        @Schema(description = "응원 구단 ID", example = "1")
        @NotNull(message = "응원 구단은 필수입니다.")
        Long favoriteTeamId,

        @Schema(description = "생년월일", example = "1998-07-15")
        @NotNull(message = "생년월일은 필수입니다.")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        LocalDate birthday,

        @Schema(description = "성별", example = "MALE")
        @NotNull(message = "성별은 필수입니다.")
        Gender gender

) {

    public UserProfileUpdateRequest {
        if (nickname != null) {
            nickname = nickname.strip();
        }
    }
}
