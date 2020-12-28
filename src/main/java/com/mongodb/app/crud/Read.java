package com.mongodb.app.crud;

import com.mongodb.client.*;
import com.mongodb.app.consts.Config;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

@SuppressWarnings("ALL")
public class Read {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(Config.MONGO_DB.MONGODB_URI)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase(Config.MONGO_DB.DB_NAME);
            MongoCollection<Document> companiesCollection = sampleTrainingDB.getCollection(Config.MONGO_DB.COLLECTION_NAME);

            // find one document
            findOneDocument(companiesCollection, "5fdb62cd0f29a9582b1dbbd5");

           // find one document with Filters.eq()
            findOneDocumentWithFilters(companiesCollection, "5fdb62cd0f29a9582b1dbbd5");

            // find a list of documents and iterate throw it using an iterator.
            findListOfDocument(companiesCollection, "name template test");

            // find a list of documents and print using a consumer
            System.out.println("Student list using a Consumer:");
            Consumer<Document> printConsumer = document -> System.out.println(document.toJson());
            findListOfDocument(companiesCollection, "name template test").forEach(printConsumer);

            // find a list of documents with sort, skip, limit and projection
            findListOfDocumentsWithSortSkipLimitAndProjection(companiesCollection, "name template");
        }
    }

    private static Document findOneDocument (MongoCollection<Document> collection, String id) {
        Document document = collection.find(new Document(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id))).first();

        if(Objects.isNull(document)) document = new Document();
        System.out.println("findOneDocument document: " + document.toJson());
        return document;
    }

    private static Document findOneDocumentWithFilters (MongoCollection<Document> collection, String id) {
        Document document = collection.find(eq(Config.COMPANY_PARAMS.ID_PARAM, new ObjectId(id))).first();

        if(Objects.isNull(document)) document = new Document();
        System.out.println("findOneDocumentWithFilters document: " + document.toJson());
        return document;
    }

    private static FindIterable<Document> findListOfDocument (MongoCollection<Document> collection, String name) {
        //gte selects the documents where the value of the field is greater than or equal to (i.e. >=) a specified value
        FindIterable<Document> iterable = collection.find(gte(Config.COMPANY_PARAMS.NAME_PARAM, name));

        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Company list with a cursor: ");
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }

        return iterable;
    }

    private static List<Document> findListOfDocumentsWithSortSkipLimitAndProjection (MongoCollection<Document> collection, String name) {
        List<Document> docs = collection.find(and(eq(Config.COMPANY_PARAMS.NAME_PARAM, name), lte(Config.COMPANY_PARAMS.NUMBER_OF_EMPLOYEES_PARAM, 20)))
                .projection(fields(excludeId(), include(Config.COMPANY_PARAMS.ID_PARAM, Config.COMPANY_PARAMS.PHONE_NUMBER_PARAM)))
                .sort(descending(Config.COMPANY_PARAMS.NUMBER_OF_EMPLOYEES_PARAM))
                .skip(1)
                .limit(2)
                .into(new ArrayList<>());

        System.out.println("Company sorted, skipped, limited and projected: ");
        for (Document doc : docs) {
            System.out.println(doc.toJson());
        }
        return docs;
    }
}
