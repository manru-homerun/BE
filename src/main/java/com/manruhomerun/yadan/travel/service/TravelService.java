package com.manruhomerun.yadan.travel.service;

import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travel.domain.entity.Theme;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.dto.ThemeListResponse;
import com.manruhomerun.yadan.travel.dto.TravelDetailResponse;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;
import com.manruhomerun.yadan.travel.error.exception.TravelNotFoundException;
import com.manruhomerun.yadan.travel.repository.ThemeRepository;
import com.manruhomerun.yadan.travel.repository.TravelRepository;
import com.manruhomerun.yadan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final BaseballGameRepository baseballGameRepository;
    private final TravelRepository travelRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public void creatTravel() {

    }

    public void updateTravel() {

    }

    public List<TravelDetailResponse> getTravelList(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        List<Travel> travelList = travelRepository.findAllByUserId(userId);

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
