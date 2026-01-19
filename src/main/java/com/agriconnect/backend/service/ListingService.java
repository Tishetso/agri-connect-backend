package com.agriconnect.backend.service;


import com.agriconnect.backend.dto.MarketplaceDTO;
import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListingService {

    @Autowired
    private ListingRepository repo;

    public Listing saveListing(Listing listing){
        return repo.save(listing);
    }

    @Transactional(readOnly = true)//Read only for query methods
    public List<Listing> findAll(){
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Listing findById(long id){
        return repo.findById(id).orElse(null);
    }

    public void deleteListing(Long id){
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Listing> findByUserEmail(String email){
        return repo.findByUserEmail(email);
    }

    //get Available listings for marketplace
    public List<MarketplaceDTO> getAllAvailableListings(){
        List <Listing> listings = repo.findAllAvailableListingsWithUser();

        return listings.stream().map(listing -> {
            String farmerName = listing.getUser().getName() + " " + listing.getUser().getSurname();
            String location = listing.getUser().getRegion();

            return new MarketplaceDTO(
                    listing.getId(),
                    listing.getProduct(),
                    listing.getQuantity(),
                    listing.getPrice(),
                    listing.getImageUrls(),
                    listing.getStatus(),
                    farmerName,
                    location
            );

        }).collect(Collectors.toList());
    }

}
