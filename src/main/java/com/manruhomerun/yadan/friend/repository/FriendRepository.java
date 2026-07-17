package com.manruhomerun.yadan.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.friend.domain.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
            SELECT f
            FROM Friend f
            WHERE f.user.id = :userId OR f.friendUser.id = :userId
            ORDER BY f.createdAt DESC
            """)
    List<Friend> findAllByUserId(@Param("userId") String userId);

    @Query("""
            SELECT f
            FROM Friend f
            WHERE (f.user.id = :userId AND f.friendUser.id = :friendUserId)
               OR (f.user.id = :friendUserId AND f.friendUser.id = :userId)
            """)
    Optional<Friend> findBetweenUsers(
            @Param("userId") String userId,
            @Param("friendUserId") String friendUserId
    );

    @Query("""
            SELECT COUNT(f) > 0
            FROM Friend f
            WHERE (f.user.id = :userId AND f.friendUser.id = :friendUserId)
               OR (f.user.id = :friendUserId AND f.friendUser.id = :userId)
            """)
    boolean existsBetweenUsers(
            @Param("userId") String userId,
            @Param("friendUserId") String friendUserId
    );
}
