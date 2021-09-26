package me.phoboslabs.illuminati.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.util.NetworkUtil;
import me.phoboslabs.illuminati.common.util.exception.ServerIsNotAvailableException;
import me.phoboslabs.illuminati.mongo.properties.MongoConnectionProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

public class IlluminatiMongoConnector {

    private MongoConnectionProperties mongoConnectionProperties;

    private boolean existUserAuthInfo = false;

    public IlluminatiMongoConnector() {
        final String profiles = System.getProperty("spring.profiles.active");
        final Yaml yaml = new Yaml(new Constructor(MongoConnectionProperties.class));
        yaml.setBeanAccess(BeanAccess.FIELD);
        final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("config/mongo/mongo-"+profiles+".yml");

        if (inputStream == null) {
            throw new IllegalArgumentException("config/mongo/mongo-{phase}.yml not exists.");
        }

        this.mongoConnectionProperties = yaml.load(inputStream);

        try {
            this.mongoConnectionProperties.getMongoInfo().validateUserAuthInfo();
            this.existUserAuthInfo = true;
        } catch (IllegalArgumentException ignore) {}
    }

    public boolean isExistUserAuthInfo() {
        return this.existUserAuthInfo;
    }

    private static String ILLUMINATI_DB_NAME = "illuminati";

    protected String getDatabaseName() {
        return ILLUMINATI_DB_NAME;
    }

    private static String ILLUMINATI_COLLECTION_NAME = "illuminati";

    public MongoClient mongoClient() {
        // check mongodb is alive.
        this.isMongoAlive();

        MongoCredential credential = null;
        if (this.existUserAuthInfo) {
            MongoConnectionProperties.MongoInfo mongoInfo = this.mongoConnectionProperties.getMongoInfo();
            credential = MongoCredential.createCredential(mongoInfo.getUsername(), ILLUMINATI_DB_NAME, mongoInfo.getPassword());
        }

        ConnectionString connectionString = new ConnectionString(this.mongoConnectionProperties.getMongoConnectionURI());
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    private void isMongoAlive() {
        MongoConnectionProperties.MongoInfo mongoInfo = this.mongoConnectionProperties.getMongoInfo();
        if (NetworkUtil.canIConnect(mongoInfo.getHostAddress(), mongoInfo.getPort()) == false) {
            throw new ServerIsNotAvailableException("MongoDB is not available, Check network or Server is alive.");
        }
    }

    public Collection getMappingBasePackages() {
        return Collections.singleton("me.phoboslabs.illuminati");
    }

//    public MongoClient getMongoClient() {
//        try (MongoClient mongoClient = MongoClients.create(this.mongoConnectionProperties.getMongoConnectionURI())) {
//            MongoDatabase database = mongoClient.getDatabase(ILLUMINATI_DB_NAME);
//            if (database == null) {
//                throw new IllegalArgumentException("database name "+ ILLUMINATI_DB_NAME + " must exist.");
//            }
//            return mongoClient;
//        }
//
//    }

//    public void tset() {
//        MongoCollection<Document> illuminatiCollection = database.getCollection(ILLUMINATI_COLLECTION_NAME);
//    }
}
