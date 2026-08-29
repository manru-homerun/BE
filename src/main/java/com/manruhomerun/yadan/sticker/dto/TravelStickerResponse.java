package com.manruhomerun.yadan.sticker.dto;

import com.manruhomerun.yadan.sticker.domain.entity.Sticker;
import com.manruhomerun.yadan.sticker.domain.entity.StickerPack;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "여행 스티커 조회 응답")
public record TravelStickerResponse(
        @Schema(description = "스티커 보유 여부", example = "true")
        boolean hasSticker,

        @Schema(description = "스티커팩 정보", nullable = true)
        StickerPackResponse stickerPack
) {
    public static TravelStickerResponse empty() {
        return new TravelStickerResponse(false, null);
    }

    public static TravelStickerResponse of(StickerPack stickerPack, List<Sticker> stickers) {
        return new TravelStickerResponse(
                true,
                StickerPackResponse.from(stickerPack, stickers)
        );
    }

    @Schema(description = "스티커팩 정보")
    public record StickerPackResponse(
            @Schema(description = "스티커팩 ID", example = "23")
            Long id,

            @Schema(description = "스티커팩 이름", example = "25시즌 서울 스티커팩")
            String name,

            @Schema(description = "스티커 목록")
            List<StickerResponse> stickers
    ) {
        public static StickerPackResponse from(StickerPack stickerPack, List<Sticker> stickers) {
            return new StickerPackResponse(
                    stickerPack.getId(),
                    stickerPack.getName(),
                    stickers.stream()
                            .map(StickerResponse::from)
                            .toList()
            );
        }
    }

    @Schema(description = "스티커 정보")
    public record StickerResponse(
            @Schema(description = "스티커 ID", example = "1")
            Long id,

            @Schema(description = "스티커 이미지 URL", example = "https://example.com/sticker.png")
            String image
    ) {
        public static StickerResponse from(Sticker sticker) {
            return new StickerResponse(
                    sticker.getId(),
                    sticker.getImage()
            );
        }
    }
}
