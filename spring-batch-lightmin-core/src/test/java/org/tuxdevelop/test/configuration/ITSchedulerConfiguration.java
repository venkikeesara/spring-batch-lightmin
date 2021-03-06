package org.tuxdevelop.test.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuxdevelop.spring.batch.lightmin.TestHelper;
import org.tuxdevelop.spring.batch.lightmin.admin.domain.*;
import org.tuxdevelop.spring.batch.lightmin.admin.scheduler.CronScheduler;
import org.tuxdevelop.spring.batch.lightmin.admin.scheduler.PeriodScheduler;

@Configuration
public class ITSchedulerConfiguration {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job simpleJob;

    @Bean
    public PeriodScheduler periodScheduler() {
        final JobSchedulerConfiguration jobSchedulerConfiguration = TestHelper.createJobSchedulerConfiguration(null,
                10L, 10L, JobSchedulerType.PERIOD);
        final JobConfiguration jobConfiguration = TestHelper.createJobConfiguration(jobSchedulerConfiguration);
        final SchedulerConstructorWrapper schedulerConstructorWrapper = new SchedulerConstructorWrapper();
        schedulerConstructorWrapper.setJob(simpleJob);
        schedulerConstructorWrapper.setJobConfiguration(jobConfiguration);
        schedulerConstructorWrapper.setJobIncrementer(JobIncrementer.DATE);
        schedulerConstructorWrapper.setJobLauncher(jobLauncher);
        schedulerConstructorWrapper.setJobParameters(new JobParametersBuilder().toJobParameters());
        final PeriodScheduler periodScheduler = new PeriodScheduler(schedulerConstructorWrapper);
        return periodScheduler;
    }

    @Bean
    public CronScheduler cronScheduler() {
        final JobSchedulerConfiguration jobSchedulerConfiguration = TestHelper.createJobSchedulerConfiguration(
                "0 0/5 * * * ?", null, null, JobSchedulerType.CRON);
        final JobConfiguration jobConfiguration = TestHelper.createJobConfiguration(jobSchedulerConfiguration);
        final SchedulerConstructorWrapper schedulerConstructorWrapper = new SchedulerConstructorWrapper();
        schedulerConstructorWrapper.setJob(simpleJob);
        schedulerConstructorWrapper.setJobConfiguration(jobConfiguration);
        schedulerConstructorWrapper.setJobIncrementer(JobIncrementer.DATE);
        schedulerConstructorWrapper.setJobLauncher(jobLauncher);
        schedulerConstructorWrapper.setJobParameters(new JobParametersBuilder().toJobParameters());
        final CronScheduler cronScheduler = new CronScheduler(schedulerConstructorWrapper);
        return cronScheduler;
    }
}
