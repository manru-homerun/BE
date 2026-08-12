package com.manruhomerun.yadan.friend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.friend.domain.enums.FriendRequestStatus;
import com.manruhomerun.yadan.friend.dto.FriendRequestCreateRequest;
import com.manruhomerun.yadan.friend.dto.FriendRequestListResponse;
import com.manruhomerun.yadan.friend.dto.FriendRequestResponse;
import com.manruhomerun.yadan.friend.dto.ReceivedFriendRequestListResponse;
import com.manruhomerun.yadan.friend.dto.ReceivedFriendRequestResponse;
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

        if (requesterUserId.equals(receiverUserId)) {
            throw new FriendException(FriendErrorCode.SELF_REQUEST_NOT_ALLOWED);
        }

        User requester = getUser(requesterUserId);
        User receiver = getUser(receiverUserId);

        // 사용자 정렬
        User firstUser;
        User secondUser;
        if (requesterUserId.compareTo(receiverUserId) < 0) {
            firstUser = requester;
            secondUser = receiver;
        } else {
            firstUser = receiver;
            secondUser = requester;
        }

        if (friendRepository.existsByFirstUserIdAndSecondUserId(
                firstUser.getId(),
                secondUser.getId()
        )) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIENDS);
        }

        FriendRequest friendRequest = friendRequestRepository
                .findByFirstUserIdAndSecondUserId(firstUser.getId(), secondUser.getId())
                .map(existingRequest -> {
                    if (existingRequest.getStatus() == FriendRequestStatus.PENDING) {
                        throw new FriendException(FriendErrorCode.REQUEST_ALREADY_EXISTS);
                    }

                    existingRequest.requestAgain(requester);
                    return existingRequest;
                })
                .orElseGet(() -> friendRequestRepository.save(
                        FriendRequest.builder()
                                .firstUser(firstUser)
                                .secondUser(secondUser)
                                .requesterUser(requester)
                                .build()
                ));

        return FriendRequestResponse.from(friendRequest);
    }

    // 받은 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public ReceivedFriendRequestListResponse getReceivedRequests(String receiverUserId) {
        getUser(receiverUserId);
        List<ReceivedFriendRequestResponse> receivedRequests = friendRequestRepository
                .findPendingReceivedRequests(receiverUserId)
                .stream()
                .map(ReceivedFriendRequestResponse::from)
                .toList();

        long friendCount = friendRepository.countByUserId(receiverUserId);
        long sentRequestCount = friendRequestRepository.countPendingSentRequests(receiverUserId);

        return ReceivedFriendRequestListResponse.of(
                friendCount,
                sentRequestCount,
                receivedRequests
        );
    }

    // 보낸 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public FriendRequestListResponse getSentRequests(String requesterUserId) {
        getUser(requesterUserId);
        List<FriendRequestResponse> requests = friendRequestRepository
                .findPendingSentRequests(requesterUserId)
                .stream()
                .map(FriendRequestResponse::from)
                .toList();

        return FriendRequestListResponse.from(requests);
    }

    // 친구 요청 수락
    public void acceptRequest(String receiverUserId, Long requestId) {
        FriendRequest friendRequest = getReceivedRequest(receiverUserId, requestId);
        validatePending(friendRequest);

        if (friendRepository.existsByFirstUserIdAndSecondUserId(
                friendRequest.getFirstUser().getId(),
                friendRequest.getSecondUser().getId()
        )) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIENDS);
        }

        friendRequest.accept();
        friendRepository.save(
                Friend.builder()
                        .firstUser(friendRequest.getFirstUser())
                        .secondUser(friendRequest.getSecondUser())
                        .build()
        );
    }
    // 친구 요청 거절
    public void rejectRequest(String receiverUserId, Long requestId) {
        FriendRequest friendRequest = getReceivedRequest(receiverUserId, requestId);
        validatePending(friendRequest);
        friendRequest.reject();
    }

    // 친구 요청 취소
    public void cancelRequest(String requesterUserId, Long requestId) {
        FriendRequest friendRequest = getSentRequest(requesterUserId, requestId);
        validatePending(friendRequest);
        friendRequest.cancel();
    }

    // 사용자 정보 찾기
    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
    // 받은 친구 요청
    private FriendRequest getReceivedRequest(String receiverUserId, Long requestId) {
        return friendRequestRepository.findReceivedRequest(requestId, receiverUserId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.REQUEST_NOT_FOUND));
    }

    // 보낸 친구 요청
    private FriendRequest getSentRequest(String requesterUserId, Long requestId) {
        return friendRequestRepository.findByIdAndRequesterUserId(requestId, requesterUserId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.REQUEST_NOT_FOUND));
    }

    // 보류 상태 검증(이미 처리된 요청)
    private void validatePending(FriendRequest friendRequest) {
        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new FriendException(FriendErrorCode.REQUEST_ALREADY_PROCESSED);
        }
    }
}
