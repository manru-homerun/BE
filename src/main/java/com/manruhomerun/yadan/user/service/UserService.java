package com.manruhomerun.yadan.user.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballResourceNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballTeamRepository;
import com.manruhomerun.yadan.friend.domain.entity.Friend;
import com.manruhomerun.yadan.friend.domain.entity.FriendRequest;
import com.manruhomerun.yadan.friend.domain.enums.FriendRelationshipStatus;
import com.manruhomerun.yadan.friend.repository.FriendRepository;
import com.manruhomerun.yadan.friend.repository.FriendRequestRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.travelspot.domain.enums.PreferredTravelRegionCode;
import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.dto.NicknameCheckRequest;
import com.manruhomerun.yadan.user.dto.NicknameCheckResponse;
import com.manruhomerun.yadan.user.dto.OnboardingRequest;
import com.manruhomerun.yadan.user.dto.TravelPreferenceResponse;
import com.manruhomerun.yadan.user.dto.TravelPreferenceUpdateRequest;
import com.manruhomerun.yadan.user.dto.UserProfileResponse;
import com.manruhomerun.yadan.user.dto.UserProfileUpdateRequest;
import com.manruhomerun.yadan.user.dto.UserSearchItemResponse;
import com.manruhomerun.yadan.user.dto.UserSearchRequest;
import com.manruhomerun.yadan.user.dto.UserSearchResponse;
import com.manruhomerun.yadan.user.error.UserErrorCode;
import com.manruhomerun.yadan.user.error.exception.UserException;
import com.manruhomerun.yadan.user.repository.TravelPreferenceRepository;
import com.manruhomerun.yadan.user.repository.UserAgreementRepository;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BaseballTeamRepository baseballTeamRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    // 온보딩
    @Transactional
    public void onboard(String userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
            throw new UserException(UserErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }

        if (userRepository.existsByNicknameAndIdNot(request.nickname(), userId)) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        if (!Boolean.TRUE.equals(request.agreements().serviceTerms())
                || !Boolean.TRUE.equals(request.agreements().privacyPolicy())) {
            throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
        }

        BaseballTeam favoriteTeam = baseballTeamRepository.findById(request.favoriteTeamId())
                .orElseThrow(() -> new BaseballResourceNotFoundException(
                        BaseballErrorCode.BASEBALL_TEAM_NOT_FOUND,
                        "팀을 찾을 수 없습니다. teamId=" + request.favoriteTeamId()
                ));

        PreferredTravelRegionCode residenceRegionCode;
        Set<PreferredTravelRegionCode> preferredRegionCodes = new HashSet<>();

        try {
            residenceRegionCode = PreferredTravelRegionCode.fromRegionName(request.residenceRegion());

            for (String preferredRegion : request.preferredRegions()) {
                preferredRegionCodes.add(PreferredTravelRegionCode.fromRegionName(preferredRegion));
            }
        } catch (IllegalArgumentException exception) {
            throw new UserException(
                    UserErrorCode.INVALID_TRAVEL_REGION,
                    exception.getMessage()
            );
        }

        userAgreementRepository.save(request.agreements().toEntity(user));
        travelPreferenceRepository.save(request.toEntity(
                user,
                residenceRegionCode,
                preferredRegionCodes
        ));

        user.completeOnboarding(
                request.nickname(),
                request.gender(),
                request.birthday(),
                favoriteTeam
        );
    }

    // 나의 프로필 조회
    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return UserProfileResponse.from(user);
    }

    // 닉네임 중복 확인
    public NicknameCheckResponse checkNickname(String userId, NicknameCheckRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        boolean duplicated = userRepository.existsByNicknameAndIdNot(
                request.nickname(),
                userId
        );

        return new NicknameCheckResponse(!duplicated);
    }

    // 닉네임으로 사용자를 검색하고 현재 사용자와의 친구 관계를 함께 조회한다.
    public UserSearchResponse searchUsers(String userId, UserSearchRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Pageable pageable = PageRequest.of(
                0,
                request.limit(),
                Sort.by("nickname").ascending()
        );

        List<User> users = userRepository.searchByNicknamePrefix(
                request.nickname(),
                userId,
                pageable
        );

        if (users.isEmpty()) {
            return UserSearchResponse.from(List.of());
        }

        List<String> targetUserIds = users.stream()
                .map(User::getId)
                .toList();

        // 사용자 ID: 현재 사용자와의 친구 또는 요청 상태
        Map<String, FriendRelationshipStatus> relationshipStatuses = new HashMap<>();

        List<Friend> friends = friendRepository.findAllBetweenCurrentUserAndTargets(
                userId,
                targetUserIds
        );

        for (Friend friend : friends) {
            String targetUserId = friend.getFirstUser().getId().equals(userId)
                    ? friend.getSecondUser().getId()
                    : friend.getFirstUser().getId();

            relationshipStatuses.put(targetUserId, FriendRelationshipStatus.FRIEND);
        }

        List<FriendRequest> pendingRequests = friendRequestRepository
                .findPendingBetweenCurrentUserAndTargets(userId, targetUserIds);

        for (FriendRequest friendRequest : pendingRequests) {
            String targetUserId = friendRequest.getFirstUser().getId().equals(userId)
                    ? friendRequest.getSecondUser().getId()
                    : friendRequest.getFirstUser().getId();

            if (relationshipStatuses.get(targetUserId) == FriendRelationshipStatus.FRIEND) {
                continue;
            }

            FriendRelationshipStatus relationshipStatus = friendRequest.getRequesterUser()
                    .getId()
                    .equals(userId)
                    ? FriendRelationshipStatus.REQUEST_SENT
                    : FriendRelationshipStatus.REQUEST_RECEIVED;

            relationshipStatuses.put(targetUserId, relationshipStatus);
        }

        List<UserSearchItemResponse> searchResults = users.stream()
                .map(user -> UserSearchItemResponse.from(
                        user,
                        relationshipStatuses.getOrDefault(
                                user.getId(),
                                FriendRelationshipStatus.NONE
                        )
                ))
                .toList();

        return UserSearchResponse.from(searchResults);
    }

    // 나의 여행 취향 정보 조회
    public TravelPreferenceResponse getPreference(String userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        TravelPreference travelPreference = travelPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(
                        UserErrorCode.TRAVEL_PREFERENCE_NOT_FOUND
                ));

        return TravelPreferenceResponse.from(travelPreference);
    }

    // 나의 여행 취향 정보 수정
    @Transactional
    public TravelPreferenceResponse updatePreference(
            String userId,
            TravelPreferenceUpdateRequest request
    ) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        TravelPreference travelPreference = travelPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(
                        UserErrorCode.TRAVEL_PREFERENCE_NOT_FOUND
                ));

        PreferredTravelRegionCode residenceRegionCode;
        Set<PreferredTravelRegionCode> preferredRegionCodes = new HashSet<>();

        try {
            residenceRegionCode = PreferredTravelRegionCode.fromRegionName(request.residenceRegion());

            for (String preferredRegion : request.preferredRegions()) {
                preferredRegionCodes.add(PreferredTravelRegionCode.fromRegionName(preferredRegion));
            }
        } catch (IllegalArgumentException exception) {
            throw new UserException(
                    UserErrorCode.INVALID_TRAVEL_REGION,
                    exception.getMessage()
            );
        }

        travelPreference.updatePreference(
                request.travelStyleValue(),
                residenceRegionCode,
                preferredRegionCodes
        );

        return TravelPreferenceResponse.from(travelPreference);
    }

    // 나의 프로필 수정
    @Transactional
    public UserProfileResponse updateProfile(String userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (userRepository.existsByNicknameAndIdNot(request.nickname(), userId)) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        BaseballTeam favoriteTeam = baseballTeamRepository.findById(request.favoriteTeamId())
                .orElseThrow(() -> new BaseballResourceNotFoundException(
                        BaseballErrorCode.BASEBALL_TEAM_NOT_FOUND,
                        "팀을 찾을 수 없습니다. teamId=" + request.favoriteTeamId()
                ));

        user.updateProfile(
                request.profileImageUrl(),
                request.nickname(),
                favoriteTeam,
                request.birthday(),
                request.gender()
        );

        return UserProfileResponse.from(user);
    }
}
