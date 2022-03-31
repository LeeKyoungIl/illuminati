package me.phoboslabs.illuminati.processor.test;

import me.phoboslabs.illuminati.annotation.Illuminati;

public class TestServiceImpl implements TestService {

    @Illuminati(isTest = true)
    @Override
    public Test getTest(String inputString, int inputInteger) {
        return new Test();
    }

    @Illuminati(isTest = true)
    @Override
    public Test getTestSample() {
        return new Test();
    }
}
