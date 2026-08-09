package com.manruhomerun.yadan.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.auth.domain.entity.LoginSession;

public interface LoginSessionRepository extends JpaRepository<LoginSession, String> {

    // 세션 ID와 사용자 ID가 일치하는 로그인 세션 조회
    Optional<LoginSession> findByIdAndUser_Id(
            String id,
            String userId
    );

    // 모든 로그인 세션을 조회
    List<LoginSession> findAllByUser_Id(String userId);

    // 모든 로그인 세션 삭제
    void deleteAllByUser_Id(String userId);

    // 전달받은 시각보다 먼저 만료된 로그인 세션을 모두 삭제
    long deleteAllByExpiresAtBefore(LocalDateTime dateTime);
}
