package me.phoboslabs.illuminati.mongo

import me.phoboslabs.illuminati.mongo.vo.MongoConnectionProperties
import spock.lang.Specification

class MongoConnectorTest extends Specification {

    private IlluminatiMongoConnector illuminatiMongoConnector

    def setup() {
        System.setProperty("spring.profiles.active", "test")
        illuminatiMongoConnector = new IlluminatiMongoConnector()

    }

    def "TEST : data write to mongo"() {
        given:
        def databaseMName = "illuminati"

        when:
        com.mongodb.client.MongoDatabase mongoDatabase = this.illuminatiMongoConnector.getMongoClient().getDatabase(databaseMName)

        then:
        mongoDatabase != null
    }
}