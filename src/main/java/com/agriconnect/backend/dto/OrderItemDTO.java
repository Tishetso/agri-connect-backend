package com.agriconnect.backend.dto;

public class OrderItemDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private Double pricePerUnit;
    private Double subtotal;

    //Constructor
    public OrderItemDTO(){}

    public OrderItemDTO(Long id, String productName, Integer quantity, Double pricePerUnit, Double subtotal){
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = subtotal;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
