package com.jmeterPractice.practice.HelpClasses;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JmeterTesting
{
    private StandardJMeterEngine jmeter;

    /**
     * Начало теста
     */
    public void start() {

        //JMeter Engine
        jmeter = new StandardJMeterEngine();

        JMeterUtils.loadJMeterProperties("jmeter.properties"); // местоположение файла (не менять)
        JMeterUtils.initLocale();

        // HTTP Sampler
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setName("httpSampler");
        httpSampler.setProtocol("https");
        httpSampler.setDomain("youtube.com");
        httpSampler.setPath("/");
        httpSampler.setMethod("GET");
        httpSampler.setFollowRedirects(true);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setName("loopController");
        loopController.setLoops(1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("threadGroup");
        threadGroup.setNumThreads(10);
        threadGroup.setRampUp(10);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        // Test Plan
        TestPlan testPlan = new TestPlan("testPlan");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // JMeter HashTree
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSampler);

        // Run Test Plan
        jmeter.configure(testPlanTree);

        int threadCount = 20; // число потоков на старте
        long startTime, finishTime, temp; // просмотр по времени
        do {
            threadGroup.setNumThreads(threadCount);
            startTime = System.nanoTime(); // стартовое время
            jmeter.run();
            finishTime = System.nanoTime(); // окончание теста
            threadCount += 10;
            temp = finishTime - startTime;
        } while ( temp < 11 * Math.pow(10, 9)); /* общее время стартового прохода теста 10.5 сек.
         еще 0.5 сек. свидетельствуют о пропущенных запросах */

        // снижение числа потоков и запуск Jmeter навсегда (работает)
        threadCount -= 20;
        loopController.setContinueForever(true);
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
