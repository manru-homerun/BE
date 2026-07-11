package com.manruhomerun.yadan.baseball.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballStadium;

public interface BaseballStadiumRepository extends JpaRepository<BaseballStadium, Long> {
}
