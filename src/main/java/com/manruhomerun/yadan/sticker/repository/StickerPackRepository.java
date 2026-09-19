package com.manruhomerun.yadan.sticker.repository;

import java.util.Optional;

import com.manruhomerun.yadan.sticker.domain.entity.StickerPack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerPackRepository extends JpaRepository<StickerPack, Long> {

    Optional<StickerPack> findFirstByRegionCodeAndYearOrderByIdAsc(String regionCode, Integer year);
}
