package com.manruhomerun.yadan.baseball.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.dto.BaseballGameDetailResponse;
import com.manruhomerun.yadan.baseball.dto.BaseballGameScheduleItemResponse;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballResourceNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import com.manruhomerun.yadan.baseball.repository.BaseballStadiumRepository;
import com.manruhomerun.yadan.baseball.repository.BaseballTeamRepository;
import com.manruhomerun.yadan.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BaseballService {

    private final BaseballGameRepository baseballGameRepository;
    private final BaseballStadiumRepository baseballStadiumRepository;
    private final BaseballTeamRepository baseballTeamRepository;

    public BaseballGameDetailResponse getGameDetail(Long gameId) {
        BaseballGame game = baseballGameRepository.findById(gameId)
                .orElseThrow(() -> new BaseballResourceNotFoundException(
                        BaseballErrorCode.BASEBALL_GAME_NOT_FOUND,
                        "경기를 찾을 수 없습니다. gameId=" + gameId
                ));

        return BaseballGameDetailResponse.from(game);
    }

    public PageResponse<BaseballGameScheduleItemResponse> getStadiumGameSchedules(Long stadiumId, LocalDate baselineDate, int pageNumber) {
        if (!baseballStadiumRepository.existsById(stadiumId)) {
            throw new BaseballResourceNotFoundException(
                    BaseballErrorCode.BASEBALL_STADIUM_NOT_FOUND,
                    "구장을 찾을 수 없습니다. stadiumId=" + stadiumId
            );
        }

        int validatedPageNumber = Math.max(pageNumber, 1);
        PageRequest pageRequest = PageRequest.of(
                validatedPageNumber - 1,
                6,
                Sort.by(Sort.Order.asc("gameDate"), Sort.Order.asc("id"))
        );

        Page<BaseballGame> page = baseballGameRepository.findByStadiumIdAndGameDateGreaterThanEqualOrderByGameDateAscIdAsc(
                stadiumId,
                (baselineDate == null ? LocalDate.now() : baselineDate).atStartOfDay(),
                pageRequest
        );

        return PageResponse.from(
                page,
                page.getContent().stream()
                        .map(BaseballGameScheduleItemResponse::from)
                        .toList()
        );
    }

    public PageResponse<BaseballGameScheduleItemResponse> getTeamGameSchedules(Long teamId, LocalDate baselineDate, int pageNumber) {
        if (!baseballTeamRepository.existsById(teamId)) {
            throw new BaseballResourceNotFoundException(
                    BaseballErrorCode.BASEBALL_TEAM_NOT_FOUND,
                    "팀을 찾을 수 없습니다. teamId=" + teamId
            );
        }

        int validatedPageNumber = Math.max(pageNumber, 1);
        PageRequest pageRequest = PageRequest.of(
                validatedPageNumber - 1,
                6,
                Sort.by(Sort.Order.asc("gameDate"), Sort.Order.asc("id"))
        );

        Page<BaseballGame> page = baseballGameRepository.findUpcomingGamesByTeamId(
                teamId,
                (baselineDate == null ? LocalDate.now() : baselineDate).atStartOfDay(),
                pageRequest
        );

        return PageResponse.from(
                page,
                page.getContent().stream()
                        .map(BaseballGameScheduleItemResponse::from)
                        .toList()
        );
    }
}
