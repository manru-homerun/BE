package com.manruhomerun.yadan.friend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus;
import com.manruhomerun.yadan.friend.dto.FriendRequestCreateRequest;
import com.manruhomerun.yadan.friend.dto.FriendRequestResponse;
import com.manruhomerun.yadan.friend.error.FriendErrorCode;
import com.manruhomerun.yadan.friend.error.exception.FriendException;
import com.manruhomerun.yadan.friend.repository.FriendRepository;
import com.manruhomerun.yadan.friend.repository.FriendRequestRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구 요청 생성
    public FriendRequestResponse createRequest(String requesterUserId, FriendRequestCreateRequest request) {
        String receiverUserId = request.receiverUserId();
        // 자기 자신
        if (requesterUserId.equals(receiverUserId)) {
            throw new FriendException(FriendErrorCode.SELF_REQUEST_NOT_ALLOWED);
        }

        User requester = getUser(requesterUserId);
        User receiver = getUser(receiverUserId);
        // 이미 친구
        if (friendRepository.existsBetweenUsers(requesterUserId, receiverUserId)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIENDS);
        }

        boolean pendingRequestExists = friendRequestRepository.existsBetweenUsersByStatus(
                requesterUserId,
                receiverUserId,
                FriendRequestStatus.PENDING
        );
        // 이미 요청 보냄
        if (pendingRequestExists) {
            throw new FriendException(FriendErrorCode.REQUEST_ALREADY_EXISTS);
        }

        FriendRequest friendRequest = friendRequestRepository.save(
                FriendRequest.builder()
                        .requesterUser(requester)
                        .receiverUser(receiver)
                        .build()
        );
        return FriendRequestResponse.from(friendRequest);
    }

    // 받은 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(String receiverUserId) {
        getUser(receiverUserId);
        return friendRequestRepository
                .findByReceiverUserIdAndStatusOrderByCreatedAtDesc(receiverUserId, FriendRequestStatus.PENDING)
                .stream()
                .map(FriendRequestResponse::from)
                .toList();
    }

    // 보낸 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getSentRequests(String requesterUserId) {
        getUser(requesterUserId);
        return friendRequestRepository
                .findByRequesterUserIdAndStatusOrderByCreatedAtDesc(requesterUserId, FriendRequestStatus.PENDING)
                .stream()
                .map(FriendRequestResponse::from)
                .toList();
    }

    // 친구 요청 수락
    public void acceptRequest(String receiverUserId, Long requestId) {
        FriendRequest friendRequest = getReceivedRequest(receiverUserId, requestId);
        validatePending(friendRequest);

        String requesterUserId = friendRequest.getRequesterUser().getId();
        if (friendRepository.existsBetweenUsers(requesterUserId, receiverUserId)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIENDS);
        }

        friendRequest.accept();
        friendRepository.save(
                Friend.builder()
                        .user(friendRequest.getRequesterUser())
                        .friendUser(friendRequest.getReceiverUser())
                        .build()
        );
    }
    // 친구 요청 거절
    public void rejectRequest(String receiverUserId, Long requestId) {
        FriendRequest friendRequest = getReceivedRequest(receiverUserId, requestId);
        validatePending(friendRequest);
        friendRequest.reject();
    }

    // 사용자 정보 찾기
    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
    // 받은 친구 요청
    private FriendRequest getReceivedRequest(String receiverUserId, Long requestId) {
        return friendRequestRepository.findByIdAndReceiverUserId(requestId, receiverUserId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.REQUEST_NOT_FOUND));
    }
    // 보류 상태 검증(이미 수락되었거나 거절된 요청)
    private void validatePending(FriendRequest friendRequest) {
        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new FriendException(FriendErrorCode.REQUEST_ALREADY_PROCESSED);
        }
    }
}
