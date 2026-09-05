package com.manruhomerun.yadan.travelspot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.travelspot.domain.entity.Dibs;

public interface DibsRepository extends JpaRepository<Dibs, Long> {

    boolean existsByUserIdAndTravelSpotId(String userId, String travelSpotId);

    void deleteByUserIdAndTravelSpotId(String userId, String travelSpotId);

    Page<Dibs> findByUserIdAndTravelSpotRegionCodeStartingWithOrderByCreatedAtDescIdDesc(
            String userId,
            String regionCodePrefix,
            Pageable pageable
    );
}
