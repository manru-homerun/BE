package com.manruhomerun.yadan.user.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.baseball.error.exception.BaseballResourceNotFoundException;
import com.manruhomerun.yadan.baseball.repository.BaseballTeamRepository;
import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.TravelRegionCode;
import com.manruhomerun.yadan.user.dto.OnboardingRequest;
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

    // 온보딩
    @Transactional
    public void onboard(String userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
            throw new UserException(UserErrorCode.ONBOARDING_ALREADY_COMPLETED);
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

        TravelRegionCode residenceRegionCode;
        Set<TravelRegionCode> preferredRegionCodes = new HashSet<>();

        try {
            residenceRegionCode = TravelRegionCode.fromCityName(request.residenceRegion());

            for (String preferredRegion : request.preferredRegions()) {
                preferredRegionCodes.add(TravelRegionCode.fromCityName(preferredRegion));
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
}
