package com.manruhomerun.yadan.travelspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.travelspot.domain.entity.Dibs;

public interface DibsRepository extends JpaRepository<Dibs, Long> {

    boolean existsByUserIdAndTravelSpotId(String userId, String travelSpotId);
}
