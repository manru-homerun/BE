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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_pack_id", nullable = false)
    private StickerPack stickerPack;
}
