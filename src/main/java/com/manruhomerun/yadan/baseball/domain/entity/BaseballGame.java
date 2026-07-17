package com.manruhomerun.yadan.baseball.domain.entity;

import java.time.LocalDateTime;

import com.manruhomerun.yadan.baseball.domain.enums.BaseballGameType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "baseball_game")
public class BaseballGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stadium_id", nullable = false)
    private BaseballStadium stadium;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private BaseballTeam homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private BaseballTeam awayTeam;

    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;

    @Column(name = "away_team_score")
    private Integer awayTeamScore;

    @Column(name = "home_team_score")
    private Integer homeTeamScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private BaseballGameType gameType;

    @Column(name = "is_canceled")
    private Boolean isCanceled;
}
