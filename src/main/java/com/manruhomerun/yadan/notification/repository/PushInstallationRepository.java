package com.manruhomerun.yadan.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manruhomerun.yadan.notification.domain.entity.PushInstallation;

public interface PushInstallationRepository extends JpaRepository<PushInstallation, Long> {

    Optional<PushInstallation> findByFirebaseInstallationId(String firebaseInstallationId);

    List<PushInstallation> findAllByUserId(String userId);
}
