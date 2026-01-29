package com.agriconnect.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn( name = "consumer_id", nullable = false)
    private User consumer;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "delivery_notes", length = 1000)
    private String deliveryNotes;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;//cash/eft/card/payfast/stripe

    @Column(name = "payment_status")
    private String paymentStatus; // pending/failed/paid

    @Column(name = "payment_reference")
    private String paymentReference; // transaction ID from payment gateway

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "delivery_fee")
    private Double deliveryFee = 10.0; //This soon to change, well be using live location api

    @Column(name = "status", nullable = false)
    private String status; //Pending / confirmed/ in-Transit,Delivered,/cancelled

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    //Constructors
    public Order () {};

    public Order (User consumer, User farmer, String deliveryAddress, String contactNumber,
                  String deliveryNotes, String paymentMethod, Double totalPrice){
        this.consumer = consumer;
        this.farmer = farmer;
        this.deliveryAddress = deliveryAddress;
        this.contactNumber = contactNumber;
        this.deliveryNotes = deliveryNotes;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.status = "Pending";
        this.paymentStatus = paymentMethod.equals("cash") ? "pending" : "pending";
        this.deliveryFee = 10.0;            //subjected to change by km to be travelled
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //Helper methods
    public void addItem(OrderItem item){
        items.add(item);
        item.setOrder(this);
    }

    public Double getGrandTotal(){
        return totalPrice + deliveryFee;
    }

    //Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public User getConsumer(){
        return consumer;
    }

    public void setConsumer(User consumer){
        this.consumer = consumer;
    }

    public User getFarmer(){
        return farmer;
    }

    public void setFarmer(User farmer){
        this.farmer = farmer;
    }

    public List<OrderItem> getItems(){
        return items;
    }

    public void setItems(List<OrderItem> items){
        this.items = items;
    }

    public String getDeliveryAddress(){
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress){
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber(){
        return contactNumber;
    }

    public void setContactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }

    public String getDeliveryNotes(){
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes){
        this.deliveryNotes = deliveryNotes;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus(){
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus){
        this.paymentStatus = paymentStatus;
    }

public String getPaymentReference(){
        return paymentReference;
}

public void setPaymentReference(String paymentReference){
        this.paymentReference = paymentReference;
}

public Double getTotalPrice(){
        return totalPrice;
}

public void setTotalPrice(Double totalPrice){
        this.totalPrice = totalPrice;
}

public Double getDeliveryFee(){
        return deliveryFee;
}

public void setDeliveryFee(Double deliveryFee){
        this.deliveryFee = deliveryFee;
}

public String getStatus(){
        return status;
}

public void setStatus(String status){
        this.status = status;
}

public String getCancellationReason(){
        return cancellationReason;
}

public void setCancellationReason(String cancellationReason){
        this.cancellationReason = cancellationReason;
}

public LocalDateTime getCreatedAt(){
        return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
}

public LocalDateTime getUpdatedAt(){
        return updatedAt;
}

public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
}

public LocalDateTime getConfirmedAt(){
        return confirmedAt;
}

public void setConfirmedAt(LocalDateTime confirmedAt){
        this.confirmedAt = confirmedAt;
}

public LocalDateTime getDeliveredAt(){
        return deliveredAt;
}

public void setDeliveredAt(LocalDateTime deliveredAt){
        this.deliveredAt = deliveredAt;
}

@PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
}
@PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
}
}
