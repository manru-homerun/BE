package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TravelTravelSpotRepository extends JpaRepository<TravelTravelSpot, Long> {
    void deleteTravelTravelSpotsByTravel(Travel travel);

    @Query("""
            SELECT travelTravelSpot.travelSpot
            FROM Travel travel
            JOIN travel.travelTravelSpotList travelTravelSpot
            WHERE travel.regionCode = :regionCode
            AND travel.endDate >= :from
            GROUP BY travelTravelSpot.travelSpot
            ORDER BY COUNT(travel.id) DESC, MAX(travel.endDate) DESC
            """)
    List<TravelSpot> findPopularTravelSpotsByRegionCodeAndEndDateAfter(
            @Param("regionCode") String regionCode,
            @Param("from") LocalDate from,
            Pageable pageable
    );

    @Query("""
            SELECT travelTravelSpot.travelSpot
            FROM Travel travel
            JOIN travel.travelTravelSpotList travelTravelSpot
            WHERE travel.regionCode = :regionCode
            GROUP BY travelTravelSpot.travelSpot
            ORDER BY COUNT(travel.id) DESC, MAX(travel.endDate) DESC
            """)
    List<TravelSpot> findPopularTravelSpotsByRegionCode(
            @Param("regionCode") String regionCode,
            Pageable pageable
    );
}
