package com.leekyoungil.illuminati.ApiServerSample.service;

import com.leekyoungil.illuminati.ApiServerSample.model.TestModel;
import com.leekyoungil.illuminati.client.annotation.Illuminati;
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
