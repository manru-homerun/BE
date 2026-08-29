package com.manruhomerun.yadan.sticker.service;

import com.manruhomerun.yadan.sticker.domain.entity.Sticker;
import com.manruhomerun.yadan.sticker.domain.entity.StickerPack;
import com.manruhomerun.yadan.sticker.dto.TravelStickerResponse;
import com.manruhomerun.yadan.sticker.repository.StickerRepository;
import com.manruhomerun.yadan.travel.domain.entity.TravelSticker;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;
import com.manruhomerun.yadan.travel.error.exception.TravelNotFoundException;
import com.manruhomerun.yadan.travel.repository.TravelRepository;
import com.manruhomerun.yadan.travel.repository.TravelStickerRepository;
import com.manruhomerun.yadan.travel.repository.TravelUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StickerService {
    private final TravelRepository travelRepository;
    private final TravelStickerRepository travelStickerRepository;
    private final TravelUserRepository travelUserRepository;
    private final StickerRepository stickerRepository;

    public TravelStickerResponse getTravelStickers(String travelId, String userId) {
        travelRepository.findById(travelId).orElseThrow(
                () -> new TravelNotFoundException(TravelErrorCode.TRAVEL_NOT_FOUND, "여행을 찾을 수 없습니다. travelId=" + travelId));
        travelUserRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<TravelSticker> travelStickerOptional = travelStickerRepository.findFirstByTravelIdOrderByIdAsc(travelId);
        if (travelStickerOptional.isEmpty()) {
            return TravelStickerResponse.empty();
        }

        StickerPack stickerPack = travelStickerOptional.get().getStickerPack();
        List<Sticker> stickers = stickerRepository.findAllByStickerPackIdOrderByIdAsc(stickerPack.getId());

        // 여행에 연결된 스티커팩 기준으로 전체 스티커 목록을 내려준다.
        return TravelStickerResponse.of(stickerPack, stickers);
    }
}
