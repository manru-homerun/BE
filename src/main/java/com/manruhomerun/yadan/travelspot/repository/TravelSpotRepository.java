package com.manruhomerun.yadan.travelspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;

public interface TravelSpotRepository extends JpaRepository<TravelSpot, String> {
}
