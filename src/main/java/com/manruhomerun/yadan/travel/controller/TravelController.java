package com.manruhomerun.yadan.travel.controller;

import com.manruhomerun.yadan.travel.dto.TravelCreateRequest;
import com.manruhomerun.yadan.travel.dto.TravelDetailResponse;
import com.manruhomerun.yadan.travel.service.TravelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelController {
    private final TravelService travelService;

    // 여행 코스 최초 저장
    @PostMapping
    public ResponseEntity<Void> createTravel(
            @RequestBody TravelCreateRequest request
    ){
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        travelService.createTravel(userId, request);
        return ResponseEntity.ok().build();
    }

    // 특정 여행 수정
    @PutMapping("/{travelId}")
    public ResponseEntity<?> modifyTravel(
            @PathVariable
            String travelId
    ){
        return ResponseEntity.ok().build();
    }

    // 여행 목록 조회
    @GetMapping
    public ResponseEntity<?> getTravelList(
            HttpServletRequest httpServletRequest
    ){
        //        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "11111111-1111-1111-1111-111111111111"; // 임시로 고정된 userId 사용
        return ResponseEntity.ok(travelService.getTravelList(userId));
    }

    // 특정 여행 조회
    @GetMapping("/{travelId}")
    public ResponseEntity<TravelDetailResponse> getSpecificTravel(
            @PathVariable
            String travelId
    ){
        return ResponseEntity.ok(travelService.getTravelById(travelId));
    }

    // 여행 테마 조회
    @GetMapping("/theme")
    public ResponseEntity<?> getTravelThemeList(
    ){
        return ResponseEntity.ok(travelService.getTravelThemeList());
    }

}
