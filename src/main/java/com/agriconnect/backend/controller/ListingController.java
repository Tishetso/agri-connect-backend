package com.agriconnect.backend.controller;

import com.agriconnect.backend.dto.ListingDTO;
import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.model.User;
import com.agriconnect.backend.service.ListingService;
import com.agriconnect.backend.service.userService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private userService userService;

    @PostMapping("/create")
    public ResponseEntity<ListingDTO> createListing(
            @RequestPart("product") String product,
            @RequestPart("quantity") String quantity,
            @RequestPart("price") String priceStr,
            @RequestPart("images") List<MultipartFile> images,
            Authentication auth,
            HttpServletRequest request) throws IOException {

        System.out.println("=== RECEIVED REQUEST ===");
        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("Product: " + product);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + priceStr);
        System.out.println("Images count: " + (images != null ? images.size() : 0));

        try {
            Double price = Double.parseDouble(priceStr.trim());

            String email = auth.getName();
            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                return ResponseEntity.status(401).body(null);
            }

            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + filename);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, file.getBytes());
                    uploadedFiles.add(filename);
                }
            }

            Listing listing = new Listing();
            listing.setProduct(product);
            listing.setQuantity(quantity);
            listing.setPrice(price);
            listing.setImageUrls(uploadedFiles);
            listing.setUser(currentUser);

            Listing saved = service.saveListing(listing);

            // Convert to DTO before returning
            ListingDTO dto = new ListingDTO(
                    saved.getId(),
                    saved.getProduct(),
                    saved.getQuantity(),
                    saved.getPrice(),
                    saved.getImageUrls(),
                    saved.getStatus()
            );

            System.out.println("=== RETURNING DTO ===");
            return ResponseEntity.ok(dto); // ← Always return the saved listing
        } catch (Exception e) {
            System.err.println("=== ERROR IN CONTROLLER ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }



    //calling the listings here
    @GetMapping("/myListings")
    public List<Listing> getMyListings(Authentication auth){
        String email = auth.getName();
        return service.findByUserEmail(email);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id, Authentication auth){
        Listing listing = service.findById(id);
        if (!listing.getUser().getEmail().equals(auth.getName())){
            return ResponseEntity.status(403).build();
        }
        service.deleteListing(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Listing> updateListing(
            @PathVariable Long id,
            @RequestPart("product") String product,
            @RequestPart("quantity") String quantity,
            @RequestPart("price") String priceStr,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "existingImages", required = false) String existingImagesJson,
            Authentication auth) throws IOException {

        // 1. Find and secure the listing
        Listing existingListing = service.findById(id);
        if (existingListing == null) {
            return ResponseEntity.notFound().build();
        }
        if (!existingListing.getUser().getEmail().equals(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        // 2. Parse price
        Double price = Double.parseDouble(priceStr.trim());

        // 3. Start with images user wants to keep
        List<String> finalImageUrls = new ArrayList<>();

        if (existingImagesJson != null && !existingImagesJson.isBlank()) {
            String cleaned = existingImagesJson.trim();
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
                String[] parts = cleaned.split(",");
                for (String part : parts) {
                    String filename = part.trim().replaceAll("^\"|\"$", ""); // remove quotes
                    if (!filename.isEmpty()) {
                        finalImageUrls.add(filename);
                    }
                }
            }
        }

        // 4. ADD NEW UPLOADED IMAGES (THIS WAS MISSING!)
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + filename);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, file.getBytes());
                    finalImageUrls.add(filename);
                }
            }
        }

        // 5. Update the listing
        existingListing.setProduct(product);
        existingListing.setQuantity(quantity);
        existingListing.setPrice(price);
        existingListing.setImageUrls(finalImageUrls);

        Listing updated = service.saveListing(existingListing);
        return ResponseEntity.ok(updated);
    }}
