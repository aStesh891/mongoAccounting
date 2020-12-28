package com.mongodb.app.models;

import lombok.Data;

@Data
public class Product {
    private String name;
    private String link;

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Product setLink(String link) {
        this.link = link;
        return this;
    }
}
