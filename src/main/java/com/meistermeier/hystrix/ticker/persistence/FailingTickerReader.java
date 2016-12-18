package com.meistermeier.hystrix.ticker.persistence;

import com.meistermeier.hystrix.ticker.model.Ticker;

public class FailingTickerReader implements TickerReader {

    @Override
    public Ticker getTicker() {
        if (Math.random() < 0.2)
            throw new RuntimeException("whole internet is gone. you're doomed.");
        else {
            try {
                if (Math.random() < 0.3)
                    Thread.sleep(10000);
            } catch (InterruptedException e) {

            }

            return new Ticker("Foo News");
        }

    }

}
