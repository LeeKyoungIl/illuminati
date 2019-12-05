package me.phoboslabs.illuminati.ApiServerSample.service;

import me.phoboslabs.illuminati.ApiServerSample.model.TestModel;
import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import org.springframework.stereotype.Service;


@Service
public class ApiSampleService {

    public String sampleTest () {
            return "illuminati Cool";
    }

    @Illuminati(packageType = PackageType.SERVICE)
    public String sampleTestByObject (TestModel testModel) {
        this.sampleTestByObject1(null);
        return "illuminatu param test";
    }

    @Illuminati(packageType = PackageType.SERVICE)
    public String sampleTestByObject1 (TestModel testModel) {
        return "illuminatu param test";
    }
}
