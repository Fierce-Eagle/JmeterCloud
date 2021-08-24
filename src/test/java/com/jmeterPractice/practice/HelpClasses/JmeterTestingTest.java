package com.jmeterPractice.practice.HelpClasses;

import org.junit.Test;

import static org.junit.Assert.*;

public class JmeterTestingTest {

    @Test
    public void start() {
        JmeterTesting jmeterTest = JmeterTesting.getJmeterTest();
        jmeterTest.start();
    }

}