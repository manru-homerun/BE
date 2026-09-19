package com.manruhomerun.yadan.travelspot.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.client.ExternalApiClient;
import com.manruhomerun.yadan.global.client.AiApiClient;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.domain.enums.CompanionCondition;
import com.manruhomerun.yadan.travel.dto.PopularTravelSpotResponse;
import com.manruhomerun.yadan.travelspot.domain.entity.Dibs;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailImageResponse;
import com.manruhomerun.yadan.travelspot.dto.TourApiSearchKeywordResponse;
import com.manruhomerun.yadan.travelspot.dto.AiTravelSpotRecommendRequest;
import com.manruhomerun.yadan.travelspot.dto.AiTravelSpotRecommendResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDetailResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotDibsItemResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotSearchItemResponse;
import com.manruhomerun.yadan.travelspot.dto.TravelSpotSuggestionRequest;
import com.manruhomerun.yadan.travelspot.error.TravelSpotErrorCode;
import com.manruhomerun.yadan.travelspot.error.exception.TravelSpotException;
import com.manruhomerun.yadan.travelspot.repository.DibsRepository;
import com.manruhomerun.yadan.travelspot.repository.TravelSpotRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import com.manruhomerun.yadan.user.error.UserErrorCode;
import com.manruhomerun.yadan.user.error.exception.UserException;
import com.manruhomerun.yadan.user.repository.UserRepository;
import com.manruhomerun.yadan.user.repository.TravelPreferenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelSpotService {

    private final TravelSpotRepository travelSpotRepository;
    private final DibsRepository dibsRepository;
    private final UserRepository userRepository;
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final ExternalApiClient externalApiClient;
    private final AiApiClient aiApiClient;

    public PopularTravelSpotResponse getTravelSpotSuggestions(String userId, TravelSpotSuggestionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        TravelPreference travelPreference = travelPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.TRAVEL_PREFERENCE_NOT_FOUND));
        Set<CompanionCondition> companionConditions = request.companionConditions() == null
                ? Set.of()
                : new HashSet<>(request.companionConditions());
        List<String> contentIdSequence = request.travelSpotIdList() == null
                ? List.of()
                : request.travelSpotIdList().stream().map(String::valueOf).toList();

        // 추천 요청의 장소 ID와 동행 조건을 AI 서버가 요구하는 자료형으로 변환한다.
        AiTravelSpotRecommendRequest aiRequest = new AiTravelSpotRecommendRequest(
                String.valueOf((LocalDate.now(ZoneId.of("Asia/Seoul")).getYear()
                        - user.getBirthday().getYear() + 1) / 10 * 10),
                request.regionCode(),
                request.companionCount(),
                contentIdSequence,
                user.getGender().getDisplayName(),
                companionConditions.contains(CompanionCondition.CHILD) ? 1 : 0,
                companionConditions.contains(CompanionCondition.WHEELCHAIR) ? 1 : 0,
                companionConditions.contains(CompanionCondition.ELDERLY) ? 1 : 0,
                travelPreference.getPreferredRegionCodes().stream()
                        .map(preferredRegionCode -> preferredRegionCode.getCode())
                        .sorted()
                        .toList(),
                travelPreference.getResidenceRegionCode().getCode().substring(0, 2),
                String.valueOf(ChronoUnit.DAYS.between(request.from(), request.to())),
                request.theme(),
                String.valueOf(travelPreference.getTravelStyleValue())
        );

        AiTravelSpotRecommendResponse aiResponse = aiApiClient.recommendTravelSpots(
                aiRequest, AiTravelSpotRecommendResponse.class
        );
        if (aiResponse == null || aiResponse.recommendations() == null) {
            throw new ExternalApiCallException("AI 여행지 추천 응답에 recommendations가 없습니다.");
        }

        // AI 추천 순서를 유지하면서 인기 여행지 응답과 같은 상세 정보를 채운다.
        List<PopularTravelSpotResponse.ContentResponse> contents = aiResponse.recommendations().stream()
                .map(recommendation -> {
                    String contentId = recommendation.contentId();
                    Map<String, Object> queryParams = new LinkedHashMap<>();
                    queryParams.put("contentId", contentId);
                    TourApiDetailCommonResponse response = externalApiClient.get(
                            "/detailCommon2",
                            queryParams,
                            TourApiDetailCommonResponse.class
                    );
                    if (response == null
                            || response.response() == null
                            || response.response().body() == null
                            || response.response().body().items() == null
                            || response.response().body().items().item() == null
                            || response.response().body().items().item().isEmpty()) {
                        throw new TravelSpotException(
                                TravelSpotErrorCode.TRAVEL_SPOT_NOT_FOUND,
                                "여행지를 찾을 수 없습니다. contentId=" + contentId
                        );
                    }

                    TourApiDetailCommonResponse.Item item = response.response().body().items().item().getFirst();
                    String address = item.addr2() == null || item.addr2().isBlank()
                            ? item.addr1()
                            : item.addr1() + " " + item.addr2();
                    boolean dibs = dibsRepository.existsByUserIdAndTravelSpotId(userId, contentId);
                    return new PopularTravelSpotResponse.ContentResponse(
                            contentId,
                            address,
                            TravelSpotCategory.getDisplayNameByContentTypeId(Integer.valueOf(item.contenttypeid())),
                            item.firstimage() == null || item.firstimage().isBlank() ? null : item.firstimage(),
                            item.title(),
                            Integer.valueOf(item.lDongRegnCd() + item.lDongSignguCd()),
                            dibs
                    );
                })
                .toList();

        return new PopularTravelSpotResponse(contents);
    }

    public void createDibs(String userId, String contentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

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
                .orElseThrow(UserNotFoundException::new);

        // DELETE는 멱등적으로 처리해, 찜이 없어도 성공 응답을 반환한다.
        dibsRepository.deleteByUserIdAndTravelSpotId(userId, contentId);
    }

    @Transactional(readOnly = true)
    public PageResponse<TravelSpotDibsItemResponse> getDibs(
            String userId,
            TravelRegionCode regionCode,
            TravelSpotCategory category,
            int pageNumber,
            int pageSize
    ) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 기준 지역 코드의 뒤쪽 0을 제거한 prefix로 같은 지역 소속 여행지를 조회한다.
        String regionCodePrefix = regionCode.getCodePrefix();
        Page<Dibs> dibsPage = dibsRepository
                .findByUserIdAndTravelSpotRegionCodeStartingWithAndTravelSpotCategoryOrderByCreatedAtDescIdDesc(
                        userId,
                        regionCodePrefix,
                        category.getContentTypeId(),
                        PageRequest.of(pageNumber - 1, pageSize)
                );
        List<TravelSpotDibsItemResponse> contents = dibsPage.getContent().stream()
                .map(Dibs::getTravelSpot)
                .map(TravelSpotDibsItemResponse::from)
                .toList();

        return PageResponse.from(dibsPage, contents);
    }

    @Transactional(readOnly = true)
    public TravelSpotDetailResponse getSpotDetail(String spotId, String userId) {
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

        String contentId = item.contentid();
        boolean dibs = dibsRepository.existsByUserIdAndTravelSpotId(userId, contentId);

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
                item.overview(),
                dibs
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
