package com.manruhomerun.yadan.sticker.controller;

import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.sticker.dto.TravelStickerResponse;
import com.manruhomerun.yadan.sticker.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
@Tag(name = "Sticker", description = "스티커 API")
public class StickerController {
    private final StickerService stickerService;

    @GetMapping("/{travelId}/stickers")
    @Operation(summary = "여행 스티커 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 스티커 조회 성공",
                    content = @Content(schema = @Schema(implementation = TravelStickerResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TravelStickerResponse> getTravelStickers(
            @Parameter(description = "조회할 여행 ID", example = "1e3a5081-675e-4264-8e56-ebb659e12acd")
            @PathVariable
            String travelId,
            HttpServletRequest request
    ) {
        //String userId = (String) request.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(stickerService.getTravelStickers(travelId, userId));
    }

}
