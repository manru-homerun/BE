package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
    List<TravelUser> findAllByUserId(String userId);
    Page<TravelUser> findAllByUserId(String userId, Pageable pageable);
    Optional<TravelUser> findByTravelIdAndUserId(String travelId, String userId);

    @Query("""
            select tu
            from TravelUser tu
            join fetch tu.travel t
            join fetch tu.user u
            where t.startDate = :startDate
            order by t.id, u.id
            """)
    List<TravelUser> findAllReminderTargetsByTravelStartDate(
            @Param("startDate") LocalDate startDate
    );

    void deleteAllByTravelId(String travelId);

    @Query("""
            SELECT COUNT(tu) > 0
            FROM TravelUser tu
            WHERE tu.user.id IN :userIds
              AND tu.travel.startDate <= :endDate
              AND tu.travel.endDate >= :startDate
            """)
    boolean existsOverlappingTravel(
            @Param("userIds") Collection<String> userIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
