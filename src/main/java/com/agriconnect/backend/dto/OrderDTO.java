package com.agriconnect.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private String farmerName;
    private String farmerRegion;
    private Long farmerId;
    private String consumerName;
    private Long consumerId;
    private List<OrderItemDTO> items;
    private String deliveryAddress;
    private String contactNumber;
    private String deliveryNotes;
    private String paymentMethod;
    private String paymentStatus;
    private Double totalPrice;
    private Double deliveryFee;
    private Double grandTotal;
    private String status;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime deliveredAt;

    //Constructor
    public OrderDTO(){}

    public OrderDTO(Long id, String farmerName, String farmerRegion, Long farmerId, String consumerName,
                    Long consumerId, List<OrderItemDTO> items, String deliveryAddress, String contactNumber,
                    String deliveryNotes, String paymentMethod, String paymentStatus, Double totalPrice,
                    Double deliveryFee, Double grandTotal, String status, String cancellationReason,
                    LocalDateTime createdAt, LocalDateTime confirmedAt, LocalDateTime deliveredAt){
        this.id = id;
        this.farmerName = farmerName;
        this.farmerRegion = farmerRegion;
        this.farmerId = farmerId;
        this.consumerName = consumerName;
        this.consumerId = consumerId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.contactNumber = contactNumber;
        this.deliveryNotes = deliveryNotes;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.grandTotal = grandTotal;
        this.status = status;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
        this.deliveredAt = deliveredAt;



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

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }



}
