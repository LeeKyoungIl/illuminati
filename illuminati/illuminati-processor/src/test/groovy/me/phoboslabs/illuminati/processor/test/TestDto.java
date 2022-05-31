package me.phoboslabs.illuminati.processor.test;

public class TestDto {

    private String inputString;
    private int inputInteger;

    private String testValue = "testValue";

    public String getTestValue() {
        return this.testValue;
    }

    public void setValue(String inputString, int inputInteger) {
        this.inputString = inputString;
        this.inputInteger = inputInteger;
    }
}
