package me.phoboslabs.illuminati.ApiServerSample.service;

import me.phoboslabs.illuminati.ApiServerSample.model.TestModel;
import me.phoboslabs.illuminati.client.annotation.Illuminati;
import org.springframework.stereotype.Service;

@Illuminati
@Service
public class ApiSampleService {

    public String sampleTest () {
            return "illuminati Cool";
    }

    public String sampleTestByObject (TestModel testModel) {
        return "illuminatu param test";
    }
}
