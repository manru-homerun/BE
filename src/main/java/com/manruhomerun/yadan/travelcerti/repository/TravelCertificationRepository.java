package com.manruhomerun.yadan.travelcerti.repository;

import java.util.List;

import com.manruhomerun.yadan.travelcerti.domain.entity.TravelCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelCertificationRepository extends JpaRepository<TravelCertification, Long> {

    boolean existsByTravelUserIdAndTravelSpotId(Long travelUserId, Long travelSpotId);

    @Query("""
            SELECT COUNT(DISTINCT certification.travelSpot.travelSpot.id)
            FROM TravelCertification certification
            WHERE certification.travelUser.id = :travelUserId
            """)
    long countVerifiedSpotsByTravelUserId(@Param("travelUserId") Long travelUserId);

    List<TravelCertification> findAllByTravelUserId(Long travelUserId);
}
