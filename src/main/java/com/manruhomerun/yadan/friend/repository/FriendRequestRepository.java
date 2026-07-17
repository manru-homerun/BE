package com.manruhomerun.yadan.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByReceiverUserIdAndStatusOrderByCreatedAtDesc(
            String receiverUserId,
            FriendRequestStatus status
    );

    List<FriendRequest> findByRequesterUserIdAndStatusOrderByCreatedAtDesc(
            String requesterUserId,
            FriendRequestStatus status
    );

    Optional<FriendRequest> findByIdAndReceiverUserId(Long id, String receiverUserId);

    @Query("""
            SELECT COUNT(fr) > 0
            FROM FriendRequest fr
            WHERE fr.status = :status
              AND ((fr.requesterUser.id = :userId AND fr.receiverUser.id = :friendUserId)
                OR (fr.requesterUser.id = :friendUserId AND fr.receiverUser.id = :userId))
            """)
    boolean existsBetweenUsersByStatus(
            @Param("userId") String userId,
            @Param("friendUserId") String friendUserId,
            @Param("status") FriendRequestStatus status
    );
}
