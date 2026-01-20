package com.agriconnect.backend.dto;

import java.util.List;

public class CartItemDTO {
    private Long id;
    private Long listingId;
    private String productName;
    private Double pricePerUnit;
    private Integer quantity;
    private Double subtotal;
    private List<String> imageUrls;

    //Constructor
    public CartItemDTO(){}

    public CartItemDTO(Long id, Long listingId, String productName, Double pricePerUnit,
                       Integer quantity, Double subtotal, List<String> imageUrls){
        this.id = id;
        this.listingId = listingId;
        this.productName = productName;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.imageUrls = imageUrls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
