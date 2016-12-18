package com.uysalk.hystrix;

import com.meistermeier.hystrix.ticker.persistence.FailingTickerReader;
import com.meistermeier.hystrix.ticker.service.TickerReaderCommandWithCustomizedTimeout;
import com.meistermeier.hystrix.ticker.service.TickerReaderCommand;
import com.meistermeier.hystrix.ticker.service.TickerServiceWithFallbackCommand;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by uysal.kara on 18.12.2016.
 */
public class Demo {


    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 5, TimeUnit.DAYS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String args[]) {
        new Demo().startDemo();
    }


    public void startDemo() {
        int i = 0;
        long start = System.currentTimeMillis();
        while (i<1000) {
            runSimulation();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            i++;
        }

        System.out.println (System.currentTimeMillis() - start);

    }

    public void runSimulation() {
        pool.execute(new Runnable() {

            @Override
            public void run() {
                try {

                   System.out.println ( new FailingTickerReader().getTicker());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
