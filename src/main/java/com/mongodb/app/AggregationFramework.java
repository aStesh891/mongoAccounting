package com.mongodb.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

public class AggregationFramework {

    public static void main(String[] args) {
        String connectionString = Config.MONGO_DB.MONGODB_URI;
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase db = mongoClient.getDatabase("sample_training");
            MongoCollection<Document> companies = db.getCollection(Config.MONGO_DB.COLLECTION_NAME);
            threeBiggestSocialCompanies(companies);
        }
    }

    private static void threeBiggestSocialCompanies(MongoCollection<Document> companies) {
        Bson match = match(eq("category_code", "social"));
        Bson project = project(fields(excludeId(), include("number_of_employees", "name")));
        Bson sort = sort(descending("number_of_employees"));
        Bson limit = limit(4);

        List<Document> results = companies.aggregate(Arrays.asList(match, project, sort, limit))
                                     .into(new ArrayList<>());
        System.out.println("==> 3 biggest social companies");
        results.forEach(printDocuments());
    }

    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
    }
}
