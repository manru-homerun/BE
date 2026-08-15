package com.manruhomerun.yadan.friend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.friend.dto.FriendListResponse;
import com.manruhomerun.yadan.friend.dto.FriendResponse;
import com.manruhomerun.yadan.friend.error.FriendErrorCode;
import com.manruhomerun.yadan.friend.error.exception.FriendException;
import com.manruhomerun.yadan.friend.repository.FriendRepository;
import com.manruhomerun.yadan.friend.repository.FriendRequestRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public FriendListResponse getFriends(String currentUserId) {
        validateUser(currentUserId);

        List<FriendResponse> friends = friendRepository.findAllByUserId(currentUserId)
                .stream()
                .map(friend -> FriendResponse.from(friend, currentUserId))
                .toList();

        long receivedRequestCount =
                friendRequestRepository.countPendingReceivedRequests(currentUserId);

        return FriendListResponse.from(friends, receivedRequestCount);
    }

    // 친구 삭제
    public void deleteFriend(String currentUserId, Long friendId) {
        Friend friend = friendRepository.findByIdAndUserId(friendId, currentUserId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_NOT_FOUND));

        friendRepository.delete(friend);
    }

    // 사용자 존재 여부 확인
    private void validateUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
