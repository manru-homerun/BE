package com.manruhomerun.yadan.travelcerti.service;

import com.manruhomerun.yadan.sticker.domain.entity.StickerPack;
import com.manruhomerun.yadan.sticker.repository.StickerPackRepository;
import com.manruhomerun.yadan.travel.domain.entity.TravelSticker;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import com.manruhomerun.yadan.travel.repository.TravelStickerRepository;
import com.manruhomerun.yadan.travel.repository.TravelTravelSpotRepository;
import com.manruhomerun.yadan.travel.repository.TravelUserRepository;
import com.manruhomerun.yadan.travelcerti.domain.entity.TravelCertification;
import com.manruhomerun.yadan.travelcerti.dto.TravelSpotVerificationRequest;
import com.manruhomerun.yadan.travelcerti.dto.TravelSpotVerificationResponse;
import com.manruhomerun.yadan.travelcerti.error.TravelCertificationErrorCode;
import com.manruhomerun.yadan.travelcerti.error.exception.TravelCertificationException;
import com.manruhomerun.yadan.travelcerti.repository.TravelCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelCertiService {

    private final TravelUserRepository travelUserRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelCertificationRepository travelCertificationRepository;
    private final TravelStickerRepository travelStickerRepository;
    private final StickerPackRepository stickerPackRepository;

    public TravelSpotVerificationResponse verifyTravelSpot(
            String userId,
            String travelId,
            String spotId,
            TravelSpotVerificationRequest request
    ) {
        double certificationRadiusMeters = 1_000.0; // 1km
        double earthRadiusMeters = 6_371_000.0;

        // 1. 요청한 사용자가 해당 여행에 참여 중인지 확인합니다.
        TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(() -> new TravelCertificationException(
                        TravelCertificationErrorCode.TRAVEL_USER_NOT_FOUND,
                        "여행 참여 정보를 찾을 수 없습니다. travelId=" + travelId + ", userId=" + userId
                ));

        // 2. 요청한 여행지가 해당 여행 일정에 포함되어 있는지 확인합니다.
        TravelTravelSpot travelTravelSpot = travelTravelSpotRepository
                .findByTravelIdAndTravelSpotId(travelId, spotId)
                .orElseThrow(() -> new TravelCertificationException(
                        TravelCertificationErrorCode.TRAVEL_SPOT_NOT_FOUND,
                        "여행에 포함된 여행지를 찾을 수 없습니다. travelId=" + travelId + ", spotId=" + spotId
                ));

        // 3. 이미 인증한 여행지라면 중복 기록을 생성하지 않고 성공 처리합니다.
        if (travelCertificationRepository.existsByTravelUserIdAndTravelSpotId(
                travelUser.getId(),
                travelTravelSpot.getId()
        )) {
            long certificationCount = travelCertificationRepository.countByTravelUserId(travelUser.getId());
            return new TravelSpotVerificationResponse(certificationCount);
        }

        // 4. 하버사인 공식을 이용해 현재 위치와 여행지 기준점 사이의 직선거리를 계산합니다.
        double currentLatitudeRadians = Math.toRadians(request.latitude().doubleValue());
        double currentLongitudeRadians = Math.toRadians(request.longitude().doubleValue());
        double spotLatitudeRadians = Math.toRadians(travelTravelSpot.getTravelSpot().getLatitude().doubleValue());
        double spotLongitudeRadians = Math.toRadians(travelTravelSpot.getTravelSpot().getLongitude().doubleValue());
        double latitudeDifference = spotLatitudeRadians - currentLatitudeRadians;
        double longitudeDifference = spotLongitudeRadians - currentLongitudeRadians;
        double haversine = Math.pow(Math.sin(latitudeDifference / 2), 2)
                + Math.cos(currentLatitudeRadians)
                * Math.cos(spotLatitudeRadians)
                * Math.pow(Math.sin(longitudeDifference / 2), 2);
        double distanceMeters = earthRadiusMeters * 2 * Math.atan2(
                Math.sqrt(haversine),
                Math.sqrt(1 - haversine)
        );

        // 5. 측정 오차까지 반영한 최악의 거리가 기준점에서 1km를 넘으면 인증을 거부합니다.
        double farthestPossibleDistanceMeters = distanceMeters + request.accuracy().doubleValue();
        if (farthestPossibleDistanceMeters > certificationRadiusMeters) {
            throw new TravelCertificationException(
                    TravelCertificationErrorCode.LOCATION_OUT_OF_RANGE,
                    "여행지 방문 인증 가능 범위를 벗어났습니다. distanceMeters="
                            + Math.round(distanceMeters)
                            + ", accuracy="
                            + request.accuracy()
                            + ", farthestPossibleDistanceMeters="
                            + Math.round(farthestPossibleDistanceMeters)
            );
        }

        // 6. 모든 검증을 통과하면 사용자와 여행 일정의 여행지를 연결해 방문 인증을 저장합니다.
        travelCertificationRepository.save(
                TravelCertification.builder()
                        .travelUser(travelUser)
                        .travelSpot(travelTravelSpot)
                        .build()
        );

        // 7. 이번 인증을 포함한 사용자의 해당 여행 방문 인증 개수를 계산합니다.
        long certificationCount = travelCertificationRepository.countByTravelUserId(travelUser.getId());

        // 8. 인증 개수가 4개를 초과하고 아직 스티커가 없다면 사용자에게 스티커팩을 지급합니다.
        if (certificationCount > 4 && !travelStickerRepository.existsByTravelUserId(travelUser.getId())) {
            String regionCode = travelUser.getTravel().getRegionCode();
            int travelYear = travelUser.getTravel().getStartDate().getYear();
            StickerPack stickerPack = stickerPackRepository
                    .findFirstByRegionCodeAndYearOrderByIdAsc(regionCode, travelYear)
                    .orElseThrow(() -> new TravelCertificationException(
                            TravelCertificationErrorCode.STICKER_PACK_NOT_FOUND,
                            "지급할 스티커팩을 찾을 수 없습니다. regionCode="
                                    + regionCode
                                    + ", year="
                                    + travelYear
                    ));

            travelStickerRepository.save(
                    TravelSticker.builder()
                            .travelUser(travelUser)
                            .stickerPack(stickerPack)
                            .build()
            );
        }

        return new TravelSpotVerificationResponse(certificationCount);
    }
}
