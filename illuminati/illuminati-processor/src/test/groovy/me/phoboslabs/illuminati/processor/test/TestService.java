package me.phoboslabs.illuminati.processor.test;

public interface TestService {

    Test getTest(String inputString, int inputInteger);

    Test getTestSample();

    class Test {

        private String testValue = "testValue";

        public String getTestValue() {
            return this.testValue;
        }
    }
}
