package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelTravelSpotRepository extends JpaRepository<TravelTravelSpot, Long> {
    void deleteTravelTravelSpotsByTravel(Travel travel);
}
