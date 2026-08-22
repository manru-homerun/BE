package com.manruhomerun.yadan.travel.repository;

import com.manruhomerun.yadan.travel.domain.entity.TravelSticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelStickerRepository extends JpaRepository<TravelSticker, Long> {
    Optional<TravelSticker> findFirstByTravelIdOrderByIdAsc(String travelId);
}
