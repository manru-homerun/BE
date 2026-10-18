package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.Travel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, String> {
    @EntityGraph(attributePaths = {"theme", "travelUserList.user"})
    List<Travel> findTravelByEndDate(LocalDate endDate);
}
