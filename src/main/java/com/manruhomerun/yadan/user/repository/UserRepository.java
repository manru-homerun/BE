package com.manruhomerun.yadan.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.domain.enums.UserProvider;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByNicknameAndIdNot(String nickname, String userId);

    Optional<User> findByProviderAndProviderUserId(
            UserProvider provider,
            String providerUserId
    );
}
