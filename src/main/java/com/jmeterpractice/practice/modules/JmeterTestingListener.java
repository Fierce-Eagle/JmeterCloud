package com.jmeterpractice.practice.modules;

import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;

import java.util.ArrayList;
import java.util.List;

public class JmeterTestingListener
        /* extends AbstractListenerElement // пока неизвестно, зачем он нужен*/
        implements SampleListener {
    public List<SampleResult> results = new ArrayList<>(); // список для хранения результатов (в Map смысла пока не вижу)
    private int responseTime;
    /**
     * Тот метод, который нужно вызывать
     * @param e
     */
    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();
        /* так как наверняка понадобится дополнительно доставать результаты где-то в другой части приложения,
        то список лучше синхронизировать, что-бы избежать ошибок в будущем */
        synchronized (results) {
            if (result != null)
                results.add(result);
        }

    }

    @Override
    public void sampleStarted(SampleEvent e) {
        // не используется
    }

    @Override
    public void sampleStopped(SampleEvent e) {
        // не используется
    }

    /**
     * Возвращает время общее время отклика
     * @return
     */
    public int getResponseTime() {
        responseTime = 0;
        for (int i = 0; i < results.size(); i++) {
            responseTime += results.get(i).getTime(); // получение времени отклика каждого запроса в милисекундах
        }
        return responseTime;
    }

    /**
     * Определяет, успешность проведения нагрузки
     * @return
     *      true - нагрузка критическая и сеть (или сервер) не справляется
     *      false - нагрузка нормальная (можно нагружать дальше)
     */
    public boolean isCriticalTest() {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getErrorCount() > 0)
                return true;
        }
        return false;
    }
}
