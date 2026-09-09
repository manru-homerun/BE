package com.manruhomerun.yadan.travelcerti.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "travel_certification")
public class TravelCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_user_mapping_id", nullable = false)
    private TravelUser travelUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_spot_mapping_id", nullable = false)
    private TravelTravelSpot travelSpot;

    @Column(name = "certified_at", nullable = false)
    private LocalDateTime certifiedAt;

    @PrePersist
    public void prePersist() {
        if (certifiedAt == null) {
            certifiedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        }
    }
}
