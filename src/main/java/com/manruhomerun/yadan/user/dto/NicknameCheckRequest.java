package com.manruhomerun.yadan.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "닉네임 중복 확인 요청")
public record NicknameCheckRequest(

        @Schema(description = "중복 확인할 닉네임", example = "야구 여행자")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname

) {

    public NicknameCheckRequest {
        if (nickname != null) {
            nickname = nickname.strip();
        }
    }
}
