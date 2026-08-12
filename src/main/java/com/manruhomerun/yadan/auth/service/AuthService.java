package com.manruhomerun.yadan.auth.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.auth.client.KakaoApiClient;
import com.manruhomerun.yadan.auth.domain.entity.LoginSession;
import com.manruhomerun.yadan.auth.dto.kakao.KakaoTokenInfoResponse;
import com.manruhomerun.yadan.auth.dto.kakao.KakaoUserInfoResponse;
import com.manruhomerun.yadan.auth.dto.LoginResponse;
import com.manruhomerun.yadan.auth.dto.RefreshTokenResponse;
import com.manruhomerun.yadan.auth.error.AuthErrorCode;
import com.manruhomerun.yadan.auth.error.exception.AuthException;
import com.manruhomerun.yadan.auth.properties.KakaoApiProperties;
import com.manruhomerun.yadan.auth.repository.LoginSessionRepository;
import com.manruhomerun.yadan.auth.token.JwtProvider;
import com.manruhomerun.yadan.auth.token.RefreshTokenClaims;
import com.manruhomerun.yadan.auth.token.TokenHasher;
import com.manruhomerun.yadan.auth.token.TokenPair;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.UserProvider;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoApiProperties kakaoApiProperties;
    private final UserRepository userRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final JwtProvider jwtProvider;
    private final TokenHasher tokenHasher;

    // 카카오 사용자 검증, user 생성, loginSession 생성, JWT 발급
    @Transactional
    public LoginResponse login(UserProvider provider, String providerAccessToken) {
        if (provider != UserProvider.KAKAO) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        // 카카오 엑세스 토큰 조회 (토큰 검증)
        KakaoTokenInfoResponse tokenInfo = kakaoApiClient.getTokenInfo(providerAccessToken);
        if (tokenInfo == null
                || tokenInfo.id() == null
                || tokenInfo.appId() == null
                || tokenInfo.expiresInMillis() == null
                || tokenInfo.expiresInMillis() <= 0
                || !Objects.equals(tokenInfo.appId(), kakaoApiProperties.getAppId())) {
            throw new AuthException(AuthErrorCode.INVALID_KAKAO_TOKEN);
        }

        // 사용자 정보 조회
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(providerAccessToken);
        if (userInfo == null
                || userInfo.id() == null
                || !Objects.equals(tokenInfo.id(), userInfo.id())) {
            throw new AuthException(AuthErrorCode.INVALID_KAKAO_TOKEN);
        }

        // 서비스 사용자 조회 or 생성
        UserResolution userResolution = findOrCreateUser(provider, userInfo);
        User user = userResolution.user();
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new AuthException(AuthErrorCode.WITHDRAWN_USER);
        }

        // refreshToken 저장
        String loginSessionId = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtProvider.issueTokenPair(user.getId(), loginSessionId);
        String refreshTokenHash = tokenHasher.hash(tokenPair.refreshToken());

        LoginSession loginSession = LoginSession.create(
                loginSessionId,
                user,
                refreshTokenHash,
                tokenPair.refreshTokenExpiresAt()
        );
        loginSessionRepository.save(loginSession);

        return new LoginResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                userResolution.isNewMember()
        );
    }

    // RefreshToken 검증 + Access/Refresh Token 새로 발급
    @Transactional
    public RefreshTokenResponse refresh(String refreshToken) {
        // refreshToken 검증
        RefreshTokenClaims claims = jwtProvider.verifyRefreshToken(refreshToken);
        // refreshToken 정보 조회
        LoginSession loginSession = loginSessionRepository.findByIdAndUser_Id(
                        claims.sessionId(),
                        claims.userId()
                )
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        if (loginSession.isRevoked()
                || loginSession.isExpired()
                || !tokenHasher.matches(refreshToken, loginSession.getRefreshTokenHash())) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 사용자 정보 조회
        User user = loginSession.getUser();
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new AuthException(AuthErrorCode.WITHDRAWN_USER);
        }

        // accessToken, refreshToken 발급
        TokenPair tokenPair = jwtProvider.issueTokenPair(user.getId(), loginSession.getId());
        String newRefreshTokenHash = tokenHasher.hash(tokenPair.refreshToken());

        loginSession.rotate(
                newRefreshTokenHash,
                tokenPair.refreshTokenExpiresAt()
        );

        return new RefreshTokenResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken()
        );
    }

    // 로그아웃
    @Transactional
    public void logout(String refreshToken) {
        RefreshTokenClaims claims = jwtProvider.verifyRefreshToken(refreshToken);

        LoginSession loginSession = loginSessionRepository.findByIdAndUser_Id(
                        claims.sessionId(),
                        claims.userId()
                )
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        if (loginSession.isRevoked()
                || loginSession.isExpired()
                || !tokenHasher.matches(refreshToken, loginSession.getRefreshTokenHash())) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        loginSession.revoke();
    }

    // 기존 회원 찾기 or 새로운 회원 생성
    private UserResolution findOrCreateUser(
            UserProvider provider,
            KakaoUserInfoResponse userInfo
    ) {
        String providerUserId = String.valueOf(userInfo.id()); // 카카오 회원 id

        return userRepository.findByProviderAndProviderUserId(
                        provider,
                        providerUserId
                )
                .map(user -> new UserResolution(user, false)) // 기존 사용자 존재함
                .orElseGet(() -> { // 신규 사용자는 카카오 사용자 정보조회 응답으로 채우기
                    KakaoUserInfoResponse.Profile profile =
                            userInfo.kakaoAccount() == null
                                    ? null
                                    : userInfo.kakaoAccount().profile();

                    String nickname =
                            profile == null ? null : profile.nickname();

                    String profileImageUrl =
                            profile == null
                                    ? null
                                    : profile.profileImageUrl();

                    User user = User.createOAuthUser(
                            provider,
                            providerUserId,
                            nickname,
                            profileImageUrl
                    );
                    return new UserResolution(userRepository.save(user), true);
                });
    }

    // 기존 회원 조회, 신규 회원 생성 결과를 담는 객체
    private record UserResolution(
            User user,
            boolean isNewMember
    ) {
    }
}
