package com.manruhomerun.yadan.travelspot.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.client.ExternalApiClient;
import com.manruhomerun.yadan.global.error.UserNotFoundException;
import com.manruhomerun.yadan.travelspot.domain.entity.Dibs;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travelspot.error.TravelSpotErrorCode;
import com.manruhomerun.yadan.travelspot.error.TravelSpotException;
import com.manruhomerun.yadan.travelspot.repository.DibsRepository;
import com.manruhomerun.yadan.travelspot.repository.TravelSpotRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelSpotService {

    private final TravelSpotRepository travelSpotRepository;
    private final DibsRepository dibsRepository;
    private final UserRepository userRepository;
    private final ExternalApiClient externalApiClient;

    public void createDibs(String userId, String contentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        TravelSpot travelSpot = travelSpotRepository.findById(contentId)
                .orElseGet(() -> {
                    Map<String, Object> queryParams = new LinkedHashMap<>();
                    queryParams.put("contentId", contentId);
                    TourApiDetailCommonResponse response = externalApiClient.get(
                            "/detailCommon2",
                            queryParams,
                            TourApiDetailCommonResponse.class
                    );

                    if (response.response().body() == null
                            || response.response().body().items() == null
                            || response.response().body().items().item() == null
                            || response.response().body().items().item().isEmpty()) {
                        throw new TravelSpotException(
                                TravelSpotErrorCode.TRAVEL_SPOT_NOT_FOUND,
                                "여행지를 찾을 수 없습니다. contentId=" + contentId
                        );
                    }

                    TourApiDetailCommonResponse.Item item = response.response().body().items().item().getFirst();

                    // 외부 API 응답을 현재 travel_spot 스키마에 맞춰 저장한다.
                    return travelSpotRepository.save(
                            TravelSpot.builder()
                                    .id(item.contentid())
                                    .name(item.title())
                                    .latitude(new BigDecimal(item.mapy()))
                                    .longitude(new BigDecimal(item.mapx()))
                                    .regionCode(item.lDongRegnCd() + item.lDongSignguCd())
                                    .category(Integer.valueOf(item.contenttypeid()))
                                    .image(item.firstimage() == null || item.firstimage().isBlank() ? null : item.firstimage())
                                    .build()
                    );
                });

        if (!dibsRepository.existsByUserIdAndTravelSpotId(userId, travelSpot.getId())) {
            dibsRepository.save(
                    Dibs.builder()
                            .user(user)
                            .travelSpot(travelSpot)
                            .build()
            );
        }
    }
}
