package com.manruhomerun.yadan.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manruhomerun.yadan.global.error.exception.UserNotFoundException;
import com.manruhomerun.yadan.notification.domain.entity.PushInstallation;
import com.manruhomerun.yadan.notification.dto.PushRegistrationRequest;
import com.manruhomerun.yadan.notification.repository.PushInstallationRepository;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushRegistrationService {

    private final UserRepository userRepository;
    private final PushInstallationRepository pushInstallationRepository;

    @Transactional
    public void register(String userId, PushRegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        pushInstallationRepository
                .findByFirebaseInstallationId(request.installationId())
                .ifPresentOrElse(
                        installation -> installation.updateRegistration(user, request.appVersion()),
                        () -> pushInstallationRepository.save(
                                PushInstallation.create(
                                        user,
                                        request.installationId(),
                                        request.appVersion()
                                )
                        )
                );
    }
}
