package com.manruhomerun.yadan.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // 받은 요청 목록 조회
    @Query("""
            SELECT fr
            FROM FriendRequest fr
            WHERE (fr.firstUser.id = :userId OR fr.secondUser.id = :userId)
              AND fr.requesterUser.id <> :userId
              AND fr.status = com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus.PENDING
            ORDER BY fr.createdAt DESC
            """)
    List<FriendRequest> findPendingReceivedRequests(@Param("userId") String userId);

    // 보낸 요청 목록 조회
    @Query("""
            SELECT fr
            FROM FriendRequest fr
            WHERE fr.requesterUser.id = :requesterUserId
              AND fr.status = com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus.PENDING
            ORDER BY fr.createdAt DESC
            """)
    List<FriendRequest> findPendingSentRequests(
            @Param("requesterUserId") String requesterUserId
    );

    // 사용자 쌍 조회
    Optional<FriendRequest> findByFirstUserIdAndSecondUserId(
            String firstUserId,
            String secondUserId
    );

    // 받은 요청 단건 조회
    @Query("""
            SELECT fr
            FROM FriendRequest fr
            WHERE fr.id = :requestId
              AND (fr.firstUser.id = :receiverUserId OR fr.secondUser.id = :receiverUserId)
              AND fr.requesterUser.id <> :receiverUserId
            """)
    Optional<FriendRequest> findReceivedRequest(
            @Param("requestId") Long requestId,
            @Param("receiverUserId") String receiverUserId
    );

    // 보낸 요청 단건 조회
    Optional<FriendRequest> findByIdAndRequesterUserId(
            Long requestId,
            String requesterUserId
    );
}
