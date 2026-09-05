package com.manruhomerun.yadan.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.user.domain.entity.TravelPreference;

public interface TravelPreferenceRepository extends JpaRepository<TravelPreference, Long> {

    Optional<TravelPreference> findByUserId(String userId);
}
