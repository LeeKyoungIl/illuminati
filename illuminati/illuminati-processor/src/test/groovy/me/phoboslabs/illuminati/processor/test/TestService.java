package me.phoboslabs.illuminati.processor.test;

import me.phoboslabs.illuminati.annotation.Illuminati;

public class TestService {

    @Illuminati(isTest = true, samplingRate = 90)
    public TestDto getTest(String inputString, int inputInteger) {
        TestDto testDto = new TestDto();
        testDto.setValue(inputString, inputInteger);
        return testDto;
    }
}
