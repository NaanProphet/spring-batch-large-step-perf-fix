package com.bitwiseninja.batch;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:sample-job-context.xml", "classpath:test-context.xml"})
@DirtiesContext(classMode =  AFTER_EACH_TEST_METHOD)
public class JobRepositoryPerformanceTest {

    public static final long GRID_SIZE = 100_000L;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testStock() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("GRID.SIZE", GRID_SIZE).toJobParameters();
        JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals(ExitStatus.COMPLETED, result.getExitStatus());



    }

}
