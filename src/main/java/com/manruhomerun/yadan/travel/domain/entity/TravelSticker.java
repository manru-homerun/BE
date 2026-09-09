package com.manruhomerun.yadan.travel.domain.entity;

import com.manruhomerun.yadan.sticker.domain.entity.StickerPack;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "travel_sticker_mapping")
public class TravelSticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_user_mapping_id", nullable = false)
    private TravelUser travelUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sticker_pack_id", nullable = false)
    private StickerPack stickerPack;
}
