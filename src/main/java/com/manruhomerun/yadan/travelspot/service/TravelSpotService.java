package com.manruhomerun.yadan.travelspot.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.client.ExternalApiClient;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travelspot.domain.entity.Dibs;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailImageResponse;
import com.manruhomerun.yadan.travelspot.dto.TourApiSearchKeywordResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDetailResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDibsItemResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotSearchItemResponse;
import com.manruhomerun.yadan.travelspot.error.TravelSpotErrorCode;
import com.manruhomerun.yadan.travelspot.error.exception.TravelSpotException;
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

    public void deleteDibs(String userId, String contentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // DELETE는 멱등적으로 처리해, 찜이 없어도 성공 응답을 반환한다.
        dibsRepository.deleteByUserIdAndTravelSpotId(userId, contentId);
    }

    @Transactional(readOnly = true)
    public List<TravelSpotDibsItemResponse> getDibs(String userId, TravelRegionCode regionCode) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 기준 지역 코드의 뒤쪽 0을 제거한 prefix로 같은 지역 소속 여행지를 조회한다.
        String regionCodePrefix = regionCode.getCodePrefix();

        return dibsRepository.findByUserIdAndTravelSpotRegionCodeStartingWithOrderByCreatedAtDescIdDesc(userId, regionCodePrefix).stream()
                .map(Dibs::getTravelSpot)
                .map(TravelSpotDibsItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TravelSpotDetailResponse getSpotDetail(String spotId) {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("contentId", spotId);

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
                    "여행지를 찾을 수 없습니다. contentId=" + spotId
            );
        }

        TourApiDetailCommonResponse.Item item = response.response().body().items().item().getFirst();
        String address = item.addr2() == null || item.addr2().isBlank()
                ? item.addr1()
                : item.addr1() + " " + item.addr2();

        return new TravelSpotDetailResponse(
                item.contentid(),
                TravelSpotCategory.getDisplayNameByContentTypeId(Integer.valueOf(item.contenttypeid())),
                item.title(),
                item.tel(),
                item.homepage(),
                item.lDongRegnCd() + item.lDongSignguCd(),
                address,
                item.mapx(),
                item.mapy(),
                item.overview()
        );
    }

    @Transactional(readOnly = true)
    public List<String> getSpotImages(String spotId) {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("contentId", spotId);

        TourApiDetailImageResponse response = externalApiClient.get(
                "/detailImage2",
                queryParams,
                TourApiDetailImageResponse.class
        );

        if (response.response().body() == null
                || response.response().body().items() == null
                || response.response().body().items().item() == null) {
            return List.of();
        }

        return response.response().body().items().item().stream()
                .map(TourApiDetailImageResponse.Item::originimgurl)
                .filter(originImageUrl -> originImageUrl != null && !originImageUrl.isBlank())
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TravelSpotSearchItemResponse> getSpots(String keyword, TravelRegionCode region, int pageNumber, int pageSize) {
        String regionCode = region.getCode();

        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("keyword", keyword);
        queryParams.put("numOfRows", pageSize);
        queryParams.put("pageNo", pageNumber);
        queryParams.put("lDongRegnCd", regionCode.substring(0, 2));

        String signguCode = regionCode.substring(2);
        if (!"000".equals(signguCode)) {
            queryParams.put("lDongSignguCd", signguCode);
        }

        TourApiSearchKeywordResponse response = externalApiClient.get(
                "/searchKeyword2",
                queryParams,
                TourApiSearchKeywordResponse.class
        );

        if (response.response().body() == null) {
            return PageResponse.from(
                    new PageImpl<>(
                            List.of(),
                            PageRequest.of(pageNumber - 1, pageSize),
                            0
                    ),
                    List.of()
            );
        }

        List<TravelSpotSearchItemResponse> content = response.response().body().items() == null
                || response.response().body().items().item() == null
                ? List.of()
                : response.response().body().items().item().stream()
                .map(TravelSpotSearchItemResponse::from)
                .toList();

        int resolvedPageSize = response.response().body().numOfRows() == null ? pageSize : response.response().body().numOfRows();
        long totalElements = response.response().body().totalCount() == null ? 0 : response.response().body().totalCount();

        return PageResponse.from(
                new PageImpl<>(
                        content,
                        PageRequest.of(pageNumber - 1, resolvedPageSize),
                        totalElements
                ),
                content
        );
    }
}
