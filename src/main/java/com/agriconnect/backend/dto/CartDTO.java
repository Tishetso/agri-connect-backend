package com.agriconnect.backend.dto;

import java.util.List;

public class CartDTO {
    private Long id;
    private String farmerName;
    private String farmerRegion;
    private Long farmerId;
    private List<CartItemDTO> items;
    private Double totalPrice;
    private Integer totalItems;

    //Constructor
    public CartDTO() {}

    public CartDTO(Long id, String farmerName, String farmerRegion, Long farmerId,
                   List<CartItemDTO> items, Double totalPrice, Integer totalItems){
        this.id = id;
        this.farmerName = farmerName;
        this.farmerRegion = farmerRegion;
        this.farmerId = farmerId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerRegion() {
        return farmerRegion;
    }

    public void setFarmerRegion(String farmerRegion) {
        this.farmerRegion = farmerRegion;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}
