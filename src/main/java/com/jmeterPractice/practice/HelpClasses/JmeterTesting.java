package com.jmeterPractice.practice.HelpClasses;

import kg.apc.jmeter.reporters.AutoStop;
import kg.apc.jmeter.threads.UltimateThreadGroup;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.sampler.HttpWebdav;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.ListenerResultWrapper;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.SummaryReport;
import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
import org.apache.jorphan.collections.HashTree;


import java.io.FileOutputStream;
import java.net.URL;

public class JmeterTesting
{
    private StandardJMeterEngine jmeter;

    /**
     * Начало теста
     * @throws Exception
     */
    public void start() throws Exception
    {

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

        /* по идее это listener, и с помощью него можно получить кол-во ошибок в тесте,
        * но я пока не нашел, как его добавить, чтобы тесты запускались*/
        SampleResult result = new SampleResult();
        result.setURL(new URL("https://www.youtube.com/"));
        result.sampleStart();

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
        //threadGroupHashTree.add(result); // тут ошибка

        // Run Test Plan
        jmeter.configure(testPlanTree);

        int threadCount = 20; // число потоков
        do {
            threadGroup.setNumThreads(threadCount);
            jmeter.run();
            threadCount += 10;
        } while ( threadCount < 160); // временная заглушка, т.к. не нашел как отслеживать плохие запросы

        // снижение числа потоков и запуск Jmeter навсегда (работает)
        threadCount -= 20;
        System.out.println("All Test!");
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
