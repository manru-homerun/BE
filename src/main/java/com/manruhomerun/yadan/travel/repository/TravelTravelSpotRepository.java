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
import java.util.Optional;

public interface TravelTravelSpotRepository extends JpaRepository<TravelTravelSpot, Long> {
    void deleteTravelTravelSpotsByTravel(Travel travel);

    @Query("""
            SELECT travelTravelSpot
            FROM TravelTravelSpot travelTravelSpot
            WHERE travelTravelSpot.travel.id = :travelId
            AND travelTravelSpot.travelSpot.id = :travelSpotId
            AND travelTravelSpot.day = :day
            AND travelTravelSpot.order = :placementOrder
            """)
    Optional<TravelTravelSpot> findByTravelSchedule(
            @Param("travelId") String travelId,
            @Param("travelSpotId") String travelSpotId,
            @Param("day") int day,
            @Param("placementOrder") int placementOrder
    );

    @Query("""
            SELECT travelTravelSpot.travelSpot
            FROM Travel travel
            JOIN travel.travelTravelSpotList travelTravelSpot
            WHERE (:regionCode IS NULL OR travel.regionCode = :regionCode)
            AND travelTravelSpot.travelSpot.category = :category
            AND travel.endDate >= :from
            GROUP BY travelTravelSpot.travelSpot
            ORDER BY COUNT(travel.id) DESC, MAX(travel.endDate) DESC
            """)
    List<TravelSpot> findPopularTravelSpotsByRegionCodeAndCategoryAndEndDateAfter(
            @Param("regionCode") String regionCode,
            @Param("category") Integer category,
            @Param("from") LocalDate from,
            Pageable pageable
    );

    @Query("""
            SELECT travelTravelSpot.travelSpot
            FROM Travel travel
            JOIN travel.travelTravelSpotList travelTravelSpot
            WHERE (:regionCode IS NULL OR travel.regionCode = :regionCode)
            AND travelTravelSpot.travelSpot.category = :category
            GROUP BY travelTravelSpot.travelSpot
            ORDER BY COUNT(travel.id) DESC, MAX(travel.endDate) DESC
            """)
    List<TravelSpot> findPopularTravelSpotsByRegionCodeAndCategory(
            @Param("regionCode") String regionCode,
            @Param("category") Integer category,
            Pageable pageable
    );
}
