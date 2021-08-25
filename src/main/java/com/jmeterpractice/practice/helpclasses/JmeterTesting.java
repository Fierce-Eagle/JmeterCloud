package com.jmeterpractice.practice.helpclasses;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JmeterTesting
{
    /**
     * Создание статического метода для вызоа одного и того же объекта всегда
     * Так как конструктор private, то создать еще экземпляров не получиться
     * @return always same instance
     */
    public static JmeterTesting getJmeterTest () {
        return test;
    }

    /**
     * Конструктор
     */
    private JmeterTesting () {}
    private static JmeterTesting test = new JmeterTesting();
    private StandardJMeterEngine jmeter;
    private String domain = "youtube.com";
    private String protocol = "https";

    public String getDomain() {
        return domain;
    }

    public String getProtocol() {
        return  protocol;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Начало теста
     */
    public void start()
    {

        //JMeter Engine
        jmeter = new StandardJMeterEngine();

        JMeterUtils.loadJMeterProperties("jmeter.properties"); // местоположение файла (не менять)
        JMeterUtils.initLocale();

        // HTTP Sampler
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setName("httpSampler");
        httpSampler.setProtocol(protocol);
        httpSampler.setDomain(domain);
        httpSampler.setPath("/");
        httpSampler.setMethod("GET");
        httpSampler.setFollowRedirects(true);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setName("loopController");
        loopController.setLoops(1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("threadGroup");
        threadGroup.setNumThreads(10);
        threadGroup.setRampUp(10);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());


        // Test Plan
        TestPlan testPlan = new TestPlan("testPlan");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // JMeter HashTree
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSampler);

        Summariser summer = new Summariser("summary");

        // Store execution results into a .jtl file
        String logFile = "example.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run Test Plan
        jmeter.configure(testPlanTree);

        jmeter.run();
    }

    /**
     * Остановка теста
     */
    public void stop()
    {
        jmeter.stopTest(true);
    }
}