package com.manruhomerun.yadan.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.user.domain.entity.UserAgreement;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {

    Optional<UserAgreement> findByUserId(String userId);
}
