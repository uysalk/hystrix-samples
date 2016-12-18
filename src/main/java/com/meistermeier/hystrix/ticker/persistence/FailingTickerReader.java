package com.meistermeier.hystrix.ticker.persistence;

import com.meistermeier.hystrix.ticker.model.Ticker;

public class FailingTickerReader implements TickerReader {

    @Override
    public Ticker grabNews() {
        if (Math.random() < 0.5)
            throw new RuntimeException("whole internet is gone. you're doomed.");
        else {
            try {
                if (Math.random() < 0.5)
                    Thread.sleep(1000);
                else

                    Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new Ticker("Foo News");
        }

    }

}
