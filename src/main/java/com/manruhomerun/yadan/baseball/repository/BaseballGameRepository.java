package com.manruhomerun.yadan.baseball.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;

public interface BaseballGameRepository extends JpaRepository<BaseballGame, Long> {

    Page<BaseballGame> findByStadiumIdAndGameDateGreaterThanEqualOrderByGameDateAscIdAsc(
            Long stadiumId,
            LocalDateTime baselineDateTime,
            Pageable pageable
    );

    @Query("""
        SELECT bg
    
        FROM BaseballGame bg
    
        WHERE (bg.homeTeam.id = :teamId OR bg.awayTeam.id = :teamId)
    
          AND bg.gameDate >= :gameDate
    
        ORDER BY bg.gameDate ASC, bg.id ASC
    """)
    Page<BaseballGame> findUpcomingGamesByTeamId(
            Long teamId,
            LocalDateTime baselineDateTime,
            Pageable pageable
    );

    @Query("""
        SELECT bg

        FROM BaseballGame bg

        WHERE bg.homeTeam.id = :homeTeamId

          AND bg.awayTeam.id = :awayTeamId

          AND bg.gameDate BETWEEN :startDateTime AND :endDateTime

        ORDER BY bg.gameDate ASC, bg.id ASC
    """)
    List<BaseballGame> findByHomeTeamIdAndAwayTeamIdAndGameDateBetween(
            @Param("homeTeamId") Long homeTeamId,
            @Param("awayTeamId") Long awayTeamId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
