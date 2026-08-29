package com.manruhomerun.yadan.travel.service;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballStadium;
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
import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travel.dto.PopularTravelSpotResponse;
import com.manruhomerun.yadan.travelspot.repository.TravelSpotRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelService {
    private final BaseballGameRepository baseballGameRepository;
    private final TravelRepository travelRepository;
    private final TravelStickerRepository travelStickerRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelUserRepository travelUserRepository;
    private final TravelThemeRepository travelThemeRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final TravelSpotRepository travelSpotRepository;
    private final ExternalApiClient externalApiClient;

    //private final TravelSpotService travelSpotService;

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
        travel.setGameIdx(request.baseballGameAfterIdx());

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
                    .map(travel -> TravelListResponse.from(
                            travel,
                            userId,
                            travelStickerRepository.existsByTravelId(travel.getId())
                    ))
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
                .map(travel -> TravelListResponse.from(
                        travel,
                        userId,
                        travelStickerRepository.existsByTravelId(travel.getId())
                ))
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
                .map(ThemeListResponse::from)
                .toList();
    }

    public TravelAlignResponse getAlignedTravelList(TravelAlignRequest request) {
        Long baseballGameId = request.baseballGame().id();
        BaseballGame baseballGame = baseballGameRepository.findById(baseballGameId).orElseThrow(
                () -> new BaseballGameNotFoundException(
                        BaseballErrorCode.BASEBALL_GAME_NOT_FOUND,
                        "야구 경기를 찾을 수 없습니다. baseballGameId=" + baseballGameId
                )
        );
        int baseballGameDay = (int) ChronoUnit.DAYS.between(
                request.from(),
                baseballGame.getGameDate().toLocalDate()
        ) + 1;

        AtomicInteger baseballGameAfterIdx = new AtomicInteger(-1);
        List<TravelAlignResponse.ScheduleResponse> scheduleResponses = request.schedule().stream()
                .map(schedule -> alignSchedule(
                        schedule,
                        baseballGameDay,
                        baseballGame,
                        baseballGameAfterIdx
                ))
                .toList();

        return new TravelAlignResponse(
                new TravelAlignResponse.BaseballGameResponse(
                        request.baseballGame().id(),
                        baseballGameDay,
                        baseballGameAfterIdx.get()
                ),
                scheduleResponses
        );
    }

    private TravelAlignResponse.ScheduleResponse alignSchedule(
            TravelAlignRequest.ScheduleRequest scheduleRequest,
            int baseballGameDay,
            BaseballGame baseballGame,
            AtomicInteger baseballGameAfterIdx
    ) {
        List<TravelSpot> travelSpots = scheduleRequest.travelSpotIdList().stream()
                .map(this::getTravelSpotById)
                .toList();
        boolean hasBaseballGame = Objects.equals(scheduleRequest.day(), baseballGameDay);
        List<TravelSpot> routeTravelSpots = new ArrayList<>(travelSpots);
        if (hasBaseballGame) {
            // 야구장은 null 경유지로 두고 거리 계산 시 경기장 좌표를 사용한다.
            routeTravelSpots.add(null);
        }

        if (routeTravelSpots.isEmpty()) {
            return new TravelAlignResponse.ScheduleResponse(scheduleRequest.day(), List.of());
        }

        List<TravelSpot> optimizedRoute = optimizeRoute(routeTravelSpots, baseballGame.getStadium());
        int gameIdx = -1;
        List<TravelAlignResponse.TravelSpotResponse> orderedTravelSpots = new ArrayList<>();

        for (TravelSpot travelSpot : optimizedRoute) {
            if (travelSpot == null) {
                gameIdx = orderedTravelSpots.size() - 1;
                continue;
            }
            orderedTravelSpots.add(TravelAlignResponse.TravelSpotResponse.from(travelSpot));
        }

        if (hasBaseballGame) {
            baseballGameAfterIdx.set(gameIdx);
        }

        // 야구장 위치를 함께 정렬한 뒤, 경기 직전 여행지의 인덱스를 반환한다.
        return new TravelAlignResponse.ScheduleResponse(
                scheduleRequest.day(),
                orderedTravelSpots
        );
    }

    private List<TravelSpot> optimizeRoute(
            List<TravelSpot> routeTravelSpots,
            BaseballStadium baseballStadium
    ) {
        if (routeTravelSpots.size() <= 2) {
            return routeTravelSpots;
        }

        List<TravelSpot> bestRoute = null;
        double bestDistance = Double.MAX_VALUE;

        // 시작점을 모두 시도해서 가장 짧은 nearest neighbor 초기해를 선택한다.
        for (TravelSpot startTravelSpot : routeTravelSpots) {
            List<TravelSpot> initialRoute = buildNearestNeighborRoute(
                    routeTravelSpots,
                    startTravelSpot,
                    baseballStadium
            );
            List<TravelSpot> optimizedRoute = improveRouteWithTwoOpt(initialRoute, baseballStadium);
            double routeDistance = calculateRouteDistance(optimizedRoute, baseballStadium);

            if (routeDistance < bestDistance) {
                bestDistance = routeDistance;
                bestRoute = optimizedRoute;
            }
        }

        return bestRoute == null ? routeTravelSpots : bestRoute;
    }

    private List<TravelSpot> buildNearestNeighborRoute(
            List<TravelSpot> routeTravelSpots,
            TravelSpot startTravelSpot,
            BaseballStadium baseballStadium
    ) {
        List<TravelSpot> route = new ArrayList<>();
        List<TravelSpot> unvisitedTravelSpots = new ArrayList<>(routeTravelSpots);
        TravelSpot currentTravelSpot = startTravelSpot;

        route.add(currentTravelSpot);
        unvisitedTravelSpots.remove(currentTravelSpot);

        while (!unvisitedTravelSpots.isEmpty()) {
            TravelSpot baseTravelSpot = currentTravelSpot;
            TravelSpot nextTravelSpot = unvisitedTravelSpots.getFirst();

            for (TravelSpot candidateTravelSpot : unvisitedTravelSpots) {
                if (getDistance(baseTravelSpot, candidateTravelSpot, baseballStadium)
                        < getDistance(baseTravelSpot, nextTravelSpot, baseballStadium)) {
                    nextTravelSpot = candidateTravelSpot;
                }
            }

            route.add(nextTravelSpot);
            unvisitedTravelSpots.remove(nextTravelSpot);
            currentTravelSpot = nextTravelSpot;
        }

        return route;
    }

    private List<TravelSpot> improveRouteWithTwoOpt(
            List<TravelSpot> route,
            BaseballStadium baseballStadium
    ) {
        List<TravelSpot> optimizedRoute = new ArrayList<>(route);
        boolean improved = true;

        while (improved) {
            improved = false;
            for (int i = 1; i < optimizedRoute.size() - 1; i++) {
                for (int j = i + 1; j < optimizedRoute.size(); j++) {
                    List<TravelSpot> swappedRoute = twoOptSwap(optimizedRoute, i, j);
                    if (calculateRouteDistance(swappedRoute, baseballStadium)
                            < calculateRouteDistance(optimizedRoute, baseballStadium)) {
                        optimizedRoute = swappedRoute;
                        improved = true;
                    }
                }
            }
        }

        return optimizedRoute;
    }

    private List<TravelSpot> twoOptSwap(List<TravelSpot> route, int i, int j) {
        List<TravelSpot> swappedRoute = new ArrayList<>();
        swappedRoute.addAll(route.subList(0, i));

        List<TravelSpot> reversedSection = new ArrayList<>(route.subList(i, j + 1));
        Collections.reverse(reversedSection);
        swappedRoute.addAll(reversedSection);

        if (j + 1 < route.size()) {
            swappedRoute.addAll(route.subList(j + 1, route.size()));
        }

        return swappedRoute;
    }

    private double calculateRouteDistance(List<TravelSpot> route, BaseballStadium baseballStadium) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += getDistance(route.get(i), route.get(i + 1), baseballStadium);
        }
        return totalDistance;
    }

    private double getDistance(TravelSpot from, TravelSpot to, BaseballStadium baseballStadium) {
        double fromLatitude = from == null
                ? baseballStadium.getLatitude().doubleValue()
                : from.getLatitude().doubleValue();
        double fromLongitude = from == null
                ? baseballStadium.getLongitude().doubleValue()
                : from.getLongitude().doubleValue();
        double toLatitude = to == null
                ? baseballStadium.getLatitude().doubleValue()
                : to.getLatitude().doubleValue();
        double toLongitude = to == null
                ? baseballStadium.getLongitude().doubleValue()
                : to.getLongitude().doubleValue();

        double latitudeDifference = Math.toRadians(toLatitude - fromLatitude);
        double longitudeDifference = Math.toRadians(toLongitude - fromLongitude);
        fromLatitude = Math.toRadians(fromLatitude);
        toLatitude = Math.toRadians(toLatitude);

        // 위도·경도를 지구 표면의 직선거리로 환산한다.
        double haversine = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(fromLatitude) * Math.cos(toLatitude)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
        return 6_371_000 * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    public void generateTravelCourse(TravelGenerateRequest request){
        // AI 논의 후 작성 예정
    }

    public PopularTravelSpotResponse getPopularSpots(TravelRegionCode region){
        LocalDate oneWeekAgo = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(7);
        PageRequest limit = PageRequest.of(0, 5);
        List<TravelSpot> popularTravelSpots = travelTravelSpotRepository
                .findPopularTravelSpotsByRegionCodeAndEndDateAfter(region.getCode(), oneWeekAgo, limit);

        if (popularTravelSpots.isEmpty()) {
            popularTravelSpots = travelTravelSpotRepository
                    .findPopularTravelSpotsByRegionCode(region.getCode(), limit);
        }

        List<PopularTravelSpotResponse.ContentResponse> contents = popularTravelSpots.stream()
                .map(travelSpot -> {
                    Map<String, Object> queryParams = new LinkedHashMap<>();
                    queryParams.put("contentId", travelSpot.getId());
                    TourApiDetailCommonResponse response = externalApiClient.get(
                            "/detailCommon2",
                            queryParams,
                            TourApiDetailCommonResponse.class
                    );
                    TourApiDetailCommonResponse.Item item = response.response().body().items().item().getFirst();
                    String address = item.addr2() == null || item.addr2().isBlank()
                            ? item.addr1()
                            : item.addr1() + " " + item.addr2();
                    return PopularTravelSpotResponse.ContentResponse.from(travelSpot, address);
                })
                .toList();

        return new PopularTravelSpotResponse(contents);

    }
}
