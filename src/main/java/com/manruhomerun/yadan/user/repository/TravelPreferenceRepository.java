package com.manruhomerun.yadan.user.repository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.user.domain.entity.TravelPreference;

public interface TravelPreferenceRepository extends JpaRepository<TravelPreference, Long> {

    Optional<TravelPreference> findByUserId(String userId);

    @EntityGraph(attributePaths = {"user", "preferredRegionCodes"})
    List<TravelPreference> findAllByUserIdIn(Collection<String> userIds);
}
