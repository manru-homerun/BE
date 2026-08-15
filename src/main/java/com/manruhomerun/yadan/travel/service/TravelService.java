package com.manruhomerun.yadan.travel.service;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballGameNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.global.client.ExternalApiClient;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.domain.entity.*;
import com.manruhomerun.yadan.travel.domain.enums.TravelStatus;
import com.manruhomerun.yadan.travel.dto.*;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;
import com.manruhomerun.yadan.travel.error.exception.TravelNotFoundException;
import com.manruhomerun.yadan.travel.repository.*;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travelspot.repository.TravelSpotRepository;
import com.manruhomerun.yadan.travelspot.service.TravelSpotService;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelService {
    // repository
    private final BaseballGameRepository baseballGameRepository;
    private final TravelRepository travelRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelUserRepository travelUserRepository;
    private final TravelThemeRepository travelThemeRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final TravelSpotRepository travelSpotRepository;
    private final ExternalApiClient externalApiClient;

    // service
    private final TravelSpotService travelSpotService;

    public TravelSpot getTravelSpotById(String travelSpotId) {
        TravelSpot travelSpot = travelSpotRepository.findById(travelSpotId)
            .orElseGet(() -> {
                Map<String, Object> queryParams = new LinkedHashMap<>();
                queryParams.put("contentId", travelSpotId);
                TourApiDetailCommonResponse response = externalApiClient.get(
                        "/detailCommon2",
                        queryParams,
                        TourApiDetailCommonResponse.class
                );

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
        return travelSpot;
    }



    public void createTravel(String userId, TravelCreateRequest request) {
        Long baseballGameId = request.baseballGame().id();
        BaseballGame baseballGame = baseballGameRepository.findById(baseballGameId).orElseThrow(
                () -> new BaseballGameNotFoundException(BaseballErrorCode.BASEBALL_GAME_NOT_FOUND, "야구 경기를 찾을 수 없습니다. baseballGameId=" + baseballGameId)
        );

        // TODO: UserNotFoundException으로 바꾸기? 암튼 확인해봐야 함
        User leader = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Travel travel = Travel.builder()
                .startDate(request.from())
                .endDate(request.to())
                .name(request.name())
                .gameIdx(request.baseballGame().baseballGameAfterIdx())
                .baseballGame(baseballGame)
                .regionCode(request.regionCode())
                .build();
        travelRepository.save(travel);

        // 사용자와의 연관관계 저장
        userRepository.findAllById(request.friends())
                .stream().map(
                        user -> TravelUser.builder()
                                .travel(travel)
                                .user(user)
                                .build()
                )
                .forEach(travelUserRepository::save);

        // 방장인 사용자와의 연관관계 저장
        travelUserRepository.save(TravelUser.builder()
                .travel(travel)
                .user(leader)
                .isLeader(true)
                .build());

        // 여행 테마와의 연관관계 저장
        themeRepository.findAllById(request.theme())
                .stream().map(
                theme -> TravelTheme.builder()
                        .travel(travel)
                        .theme(theme)
                        .build()
        ).forEach(travelThemeRepository::save);

        // 관광지와의 연관관계 저장
        for(TravelCreateRequest.ScheduleRequest schedule : request.schedule()) {
            List<String> travelSpotIds = schedule.travelSpotIdList();
            AtomicInteger order = new AtomicInteger(1);
            for(String travelSpotId : travelSpotIds) {

                TravelSpot travelSpot = getTravelSpotById(travelSpotId);
                TravelTravelSpot travelTravelSpot = TravelTravelSpot.builder()
                        .travel(travel)
                        .travelSpot(travelSpot)
                        .day(schedule.day())
                        .order(order.getAndIncrement())
                        .build();
                travelTravelSpotRepository.save(travelTravelSpot);
            }
        }
    }

    public void updateTravel(String travelId, String userId, TravelModifyRequest request) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(
                () -> new TravelNotFoundException(TravelErrorCode.TRAVEL_NOT_FOUND, "여행을 찾을 수 없습니다. travelId=" + travelId));
        TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(UserNotFoundException::new);

        if (!travelUser.isLeader()) {
            throw new IllegalArgumentException("여행 수정 권한이 없습니다. userId=" + userId);
        }

        // 여행 정보 수정
        travel.setName(request.name());
        travel.setGameIdx(request.gameIdx());

        travelTravelSpotRepository.deleteTravelTravelSpotsByTravel(travel);
        for (TravelModifyRequest.ScheduleRequest schedule : request.schedule()) {
            List<String> travelSpotIds = schedule.travelSpotIdList();
            AtomicInteger order = new AtomicInteger(1);
            for(String travelSpotId : travelSpotIds) {

                TravelSpot travelSpot = getTravelSpotById(travelSpotId);
                TravelTravelSpot travelTravelSpot = TravelTravelSpot.builder()
                        .travel(travel)
                        .travelSpot(travelSpot)
                        .day(schedule.day())
                        .order(order.getAndIncrement())
                        .build();
                travelTravelSpotRepository.save(travelTravelSpot);
            }
        }
    }

    public PageResponse<TravelListResponse> getTravelList(String userId, TravelStatus status, int pageNumber, int pageSize) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int validatedPageNumber = Math.max(pageNumber, 1);
        int validatedPageSize = Math.max(pageSize, 1);
        PageRequest pageRequest = PageRequest.of(
                validatedPageNumber - 1,
                validatedPageSize,
                Sort.by(Sort.Order.desc("travel.startDate"), Sort.Order.desc("travel.id"))
        );

        if (status == null) {
            Page<TravelUser> page = travelUserRepository.findAllByUserId(userId, pageRequest);
            List<TravelListResponse> contents = page.getContent().stream()
                    .map(TravelUser::getTravel)
                    .map(travel -> TravelListResponse.from(travel, userId))
                    .toList();

            return PageResponse.from(page, contents);
        }

        List<TravelListResponse> filteredTravels = travelUserRepository.findAllByUserId(userId).stream()
                .map(TravelUser::getTravel)
                .filter(travel -> switch (status) {
                    case PLANNING -> travel.getStartDate().isAfter(today);
                    case IN_PROGRESS -> !travel.getStartDate().isAfter(today)
                            && !travel.getEndDate().isBefore(today);
                    case COMPLETED -> travel.getEndDate().isBefore(today);
                })
                .sorted(Comparator.comparing(Travel::getStartDate).reversed()
                        .thenComparing(Travel::getId, Comparator.reverseOrder()))
                .map(travel -> TravelListResponse.from(travel, userId))
                .toList();

        int start = Math.min((validatedPageNumber - 1) * pageRequest.getPageSize(), filteredTravels.size());
        int end = Math.min(start + pageRequest.getPageSize(), filteredTravels.size());
        List<TravelListResponse> contents = filteredTravels.subList(start, end);
        PageImpl<TravelListResponse> page = new PageImpl<>(contents, pageRequest, filteredTravels.size());

        return PageResponse.from(page, contents);
    }

    public TravelDetailResponse getTravelById(String travelId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(
                () -> new TravelNotFoundException(TravelErrorCode.TRAVEL_NOT_FOUND, "여행을 찾을 수 없습니다. travelId=" + travelId));
        return TravelDetailResponse.from(travel);
    }

    public List<ThemeListResponse> getTravelThemeList() {
        List<Theme> themes = themeRepository.findAll();
        Collections.sort(themes, Comparator.comparingInt(Theme::getOrder));
        return themes.stream()
                .map(theme -> ThemeListResponse.from(theme))
                .toList();
    }
}
