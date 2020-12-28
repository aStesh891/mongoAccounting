package com.mongodb.app.crud;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

@SuppressWarnings("ConstantConditions")
public class Delete {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(Config.MONGO_DB.MONGODB_URI)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase(Config.MONGO_DB.DB_NAME);
            MongoCollection<Document> companiesCollection = sampleTrainingDB.getCollection(Config.MONGO_DB.COLLECTION_NAME);

            // delete one document
            Bson filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId("5fdb62cd0f29a9582b1dbbd2"));
            DeleteResult result = companiesCollection.deleteOne(filter);
            System.out.println(result);

           // findOneAndDelete operation
            filter = eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId("5fdb62cd0f29a9582b1dbbd6"));
            Document doc = companiesCollection.findOneAndDelete(filter);
            if(Objects.nonNull(doc)) System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));

           // delete many documents
            filter = gte(Config.COMPANY_PARAMS.NUMBER_OF_EMPLOYEES_PARAM, 91);
            result = companiesCollection.deleteMany(filter);
            System.out.println(result);

            // delete the entire collection and its metadata (indexes, chunk metadata, etc).
            //companiesCollection.drop();
        }
    }
}
