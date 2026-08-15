package com.manruhomerun.yadan.travel.domain.entity;

import com.manruhomerun.yadan.travelspot.domain.entity.TravelSpot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "travel_travel_spot_mapping")
public class TravelTravelSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "travel_spot_id", nullable = false)
    private TravelSpot travelSpot;

    @Column(name = "day", nullable = false)
    private int day;

    @Column(name = "placement_order", nullable = false)
    private int order;

}
