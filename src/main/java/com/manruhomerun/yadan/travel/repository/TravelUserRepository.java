package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
    List<TravelUser> findAllByUserId(String userId);
    Page<TravelUser> findAllByUserId(String userId, Pageable pageable);
}
