package com.agriconnect.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
        this.deliveryFee = 10.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }







}
