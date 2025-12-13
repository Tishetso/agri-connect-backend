package com.agriconnect.backend.controller;

import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.service.ListingService;
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
    public ResponseEntity<Listing> updateListing(@PathVariable Long id,
                                                 @RequestPart("product") String product,
                                                 @RequestPart("quantity") String quantity,
                                                 @RequestPart("price") String priceStr,//avoiding the octet stream error
                                                 @RequestPart(value = "images", required = false) List<MultipartFile> newImages, //can be empty or missing
                                                 @RequestPart(value = "existingImages", required = false) String existingImagesJson,//optional: list of filenames to keep
                                                 Authentication auth) throws IOException {
        //1. Security check
        Listing existingListing = service.findById(id);
        if (existingListing == null){
            return ResponseEntity.notFound().build();
        }
        if(!existingListing.getUser().getEmail().equals(auth.getName())){
            return ResponseEntity.status(403).build();
        }

        //2.Parse price
        Double price = Double.parseDouble(priceStr.trim());

        //3. Handle images
        List<String> finalImageUrls = new ArrayList<>();

        //keep existing images that user wants to retain ( sent as JSON array of filenames
        if (existingImagesJson != null && !existingImagesJson.isBlank()){
            String cleaned = existingImagesJson.trim();
            if(cleaned.startsWith("[") && cleaned.endsWith("]")){
                cleaned = cleaned.substring(1, cleaned.length() - 1);
                String[] parts = cleaned.split(",");
                for(String part : parts){
                    String filename = part.trim().replace("\"","");
                    if (!filename.isEmpty()){
                        finalImageUrls.add(filename);
                    }
                }
            }
        }

        return ResponseEntity.ok;
    }




}
