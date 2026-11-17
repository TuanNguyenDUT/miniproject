package com.sunasterisk.miniproject.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemHeartbeatScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHeartbeatScheduler.class);

    @Scheduled(fixedRate = 30000)
    public void logSystemRunning() {
        LOGGER.info("System running");
    }
}
