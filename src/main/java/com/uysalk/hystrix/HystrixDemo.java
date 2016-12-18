package com.uysalk.hystrix;

import com.meistermeier.hystrix.ticker.persistence.FailingTickerReader;
import com.meistermeier.hystrix.ticker.persistence.LongRunningTickerReader;
import com.meistermeier.hystrix.ticker.service.TickerReaderCommandWithCustomizedTimeout;
import com.meistermeier.hystrix.ticker.service.TickerReaderCommand;
import com.meistermeier.hystrix.ticker.service.TickerServiceWithFallbackCommand;
import com.meistermeier.hystrix.ticker.service.TickerServiceWithTimeoutCommand;
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
public class HystrixDemo {


    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 5, TimeUnit.DAYS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String args[]) {
        new HystrixDemo().startDemo();
        System.exit(0);
    }


    public void startDemo() {
        startMetricsMonitor();
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
                HystrixRequestContext context = HystrixRequestContext.initializeContext();
                try {

                    new TickerServiceWithFallbackCommand(new FailingTickerReader()).getTickers();

                    System.out.println("Request => " + HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    context.shutdown();
                }
            }

        });
    }

    public void startMetricsMonitor() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        // ignore
                    }

                    HystrixCommandMetrics tickerReaderCommand= HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(TickerReaderCommand.class.getSimpleName()));

                    // print out metrics
                    StringBuilder out = new StringBuilder();
                    out.append("\n");
                    out.append("#####################################################################################").append("\n");
                    out.append("# TickerReaderCommand: " + getStatsStringFromMetrics(tickerReaderCommand)).append("\n");
                     out.append("#####################################################################################").append("\n");
                    System.out.println(out.toString());
                }
            }

            private String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
                StringBuilder m = new StringBuilder();
                if (metrics != null) {
                    HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
                    m.append("Requests: ").append(health.getTotalRequests()).append(" ");
                    m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
                    m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
                    m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
                    m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
                    m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
                }
                return m.toString();
            }

        });
        t.setDaemon(true);
        t.start();
    }
}
