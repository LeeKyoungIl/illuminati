package me.phoboslabs.illuminati.ApiServerSample.service1;

import me.phoboslabs.illuminati.ApiServerSample.model.TestModel;
import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import org.springframework.stereotype.Service;


@Service
public class ApiSampleService1 {

    public String sampleTest () {
            return "illuminati Cool";
    }

    @Illuminati(packageType = PackageType.SERVICE)
    public String sampleTestByObject1 (TestModel testModel) {
        this.sampleTestByObject12(null);
        return "illuminatu param test";
    }

    @Illuminati(packageType = PackageType.SERVICE)
    public String sampleTestByObject12 (TestModel testModel) {
        return "illuminatu param test";
    }
}
