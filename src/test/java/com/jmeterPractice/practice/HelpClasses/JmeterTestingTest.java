package com.jmeterPractice.practice.HelpClasses;

import org.junit.Test;

import static org.junit.Assert.*;

public class JmeterTestingTest {
    @Test
    public void start() throws Exception {
        JmeterTesting testing = new JmeterTesting();
        testing.start();
    }

}