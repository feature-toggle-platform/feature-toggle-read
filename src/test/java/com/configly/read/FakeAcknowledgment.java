package com.configly.read;

import org.springframework.kafka.support.Acknowledgment;

public class FakeAcknowledgment implements Acknowledgment {

    private boolean ack;

    @Override
    public void acknowledge() {
        ack = true;
    }

    public boolean isAcknowledged() {
        return ack;
    }
}
