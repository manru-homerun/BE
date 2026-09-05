package com.manruhomerun.yadan.travelcerti.repository;

import com.manruhomerun.yadan.travelcerti.domain.entity.TravelCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelCertificationRepository extends JpaRepository<TravelCertification, Long> {

    boolean existsByTravelUserIdAndTravelSpotId(Long travelUserId, Long travelSpotId);

    long countByTravelUserId(Long travelUserId);
}
