package com.agriconnect.backend.dto;

import java.util.List;

public class MarketplaceDTO {
    private Long id;
    private String product;
    private String quantity;
    private Double price;
    private List<String> imageUrls;
    private String status;
    private String farmerName;
    private String location;
    private Double latitude ;
    private Double longitude ;

    public MarketplaceDTO(Long id, String product, String quantity, Double price,
                          List<String> imageUrls, String status, String farmerName, String location, Double latitude, Double longitude){
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.imageUrls = imageUrls;
        this.status = status;
        this.farmerName = farmerName;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // ✅ ADD THESE GETTERS AND SETTERS
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }


}
