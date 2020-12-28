package com.mongodb.app.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@EqualsAndHashCode
public class Company {

    private ObjectId id;

    @BsonProperty(value = "name")
    private String name;

    @BsonProperty(value = "blog_url")
    private String blogUrl;

    @BsonProperty(value = "email_address")
    private String emailAddress;

    @BsonProperty(value = "phone_number")
    private String phoneNumber;

    @BsonProperty(value = "number_of_employees")
    private Integer numberOfEmployees;

    @BsonProperty(value = "description")
    private String description;

    private List<Product> products;
}
