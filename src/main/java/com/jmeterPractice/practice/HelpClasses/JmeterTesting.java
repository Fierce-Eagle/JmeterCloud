package com.jmeterPractice.practice.HelpClasses;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
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
     * @return
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
        jmeter = new StandardJMeterEngine(); // создание движка jmeter (для работы с тестами)

        JMeterUtils.loadJMeterProperties("jmeter.properties"); // местоположение файла (не менять)

        // HTTP Sampler
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProtocol(protocol); // протокол доступа http или https
        httpSampler.setDomain(domain); // домен (название сайта)
        httpSampler.setPath("/"); // путь внутри сайта (на данный момент корневая страница)
        httpSampler.setMethod("GET"); // метод доступа (проще всего GET)
        httpSampler.setAutoRedirects(true); // поступление потоков (можно выбрать follow или auto)
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1); // количество итераций для каждого пользователя
        loopController.initialize(); // инициализация, часть прошлых настроек можно упустить (они там автоматически настраиваются)
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup(); // создание группы потоков для теста
        threadGroup.setNumThreads(10); // кол-во пользователей
        threadGroup.setRampUp(10); // период за который пользователи должны попасть на сайт
        threadGroup.setSamplerController(loopController); // добавление loopController в качестве зависимости к группе потоков
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());

        // Test Plan
        TestPlan testPlan = new TestPlan(); // создание плана тестирования, без него приложение работать не будет
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());

        // JMeter HashTree
        HashTree testPlanTree = new HashTree(); // дерево для объединения элементов плана
        testPlanTree.add(testPlan);

        /* Создание поддерева для конкретной группы потоков
        При попытке убрать unit test будут пройден, но нагрузки на сеть не будет*/
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSampler);

        // Run Test Plan
        jmeter.configure(testPlanTree); // сборка всего проекта
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
