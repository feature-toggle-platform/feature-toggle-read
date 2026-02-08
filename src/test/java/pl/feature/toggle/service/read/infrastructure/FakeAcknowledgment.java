package pl.feature.toggle.service.read.infrastructure;

import org.springframework.kafka.support.Acknowledgment;

public class FakeAcknowledgment implements Acknowledgment {

    private boolean ack;

    @Override
    public void acknowledge() {
        ack = true;
    }
}
