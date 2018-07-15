package me.phoboslabs.illuminati.esconsumer.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Created by kellin.me on 23/08/2017.
 */
@Component
public class HealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        boolean isOk = check();

        if (!isOk) {
            return Health.down().withDetail("Error Code", 10000).build();
        }

        return Health.up().build();
    }

    private boolean check() {
        return true;
    }

}
