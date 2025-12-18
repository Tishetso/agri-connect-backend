package com.agriconnect.backend.dto;

import java.util.List;

public class ListingDTO {
    private Long id;
    private String product;
    private String quantity;
    private Double price;
    private List<String> imageUrls;
    private String status;

    public ListingDTO(Long id, String product, String quantity, Double price, List<String> imageUrls, String status){
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.imageUrls = imageUrls;
        this.status = status;
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
}
