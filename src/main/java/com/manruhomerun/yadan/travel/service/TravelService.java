package com.manruhomerun.yadan.travel.service;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballStadium;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballGameNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.friend.error.FriendErrorCode;
import com.manruhomerun.yadan.friend.error.exception.FriendException;
import com.manruhomerun.yadan.friend.repository.FriendRepository;
import com.manruhomerun.yadan.global.client.ExternalApiClient;
import com.manruhomerun.yadan.global.client.AiApiClient;
import com.manruhomerun.yadan.global.dto.PageResponse;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.domain.entity.*;
import com.manruhomerun.yadan.travel.domain.enums.TravelStatus;
import com.manruhomerun.yadan.travel.domain.enums.CompanionCondition;
import com.manruhomerun.yadan.travel.dto.*;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;
import com.manruhomerun.yadan.travel.error.exception.ThemeNotFoundException;
import com.manruhomerun.yadan.travel.error.exception.TravelNotFoundException;
import com.manruhomerun.yadan.travel.error.exception.TravelScheduleOverlapException;
import com.manruhomerun.yadan.travel.repository.*;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.travelspot.domain.enums.TravelSpotCategory;
import com.manruhomerun.yadan.travelspot.dto.TourApiDetailCommonResponse;
import com.manruhomerun.yadan.travel.dto.PopularTravelSpotResponse;
import com.manruhomerun.yadan.travelspot.repository.DibsRepository;
import com.manruhomerun.yadan.travelspot.repository.TravelSpotRepository;
import com.manruhomerun.yadan.travelcerti.domain.entity.TravelCertification;
import com.manruhomerun.yadan.travelcerti.repository.TravelCertificationRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import com.manruhomerun.yadan.user.error.UserErrorCode;
import com.manruhomerun.yadan.user.error.exception.UserException;
import com.manruhomerun.yadan.user.repository.UserRepository;
import com.manruhomerun.yadan.user.repository.TravelPreferenceRepository;
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
    private final TravelCertificationRepository travelCertificationRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelUserRepository travelUserRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final TravelSpotRepository travelSpotRepository;
    private final DibsRepository dibsRepository;
    private final ExternalApiClient externalApiClient;
    private final AiApiClient aiApiClient;

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

        // 친구 목록에 유효하지 않는 사용자 있는 경우 걸러내기용
        Set<String> friendIds = request.friends() == null ? Set.of() : new HashSet<>(request.friends());
        List<User> friends = userRepository.findAllById(friendIds);

        if (friends.size() != friendIds.size()) {
            throw new UserNotFoundException();
        }

        // 요청한 모든 동행자가 방장과 실제 친구 관계인지 확인한다.
        if (!friendIds.isEmpty() && friendRepository.findAllBetweenCurrentUserAndTargets(
                userId, new ArrayList<>(friendIds)).size() != friendIds.size()) {
            throw new FriendException(FriendErrorCode.FRIEND_NOT_FOUND);
        }

        Theme theme = themeRepository.findById(request.theme()).orElseThrow(
                () -> new ThemeNotFoundException(
                        TravelErrorCode.THEME_NOT_FOUND,
                        "여행 테마를 찾을 수 없습니다. themeId=" + request.theme()
                )
        );

        // 시작일과 종료일을 포함해 방장·동행자 중 한 명이라도 기존 여행과 겹치면 생성을 차단한다.
        Set<String> participantIds = new HashSet<>(friendIds);
        participantIds.add(userId);
        if (travelUserRepository.existsOverlappingTravel(participantIds, request.from(), request.to())) {
            throw new TravelScheduleOverlapException();
        }

        Travel travel = Travel.builder()
                .startDate(request.from())
                .endDate(request.to())
                .name(request.name())
                .gameIdx(request.baseballGame().baseballGameAfterIdx())
                .baseballGame(baseballGame)
                .regionCode(request.regionCode())
                .theme(theme)
                .build();
        travelRepository.save(travel);

        // 사용자와의 연관관계 저장
        friends.stream().map(
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

    public void deleteTravel(String travelId, String userId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(
                () -> new TravelNotFoundException(TravelErrorCode.TRAVEL_NOT_FOUND, "여행을 찾을 수 없습니다. travelId=" + travelId));
        TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(UserNotFoundException::new);

        // 참조 중인 인증·스티커 이력을 먼저 삭제한 후 참여 정보와 일정을 삭제한다.
        if (travelUser.isLeader()) {
            travelCertificationRepository.deleteAllByTravelUserTravelId(travelId);
            travelStickerRepository.deleteAllByTravelUserTravelId(travelId);
            travelUserRepository.deleteAllByTravelId(travelId);
            travelTravelSpotRepository.deleteTravelTravelSpotsByTravel(travel);
            travelRepository.delete(travel);
        } else {
            // 동행자가 나갈 때는 해당 사용자의 이력과 참여 정보만 삭제한다.
            travelCertificationRepository.deleteAllByTravelUserId(travelUser.getId());
            travelStickerRepository.deleteAllByTravelUserId(travelUser.getId());
            travelUserRepository.delete(travelUser);
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
                    .map(travelUser -> TravelListResponse.from(
                            travelUser.getTravel(),
                            userId,
                            travelStickerRepository.existsByTravelUserId(travelUser.getId()),
                            travelCertificationRepository.countVerifiedSpotsByTravelUserId(travelUser.getId())
                    ))
                    .toList();

            return PageResponse.from(page, contents);
        }

        List<TravelListResponse> filteredTravels = travelUserRepository.findAllByUserId(userId).stream()
                .filter(travelUser -> switch (status) {
                    case PLANNING -> travelUser.getTravel().getStartDate().isAfter(today);
                    case IN_PROGRESS -> !travelUser.getTravel().getStartDate().isAfter(today)
                            && !travelUser.getTravel().getEndDate().isBefore(today);
                    case COMPLETED -> travelUser.getTravel().getEndDate().isBefore(today);
                })
                .sorted(Comparator.comparing(
                                (TravelUser travelUser) -> travelUser.getTravel().getStartDate()
                        ).reversed().thenComparing(
                                travelUser -> travelUser.getTravel().getId(),
                                Comparator.reverseOrder()
                        ))
                .map(travelUser -> TravelListResponse.from(
                        travelUser.getTravel(),
                        userId,
                        travelStickerRepository.existsByTravelUserId(travelUser.getId()),
                        travelCertificationRepository.countVerifiedSpotsByTravelUserId(travelUser.getId())
                ))
                .toList();

        int start = Math.min((validatedPageNumber - 1) * pageRequest.getPageSize(), filteredTravels.size());
        int end = Math.min(start + pageRequest.getPageSize(), filteredTravels.size());
        List<TravelListResponse> contents = filteredTravels.subList(start, end);
        PageImpl<TravelListResponse> page = new PageImpl<>(contents, pageRequest, filteredTravels.size());

        return PageResponse.from(page, contents);
    }

    public TravelDetailResponse getTravelById(String travelId, String userId) {
        Travel travel = travelRepository.findById(travelId).orElseThrow(
                () -> new TravelNotFoundException(TravelErrorCode.TRAVEL_NOT_FOUND, "여행을 찾을 수 없습니다. travelId=" + travelId));
        TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(UserNotFoundException::new);
        List<TravelCertification> travelCertifications = travelCertificationRepository
                .findAllByTravelUserId(travelUser.getId());
        Set<Long> vertifiedTravelSpotMappingIds = travelCertifications
                .stream()
                .map(certification -> certification.getTravelSpot().getId())
                .collect(java.util.stream.Collectors.toSet());
        long vertifiedSpotsCnt = travelCertifications.stream()
                .map(certification -> certification.getTravelSpot().getTravelSpot().getId())
                .distinct()
                .count();

        return TravelDetailResponse.from(
                travel,
                userId,
                vertifiedTravelSpotMappingIds,
                vertifiedSpotsCnt
        );
    }

    public List<ThemeListResponse> getTravelThemeList() {
        return themeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
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

    public TravelAlignResponse generateTravelCourse(String userId, TravelGenerateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Set<String> friendIds = request.friends() == null ? Set.of() : new HashSet<>(request.friends());
        // 코스를 생성하기 전에 요청한 동행자 모두와의 친구 관계를 확인한다.
        if (!friendIds.isEmpty() && friendRepository.findAllBetweenCurrentUserAndTargets(
                userId, new ArrayList<>(friendIds)).size() != friendIds.size()) {
            throw new FriendException(FriendErrorCode.FRIEND_NOT_FOUND);
        }
        TravelPreference travelPreference = travelPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.TRAVEL_PREFERENCE_NOT_FOUND));

        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int koreanAge = currentDate.getYear() - user.getBirthday().getYear() + 1;
        String travelStyleValue = String.valueOf(travelPreference.getTravelStyleValue());
        String travelPersona = String.valueOf(request.theme());
        Set<CompanionCondition> companionConditions = request.companionConditions() == null
                ? Set.of()
                : new HashSet<>(request.companionConditions());

        // AI 서버 명세에 맞춰 사용자 취향과 여행 생성 입력을 하나의 요청으로 조합한다.
        AiTravelGenerateRequest aiRequest = new AiTravelGenerateRequest(
                request.regionCode(),
                String.valueOf(ChronoUnit.DAYS.between(request.from(), request.to()) + 1),
                travelPersona,
                String.valueOf(koreanAge / 10 * 10),
                user.getGender().getDisplayName(),
                travelStyleValue,
                travelPreference.getPreferredRegionCodes().stream()
                        .map(preferredRegionCode -> preferredRegionCode.getCode())
                        .sorted()
                        .toList(),
                travelPreference.getResidenceRegionCode().getCode(),
                companionConditions.contains(CompanionCondition.CHILD),
                companionConditions.contains(CompanionCondition.ELDERLY),
                companionConditions.contains(CompanionCondition.WHEELCHAIR),
                request.friends() == null ? 0 : request.friends().size(),
                request.travelSpotIdList()
        );

        AiTravelGenerateResponse aiResponse = aiApiClient.generateTravel(aiRequest, AiTravelGenerateResponse.class);

        // AI의 일차·방문 순서를 정렬 API 입력 형식으로 옮긴다.
        long travelDuration = ChronoUnit.DAYS.between(request.from(), request.to()) + 1;
        List<TravelAlignRequest.ScheduleRequest> schedules = new ArrayList<>();
        for (int day = 1; day <= travelDuration; day++) {
            int currentDay = day;
            List<String> travelSpotIds = aiResponse.steps().stream()
                    .filter(step -> step.dayIndex() == currentDay)
                    .sorted(Comparator.comparingInt(AiTravelGenerateResponse.StepResponse::slotIndex))
                    .map(AiTravelGenerateResponse.StepResponse::contentId)
                    .toList();
            schedules.add(new TravelAlignRequest.ScheduleRequest(day, travelSpotIds));
        }

        return getAlignedTravelList(new TravelAlignRequest(
                request.from(),
                request.to(),
                new TravelAlignRequest.BaseballGameRequest(request.baseballGameId()),
                schedules
        ));
    }

    public PopularTravelSpotResponse getPopularSpots(
            TravelRegionCode region,
            TravelSpotCategory category,
            String userId
    ){
        LocalDate oneWeekAgo = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(7);
        PageRequest limit = PageRequest.of(0, 5);
        Integer categoryId = category == null ? null : category.getContentTypeId();
        List<TravelSpot> popularTravelSpots = travelTravelSpotRepository
                .findPopularTravelSpotsByRegionCodeAndCategoryAndEndDateAfter(
                        region.getCode(),
                        categoryId,
                        oneWeekAgo,
                        limit
                );

        if (popularTravelSpots.isEmpty()) {
            popularTravelSpots = travelTravelSpotRepository
                    .findPopularTravelSpotsByRegionCodeAndCategory(
                            region.getCode(),
                            categoryId,
                            limit
                    );
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
                    boolean dibs = dibsRepository.existsByUserIdAndTravelSpotId(userId, travelSpot.getId());
                    return PopularTravelSpotResponse.ContentResponse.from(travelSpot, address, dibs);
                })
                .toList();

        return new PopularTravelSpotResponse(contents);

    }
}
