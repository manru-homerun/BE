package com.manruhomerun.yadan.user.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import com.manruhomerun.yadan.travelspot.domain.enums.PreferredTravelRegionCode;
import com.manruhomerun.yadan.user.domain.converter.PreferredTravelRegionCodeConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "travel_preference")
public class TravelPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "travel_style_value", nullable = false)
    private Integer travelStyleValue;

    @Convert(converter = PreferredTravelRegionCodeConverter.class)
    @Column(name = "residence_region_code", nullable = false, length = 5)
    private PreferredTravelRegionCode residenceRegionCode;

    // 선호 지역 enum 목록의 각 값을 travel_preferred_region 테이블의 개별 행으로 저장
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "travel_preferred_region",
            joinColumns = @JoinColumn(name = "travel_preference_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"travel_preference_id", "region_code"}
            )
    )
    @Convert(converter = PreferredTravelRegionCodeConverter.class)
    @Column(name = "region_code", nullable = false, length = 5)
    private Set<PreferredTravelRegionCode> preferredRegionCodes = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
