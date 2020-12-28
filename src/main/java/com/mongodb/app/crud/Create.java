package com.mongodb.app.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

public class Create {
    private static final Random rand = new Random();

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(Config.MONGO_DB.MONGODB_URI)) {

            MongoDatabase sampleTrainingDB = mongoClient.getDatabase(Config.MONGO_DB.DB_NAME);
            MongoCollection<Document> gradesCollection = sampleTrainingDB.getCollection(Config.MONGO_DB.COLLECTION_NAME);

            insertOneDocument(gradesCollection);
            //insertManyDocuments(gradesCollection);
        }
    }

    private static void insertOneDocument(MongoCollection<Document> gradesCollection) {
        gradesCollection.insertOne(generateNewCompany());
        System.out.println("One company inserted.");
    }

    private static void insertManyDocuments(MongoCollection<Document> gradesCollection) {
        List<Document> grades = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            grades.add(generateNewCompany());
        }

        gradesCollection.insertMany(grades, new InsertManyOptions().ordered(false));
        System.out.println("Ten companies inserted.");
    }

    private static Document generateNewCompany() {
        List<Document> products = asList(
                new Document(Config.COMPANY_PARAMS.NAME_PARAM, "product name 1").append(Config.COMPANY_PARAMS.LINK_PARAM, "http://www.link1.com"),
                new Document(Config.COMPANY_PARAMS.NAME_PARAM, "product name 2").append(Config.COMPANY_PARAMS.LINK_PARAM, "http://www.link2.com"));

        return new Document("_id", new ObjectId())
                .append(Config.COMPANY_PARAMS.NAME_PARAM, "name template test")
                .append(Config.COMPANY_PARAMS.BLOG_URL_PARAM, "http://www.blog-pattern.com")
                .append(Config.COMPANY_PARAMS.EMAIL_ADDRESS_PARAM, "email1@gmail.com")
                .append(Config.COMPANY_PARAMS.PHONE_NUMBER_PARAM, "380505055555")
                .append(Config.COMPANY_PARAMS.NUMBER_OF_EMPLOYEES_PARAM, Math.round(rand.nextFloat() * 100)+1)
                .append(Config.COMPANY_PARAMS.DESCRIPTION_PARAM, "description text")
                .append(Config.COMPANY_PARAMS.PRODUCTS_PARAM, products);
    }
}
