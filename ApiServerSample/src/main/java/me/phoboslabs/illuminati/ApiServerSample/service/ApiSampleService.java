package me.phoboslabs.illuminati.ApiServerSample.service;

import me.phoboslabs.illuminati.ApiServerSample.model.TestModel;
import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import org.springframework.stereotype.Service;

@Illuminati(packageType = PackageType.SERVICE)
@Service
public class ApiSampleService {

    public String sampleTest () {
            return "illuminati Cool";
    }

    public String sampleTestByObject (TestModel testModel) {
        return "illuminatu param test";
    }
}
