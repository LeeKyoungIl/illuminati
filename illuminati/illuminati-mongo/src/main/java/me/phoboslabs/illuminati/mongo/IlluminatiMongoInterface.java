package me.phoboslabs.illuminati.mongo;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import org.bson.BsonValue;
import org.bson.Document;

import java.lang.reflect.Type;

public class IlluminatiMongoInterface {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    private static final String ILLUMINATI_DATABASE_NAME = "illuminati";
    private static final String ILLUMINATI_COLLECTION_NAME = "illuminati";

    private static final Type ILLUMINATI_INTERFACE_MODEL_TYPE = new TypeToken<IlluminatiTemplateInterfaceModelImpl>() {}.getType();

    public IlluminatiMongoInterface(MongoClient mongoClient) {
        if (mongoClient == null) {
            throw new IllegalArgumentException("mongoClient must not be null.");
        }
        this.mongoClient = mongoClient;
        this.mongoDatabase = this.mongoClient.getDatabase(ILLUMINATI_DATABASE_NAME);

        if (this.existCollection() == false) {
            this.createCollection();
        }
    }

    public boolean save(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(ILLUMINATI_COLLECTION_NAME);
        String jsonString = IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(illuminatiTemplateInterfaceModel);
        Document doc = Document.parse(jsonString);
        InsertOneResult insertOneResult = collection.insertOne(doc);
        BsonValue value = insertOneResult.getInsertedId();
        return value.toString() != null;
    }

    private boolean existCollection() {
        return this.mongoDatabase.getCollection(ILLUMINATI_COLLECTION_NAME) != null;
    }

    private void createCollection() {
        this.mongoDatabase.createCollection(ILLUMINATI_COLLECTION_NAME);
        boolean created = this.existCollection();
        if (created == false) {
            throw new IllegalArgumentException("create collection has failed.");
        }
    }
}
