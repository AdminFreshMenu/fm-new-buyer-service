package com.buyer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_status")
public class OrderStatus extends AbstractMutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String displayName;
    private String message;
    private String altDisplayName;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAltDisplayName() {
        return altDisplayName;
    }

    public void setAltDisplayName(String altDisplayName) {
        this.altDisplayName = altDisplayName;
    }   
}