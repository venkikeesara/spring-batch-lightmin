/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tuxdevelop.spring.batch.lightmin.client.registration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Marcel Becker
 * @since 0.3
 */
@Slf4j
public class RegistrationLightminClientApplicationListener {

    private final LightminClientRegistrator lightminClientRegistrator;
    private final TaskScheduler taskScheduler;
    private boolean autoDeregister = false;
    private boolean autoRegister = true;
    private long registerPeriod = 10_000L;
    private volatile ScheduledFuture<?> scheduledTask;


    public RegistrationLightminClientApplicationListener(final LightminClientRegistrator lightminClientRegistrator,
                                                         final TaskScheduler taskScheduler) {
        this.lightminClientRegistrator = lightminClientRegistrator;
        this.taskScheduler = taskScheduler;
    }

    public RegistrationLightminClientApplicationListener(final LightminClientRegistrator lightminClientRegistrator,
                                                         final ScheduledExecutorService scheduler) {
        this(lightminClientRegistrator, new ConcurrentTaskScheduler(scheduler));
    }

    public RegistrationLightminClientApplicationListener(final LightminClientRegistrator lightminClientRegistrator) {
        this(lightminClientRegistrator, Executors.newSingleThreadScheduledExecutor());
    }

    @EventListener
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void onApplicationReady(final ApplicationReadyEvent event) {
        if (autoRegister) {
            startRegisterTask();
        }
    }

    @EventListener
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void onClosedContext(final ContextClosedEvent event) {
        stopRegisterTask();

        if (autoDeregister) {
            lightminClientRegistrator.deregister();
        }
    }

    public void startRegisterTask() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            return;
        }

        scheduledTask = taskScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                lightminClientRegistrator.register();
            }
        }, registerPeriod);
        log.debug("Scheduled registration task for every {}ms", registerPeriod);
    }

    public void stopRegisterTask() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(true);
            log.debug("Canceled registration task");
        }
    }

    public void setAutoDeregister(final boolean autoDeregister) {
        this.autoDeregister = autoDeregister;
    }

    public void setAutoRegister(final boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    public void setRegisterPeriod(final long registerPeriod) {
        this.registerPeriod = registerPeriod;
    }
}