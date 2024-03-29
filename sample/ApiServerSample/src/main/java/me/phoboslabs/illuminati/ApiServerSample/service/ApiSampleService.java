package me.phoboslabs.illuminati.ApiServerSample.service;

import lombok.extern.slf4j.Slf4j;
import me.phoboslabs.illuminati.ApiServerSample.model.SampleModel;
import me.phoboslabs.illuminati.ApiServerSample.model.TestModel;
import me.phoboslabs.illuminati.ApiServerSample.service1.ApiSampleService1;
import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ApiSampleService {

    @Autowired
    private ApiSampleService1 apiSampleService1;

    public String sampleTest () {
            return "illuminati Cool";
    }

    @Illuminati(packageType = PackageType.SERVICE, ignoreProfile = {"local"})
    public String sampleTestByObject (TestModel testModel) {
//        this.sampleTestByObject1(null);
//        apiSampleService1.sampleTestByObject1(null);
        return "illuminatu param test";
    }

    @Illuminati(packageType = PackageType.SERVICE, ignoreProfile = {"dev"})
    public TestModel sampleTestByObjectTestReturn (TestModel testModel, SampleModel sampleModel) {
        testModel.setName("model");
        return testModel;
    }

    @Illuminati(packageType = PackageType.SERVICE)
    public String sampleTestByObject1 (TestModel testModel) {
        return "illuminatu param test";
    }
}
