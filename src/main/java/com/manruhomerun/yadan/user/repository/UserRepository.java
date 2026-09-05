package com.manruhomerun.yadan.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.UserProvider;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByNicknameAndIdNot(String nickname, String userId);

    @EntityGraph(attributePaths = "favoriteTeam")
    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.nickname) LIKE CONCAT(LOWER(:nickname), '%')
              AND u.id <> :currentUserId
              AND u.onboardingCompleted = true
              AND u.isDeleted = false
            """)
    List<User> searchByNicknamePrefix(
            @Param("nickname") String nickname,
            @Param("currentUserId") String currentUserId,
            Pageable pageable
    );

    Optional<User> findByProviderAndProviderUserId(
            UserProvider provider,
            String providerUserId
    );
}
