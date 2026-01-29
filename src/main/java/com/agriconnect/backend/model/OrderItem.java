package com.agriconnect.backend.model;

import jakarta.persistence.*;


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing; //Reference to original listing

    //Snapshot Data - preserved even if listing changes
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    //Constructors
    public OrderItem(){}

    public OrderItem (Order order, Listing listing, String productName,
                      Integer quantity, Double pricePerUnit, Double subtotal){
        this.order = order;
        this.listing = listing;
        this.productName = productName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = subtotal;
    }

    //Getters and Setters

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Order getOrder(){
        return order;
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public Listing getListing(){
        return listing;
    }

    public void setListing(Listing listing){
        this.listing = listing;
    }

    public String getProductName(){
        return productName;
    }

    public void setProductName(String productName){
        this.productName = productName;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }

    public Double getPricePerUnit(){
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit){
        this.pricePerUnit = pricePerUnit;
    }

    public Double getSubtotal(){
        return subtotal;
    }

    public void setSubtotal(Double subtotal){
        this.subtotal = subtotal;
    }
}
