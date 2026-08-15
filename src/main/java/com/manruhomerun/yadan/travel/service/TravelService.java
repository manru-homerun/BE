package com.manruhomerun.yadan.travel.service;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballGameNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.domain.entity.*;
import com.manruhomerun.yadan.travel.dto.TravelCreateRequest;
import com.manruhomerun.yadan.travel.dto.ThemeListResponse;
import com.manruhomerun.yadan.travel.dto.TravelDetailResponse;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;
import com.manruhomerun.yadan.travel.error.exception.TravelNotFoundException;
import com.manruhomerun.yadan.travel.repository.*;
import com.manruhomerun.yadan.travelspot.service.TravelSpotService;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelService {
    private final BaseballGameRepository baseballGameRepository;
    private final TravelRepository travelRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelUserRepository travelUserRepository;
    private final TravelThemeRepository travelThemeRepository;
    private final TravelSpotService travelSpotService;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public void createTravel(String userId, TravelCreateRequest request) {
        Long baseballGameId = request.baseballGame().id();
        BaseballGame baseballGame = baseballGameRepository.findById(baseballGameId).orElseThrow(
                () -> new BaseballGameNotFoundException(BaseballErrorCode.BASEBALL_GAME_NOT_FOUND, "야구 경기를 찾을 수 없습니다. baseballGameId=" + baseballGameId)
        );

        User leader = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("사용자가 존재하지 않습니다.")
        ); // TODO: UserNotFoundException으로 바꾸기

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
                .forEach(
                        travelUser -> travelUserRepository.save(travelUser)
                );

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
        ).forEach(
                travelTheme -> travelThemeRepository.save(travelTheme)
        );

        // 관광지와의 연관관계 저장
        for(TravelCreateRequest.ScheduleRequest schedule : request.schedule()) {
            List<String> travelSpotIds = schedule.travelSpotIdList();
            travelSpotService.getTravelSpotListByIds(travelSpotIds)
                    .stream().map(
                            travelSpot -> TravelTravelSpot.builder()
                                    .travel(travel)
                                    .travelSpot(travelSpot)
                                    .day(schedule.day())
                                    .build()
                    )
                    .forEach(
                            travelTravelSpot ->
                                    travelTravelSpotRepository.save(travelTravelSpot));
        }
    }

    public void updateTravel() {

    }

    public List<TravelDetailResponse> getTravelList(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        List<Travel> travelList = travelUserRepository.findAllByUserId(userId)
                .stream()
                .map(TravelUser::getTravel)
                .toList();

        return travelList.stream()
                .map(TravelDetailResponse::from)
                .toList();
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
