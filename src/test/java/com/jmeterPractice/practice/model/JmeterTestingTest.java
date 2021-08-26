package com.jmeterPractice.practice.model;

import com.jmeterPractice.practice.modules.JmeterTesting;
import org.junit.Test;

public class JmeterTestingTest {
    @Test
    public void start() {
        JmeterTesting.getJmeterTest().start();
    }

}