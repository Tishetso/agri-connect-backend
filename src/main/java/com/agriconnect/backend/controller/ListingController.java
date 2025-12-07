package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/listings")

public class ListingController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private ListingService service;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createListing(
            @RequestPart("product") String product,
            @RequestPart("quantity") String quantity,
            @RequestPart("price") String priceStr,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {

        Double price = Double.parseDouble(priceStr.trim());

        List<String> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : images) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + filename);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            uploadedFiles.add(filename);
        }

        Listing listing = new Listing();
        listing.setProduct(product);
        listing.setQuantity(quantity);
        listing.setPrice(price);
        listing.setImageUrls(uploadedFiles);

        return ResponseEntity.ok(service.saveListing(listing));
    }

    @GetMapping
    public List<Listing> getAllListings() {
        return service.findAll();
    }



}
