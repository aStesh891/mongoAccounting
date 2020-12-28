package com.mongodb.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.app.models.Company;
import com.mongodb.app.models.Product;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MappingPOJO {

    public static void main(String[] args) {
        ConnectionString connectionString = new ConnectionString(Config.MONGO_DB.MONGODB_URI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                                                                .applyConnectionString(connectionString)
                                                                .codecRegistry(codecRegistry)
                                                                .build();

        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase db = mongoClient.getDatabase(Config.MONGO_DB.DB_NAME);

            MongoCollection<Company> companies = db.getCollection(Config.MONGO_DB.COLLECTION_NAME, Company.class);

            // create a new company
            Company newCompany = new Company();
            newCompany.setName("company 2");
            newCompany.setBlogUrl("http://www.blog-pattern1.com");
            newCompany.setEmailAddress("email2@gmail.com");
            newCompany.setDescription("description text");
            newCompany.setNumberOfEmployees(15);
            newCompany.setPhoneNumber("380505055111");
            newCompany.setProducts(singletonList(new Product().setName("product #3").setLink("http://www.link3.com")));
            companies.insertOne(newCompany);
            System.out.println("Company inserted.");

            // find this company
            Company company = companies.find(eq(Config.COMPANY_PARAMS.NAME_PARAM, "company 2")).first();
            System.out.println("Company found:\t" + company);

            // update this company: adding a product
            List<Product> newProducts = new ArrayList<Product>(company.getProducts());
            newProducts.add(new Product().setName("product #4").setLink("http://www.link4.com"));
            company.setProducts(newProducts);
            Document filterByCompanyId = new Document("_id", company.getId());
            FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
            Company updatedCompany = companies.findOneAndReplace(filterByCompanyId, company, returnDocAfterReplace);
            System.out.println("Company replaced:\t" + updatedCompany);

            // delete this company
            System.out.println("Company deleted:\t" + companies.deleteOne(filterByCompanyId));
        }
    }
}
