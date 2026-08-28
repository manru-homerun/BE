package com.manruhomerun.yadan.user.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 검색 목록 응답")
public record UserSearchResponse(

        @Schema(description = "반환된 검색 결과 수", example = "2")
        int resultCount,

        @Schema(description = "검색된 사용자 목록")
        List<UserSearchItemResponse> users

) {

    public static UserSearchResponse from(List<UserSearchItemResponse> users) {
        return new UserSearchResponse(
                users.size(),
                users
        );
    }
}
