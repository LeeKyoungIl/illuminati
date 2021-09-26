package me.phoboslabs.illuminati.mongo

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.mongodb.client.MongoClient
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl
import me.phoboslabs.illuminati.common.util.exception.ServerIsNotAvailableException
import org.bson.Document
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Modifier
import java.lang.reflect.Type

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

    @Unroll("#sampleJsonPath")
    def "TEST: parse illuminati data from sample json"() {
        when:
        IlluminatiTemplateInterfaceModelImpl illuminatiInterfaceModel = this.getSampleIlluminatiModel(sampleJsonPath)

        then:
        illuminatiInterfaceModel != null

        where:
        sampleJsonPath << ["illuminati-data.json", "illuminati-data-param.json"]
    }

    def "TEST: save illuminati model to mongodb"() {
        given:
        def sampleJsonPath = "illuminati-data.json"
        IlluminatiTemplateInterfaceModelImpl illuminatiInterfaceModel = this.getSampleIlluminatiModel(sampleJsonPath)
        MongoClient mongoClient = this.illuminatiMongoConnector.mongoClient()

        IlluminatiMongoInterface illuminatiMongoInterface = new IlluminatiMongoInterface(mongoClient)

        when:
        boolean result = illuminatiMongoInterface.save(illuminatiInterfaceModel)

        then:
        result == true
    }

    private static final Type ILLUMINATI_INTERFACE_MODEL_TYPE = new TypeToken<IlluminatiTemplateInterfaceModelImpl>() {}.getType();

    private IlluminatiTemplateInterfaceModelImpl getSampleIlluminatiModel(def fileName) {
        File file = new File(this.getClass().getClassLoader().getResource("sample/".concat(fileName)).getFile())

        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).create()
        JsonReader reader = new JsonReader(new FileReader(file))
        return gson.fromJson(reader, ILLUMINATI_INTERFACE_MODEL_TYPE)
    }
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