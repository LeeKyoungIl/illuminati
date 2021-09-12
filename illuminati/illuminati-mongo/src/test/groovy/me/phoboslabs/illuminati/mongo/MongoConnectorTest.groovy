package me.phoboslabs.illuminati.mongo

import com.mongodb.client.MongoClient
import me.phoboslabs.illuminati.common.util.exception.ServerIsNotAvailableException
import org.bson.Document
import spock.lang.Specification

class MongoConnectorTest extends Specification {

    private IlluminatiMongoConnector illuminatiMongoConnector

    def setup() {
        System.setProperty("spring.profiles.active", "test")
        illuminatiMongoConnector = new IlluminatiMongoConnector()

    }

    def "TEST : mongodb server is not available, An Exception is thrown."() {
        given:
        System.setProperty("spring.profiles.active", "error")
        IlluminatiMongoConnector illuminatiMongoConnectorError = new IlluminatiMongoConnector()

        when:
        illuminatiMongoConnectorError.mongoClient()

        then:
        System.setProperty("spring.profiles.active", "test")
        thrown ServerIsNotAvailableException
    }

    def "TEST : mongodb setting user info does not exist."() {
        given:
        System.setProperty("spring.profiles.active", "notuserinfo")

        when:
        IlluminatiMongoConnector illuminatiMongoConnectorNotUserInfo = new IlluminatiMongoConnector()

        then:
        illuminatiMongoConnectorNotUserInfo.isExistUserAuthInfo() == false
    }

    def "TEST : mongodb setting user info exist."() {
        given:
        System.setProperty("spring.profiles.active", "test")

        when:
        IlluminatiMongoConnector illuminatiMongoConnectorNotUserInfo = new IlluminatiMongoConnector()

        then:
        illuminatiMongoConnectorNotUserInfo.isExistUserAuthInfo() == true
    }

    def "TEST : data write to mongo"() {
        given:
        def databaseMName = "illuminati"

        when:
        MongoClient mongoClient = this.illuminatiMongoConnector.mongoClient()

        then:
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>())
        databases.forEach({ db -> System.out.println(db.toJson()) })
    }

    //IlluminatiTemplateInterfaceModelImpl
}

class User {
    private String name

    User(String name) {
        this.name = name
    }

    public String getName() {
        return this.name;
    }
}