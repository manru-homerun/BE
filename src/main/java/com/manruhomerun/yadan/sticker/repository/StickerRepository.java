package com.manruhomerun.yadan.sticker.repository;

import com.manruhomerun.yadan.sticker.domain.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    List<Sticker> findAllByStickerPackIdOrderByIdAsc(Long stickerPackId);
}
