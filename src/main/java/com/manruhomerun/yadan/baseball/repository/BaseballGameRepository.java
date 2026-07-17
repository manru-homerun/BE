package com.manruhomerun.yadan.baseball.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import org.springframework.data.jpa.repository.Query;

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
            Long homeTeamId,
            Long awayTeamId,
            LocalDateTime baselineDateTime,
            Pageable pageable
    );
}
