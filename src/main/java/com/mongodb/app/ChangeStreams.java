package com.mongodb.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.app.models.Company;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.app.consts.Config;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ChangeStreams {

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
            List<Bson> pipeline;

            // => 1: print all the write operations (start "ChangeStreams" then "MappingPOJOs" to see)
            //companies.watch().forEach(printEvent());

            // => 2: print only insert and delete operations.
            // pipeline = singletonList(match(in("operationType", asList("insert", "delete"))));
            // companies.watch(pipeline).forEach(printEvent());

            // => 3: print only updates without fullDocument (start "ChangeStreams" then "Update")
            // pipeline = singletonList(match(eq("operationType", "update")));
            // companies.watch(pipeline).forEach(printEvent());

            // => 4: print only updates with fullDocument (start "ChangeStreams" then "Update")
            // pipeline = singletonList(match(eq("operationType", "update")));
            // companies.watch(pipeline).fullDocument(UPDATE_LOOKUP).forEach(printEvent());

            //5: iterating using a cursor and a while loop + remembering a resumeToken then restart the Change Streams (start "ChangeStreams" then "Update")
            exampleWithResumeToken(companies);
        }
    }

    private static void exampleWithResumeToken(MongoCollection<Company> companies) {
        List<Bson> pipeline = singletonList(match(eq("operationType", "update")));
        ChangeStreamIterable<Company> changeStream = companies.watch(pipeline);
        MongoChangeStreamCursor<ChangeStreamDocument<Company>> cursor = changeStream.cursor();
        System.out.println("==> Going through the stream a first time, record a resumeToken");

        int indexOfOperationToRestartFrom = 5;
        int indexOfIncident = 8;
        int counter = 0;
        BsonDocument resumeToken = null;
        while (cursor.hasNext() && counter != indexOfIncident) {
            ChangeStreamDocument<Company> event = cursor.next();
            if (indexOfOperationToRestartFrom == counter) {
                resumeToken = event.getResumeToken();
            }
            System.out.println(event);
            counter++;
        }
        System.out.println("==> If something happened wrong and I need to restart my Change Stream.");
        System.out.println("==> Starting from resumeToken=" + resumeToken);
        assert resumeToken != null;
        companies.watch(pipeline).resumeAfter(resumeToken).forEach(printEvent());
    }

    private static Consumer<ChangeStreamDocument<Company>> printEvent() {
        return System.out::println;
    }
}
