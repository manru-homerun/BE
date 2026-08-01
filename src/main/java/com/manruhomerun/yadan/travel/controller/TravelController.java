package com.manruhomerun.yadan.travel.controller;

import com.manruhomerun.yadan.travel.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelController {
    private final TravelService travelService;

    @PostMapping
    public ResponseEntity<?> createTravel(){
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{travelId}")
    public ResponseEntity<?> modifyTravel(
            @PathVariable
            String travelId
    ){
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getTravelList(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{travelId}")
    public ResponseEntity<?> getSpecificTravel(
            @PathVariable
            String travelId
    ){
        return ResponseEntity.ok().build();
    }

}
