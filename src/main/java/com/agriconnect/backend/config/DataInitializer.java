package com.agriconnect.backend.config;

import com.agriconnect.backend.model.User;
import com.agriconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args){
        if (userRepository.findByEmail(adminEmail).isEmpty()){
            User admin = new User();
            admin.setName("Admin");
            admin.setSurname("AgriConnect");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            admin.setIdNumber("0000000000000");
            admin.setRegion("Admin");
            userRepository.save(admin);
            System.out.println("=== Admin account created: " + adminEmail);
        }else{
            System.out.println("=== Admin account already exists");
        }
    }





}