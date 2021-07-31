package me.phoboslabs.illuminati.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.phoboslabs.illuminati.mongo.vo.MongoConnectionProperties;
import org.bson.Document;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class IlluminatiMongoConnector {

    private MongoConnectionProperties mongoConnectionProperties;

    public IlluminatiMongoConnector() {
        final String profiles = System.getProperty("spring.profiles.active");
        final Yaml yaml = new Yaml(new Constructor(MongoConnectionProperties.class));
        final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("config/mongo/mongo-"+profiles+".yml");

        if (inputStream == null) {
            throw new IllegalArgumentException("config/mongo/mongo-{phase}.yml not exists.");
        }

        this.mongoConnectionProperties = yaml.load(inputStream);
    }

    private static String ILLUMINATI_DB_NAME = "illuminati";
    private static String ILLUMINATI_COLLECTION_NAME = "illuminati";

    public MongoClient getMongoClient() {
        try (MongoClient mongoClient = MongoClients.create(this.mongoConnectionProperties.getMongoConnectionURI())) {
            MongoDatabase database = mongoClient.getDatabase(ILLUMINATI_DB_NAME);
            if (database == null) {
                throw new IllegalArgumentException("database name "+ ILLUMINATI_DB_NAME + " must exist.");
            }
            return mongoClient;
        }

    }

//    public void tset() {
//        MongoCollection<Document> illuminatiCollection = database.getCollection(ILLUMINATI_COLLECTION_NAME);
//    }
}
