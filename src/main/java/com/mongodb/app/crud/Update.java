package com.mongodb.app.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class Update {
    static JsonWriterSettings prettyPrint = JsonWriterSettings.builder().indent(true).build();

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(Config.MONGO_DB.MONGODB_URI)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase(Config.MONGO_DB.DB_NAME);
            MongoCollection<Document> companiesCollection = sampleTrainingDB.getCollection(Config.MONGO_DB.COLLECTION_NAME);

            // update one document
            updateOneDocument(companiesCollection, "5fdb62cd0f29a9582b1dbbd5");

            // upsert
            upsert(companiesCollection, "5fdb62cd0f29a9582b1dbbd5", "name template");

            // update many documents
            updateManyDocuments(companiesCollection, "5fdb62cd0f29a9582b1dbbd5");

            // findOneAndUpdate
            findOneAndUpdate(companiesCollection, "5fdb62cd0f29a9582b1dbbd5");
        }
    }

    private static void updateOneDocument (MongoCollection<Document> collection, String id) {
        Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id));
        Bson updateOperation = set("comment", "For sale!");
        UpdateResult updateResult = collection.updateOne(filter, updateOperation);
        System.out.println("=> Updating the doc with {\"_id\": "+ id + "}. Adding comment.");
        System.out.println(collection.find(filter).first().toJson(prettyPrint));
        System.out.println(updateResult);
    }

    private static void upsert (MongoCollection<Document> collection, String id, String name) {
        Bson filter = and(eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id)), eq(Config.COMPANY_PARAMS.NAME_PARAM, name));
        Bson updateOperation = push("comments", "The product is out of stock!");
        UpdateOptions options = new UpdateOptions().upsert(true);
        UpdateResult updateResult = collection.updateOne(filter, updateOperation, options);
        System.out.println("\n=> Upsert document with {\"_id\" " + id + "\"name\"" + name + "} because it doesn't exist yet.");
        System.out.println(updateResult);
        System.out.println(collection.find(filter).first().toJson(prettyPrint));
    }

    private static void updateManyDocuments (MongoCollection<Document> collection, String id) {
        Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id));
        Bson updateOperation = set("comment", "For sale!");
        UpdateResult updateResult = collection.updateMany(filter, updateOperation);
        System.out.println("\n=> Updating all the documents with {\"_id\":" + id + "}.");
        System.out.println(updateResult);
    }

    private static void findOneAndUpdate (MongoCollection<Document> collection, String id) {
        Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id));
        Bson update1 = inc(Config.COMPANY_PARAMS.NUMBER_OF_EMPLOYEES_PARAM, 10);  // increment by 10. As param doesn't exist yet, param=10
        Bson update2 = rename("emailAddress", "newEmailAddress"); // rename variable
        Bson update3 = addToSet("comments", "This comment is uniq");    // creating an array with a comment
        Bson update4 = addToSet("comments", "This comment is uniq 0");
        Bson updates = combine(update1, update2, update3, update4);
        UpdateResult updateResult = collection.updateMany(filter, updates);
        System.out.println(updateResult);
    }

    private static void returnOldVersionOfDocumentBeforeUpdate (MongoCollection<Document> collection, String id, Bson updates) {
        Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id));
        Document oldVersion = collection.findOneAndUpdate(filter, updates);
        System.out.println("\n=> FindOneAndUpdate operation. Printing the old version by default:");
        System.out.println(oldVersion.toJson(prettyPrint));
    }

    private static void requestNewVersion (MongoCollection<Document> collection, String id, Bson updates) {
        Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id));
        FindOneAndUpdateOptions optionAfter = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Document newVersion = collection.findOneAndUpdate(filter, updates, optionAfter);
        System.out.println("\n=> FindOneAndUpdate operation. But we can also ask for the new version of the doc:");
        System.out.println(newVersion.toJson(prettyPrint));
    }
}
