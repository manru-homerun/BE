package com.manruhomerun.yadan.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 닉네임 검색 요청")
public record UserSearchRequest(

        @Schema(description = "검색할 닉네임", example = "야구")
        @NotBlank(message = "검색할 닉네임은 필수입니다.")
        @Size(max = 12, message = "닉네임 검색어는 12자 이하여야 합니다.")
        String nickname,

        @Schema(description = "최대 조회 개수", example = "10", defaultValue = "10")
        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 20, message = "조회 개수는 20 이하여야 합니다.")
        Integer limit

) {

    public UserSearchRequest {
        if (nickname != null) {
            nickname = nickname.strip();
        }

        if (limit == null) {
            limit = 10;
        }
    }
}
