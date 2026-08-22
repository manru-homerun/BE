package com.manruhomerun.yadan.sticker.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sticker_pack")
public class StickerPack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "region_code", nullable = false, length = 5)
    private String regionCode;

    @OneToMany(mappedBy = "stickerPack", fetch = FetchType.LAZY)
    private List<Sticker> stickers;
}
