package com.manruhomerun.yadan.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.friend.domain.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 친구 목록 조회
    // TODO N+1 방지를 위해 @EntityGraph 적용 검토
    @Query("""
            SELECT f
            FROM Friend f
            WHERE f.firstUser.id = :userId OR f.secondUser.id = :userId
            ORDER BY f.createdAt DESC
            """)
    List<Friend> findAllByUserId(@Param("userId") String userId);

    // 친구 단건 조회
    @Query("""
            SELECT f
            FROM Friend f
            WHERE f.id = :friendId
              AND (f.firstUser.id = :userId OR f.secondUser.id = :userId)
            """)
    Optional<Friend> findByIdAndUserId(
            @Param("friendId") Long friendId,
            @Param("userId") String userId
    );

    // 사용자 쌍 조회
    Optional<Friend> findByFirstUserIdAndSecondUserId(
            String firstUserId,
            String secondUserId
    );

    // 친구 여부 확인
    boolean existsByFirstUserIdAndSecondUserId(
            String firstUserId,
            String secondUserId
    );
}
