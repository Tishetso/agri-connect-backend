package com.agriconnect.backend.service;


import com.agriconnect.backend.model.Listing;
import com.agriconnect.backend.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ListingService {

    @Autowired
    private ListingRepository repo;

    public Listing saveListing(Listing listing){
        return repo.save(listing);
    }

    public List<Listing> findAll(){
        return repo.findAll();
    }
    public Listing findById(long id){
        return repo.findById(id).orElse(null);
    }

    public void deleteListing(Long id){
        repo.deleteById(id);
    }

    public List<Listing> findByUserEmail(String email){
        return repo.findByUserEmail(email);
    }

}
