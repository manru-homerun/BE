package com.manruhomerun.yadan.baseball.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;

public interface BaseballTeamRepository extends JpaRepository<BaseballTeam, Long> {
}
