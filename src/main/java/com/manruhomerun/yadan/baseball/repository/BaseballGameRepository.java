package com.manruhomerun.yadan.baseball.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;

public interface BaseballGameRepository extends JpaRepository<BaseballGame, Long> {

    Page<BaseballGame> findByStadiumIdAndGameDateGreaterThanEqualOrderByGameDateAscIdAsc(
            Long stadiumId,
            LocalDateTime baselineDateTime,
            Pageable pageable
    );

    Page<BaseballGame> findByHomeTeamIdOrAwayTeamIdAndGameDateGreaterThanEqualOrderByGameDateAscIdAsc(
            Long homeTeamId,
            Long awayTeamId,
            LocalDateTime baselineDateTime,
            Pageable pageable
    );
}
