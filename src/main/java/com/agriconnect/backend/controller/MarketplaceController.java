package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.MarketplaceDTO;
import com.agriconnect.backend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketplaceController {

    @Autowired
    private ListingService listingService;

    /*Get /api/marketplace - Get all available listings for marketplace*/
    @GetMapping("/marketplace")
    public ResponseEntity<List<MarketplaceDTO>> getAllAvailableProduce() {
        try{
            List<MarketplaceDTO> produceList = listingService.getAllAvailableListings();
            return ResponseEntity.ok(produceList);

        } catch(Exception e){
            System.err.println("Error fetching marketplace produce: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
