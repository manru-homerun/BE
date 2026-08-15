package com.manruhomerun.yadan.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByNicknameAndIdNot(String nickname, String userId);
}
