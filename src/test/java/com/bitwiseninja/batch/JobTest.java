package com.bitwiseninja.batch;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:sample-job-context.xml", "classpath:test-context.xml"})
@DirtiesContext(classMode =  AFTER_EACH_TEST_METHOD)
public class JobTest {

    private static final long GRID_SIZE_FIRST_STEP = 500L;
    private static final long GRID_SIZE_OUTER_PARTITIONER = 10L;
    private static final long GRID_SIZE_INNER_PARTITIONER = 500L;
    private static final long POOL_SIZE_OUTER = 1;
    private static final long POOL_SIZE_INNER = 10;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("VERSION", 1L)
                .addLong("grid.size.firstStep", GRID_SIZE_FIRST_STEP)
                .addLong("grid.size.outerPartitioner", GRID_SIZE_OUTER_PARTITIONER)
                .addLong("grid.size.innerPartitioner", GRID_SIZE_INNER_PARTITIONER)
                .addLong("pool.size.outer", POOL_SIZE_OUTER)
                .addLong("pool.size.inner", POOL_SIZE_INNER)
                .toJobParameters();
        JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals(ExitStatus.COMPLETED, result.getExitStatus());
    }
    
    @Test
    public void testJob() throws Exception {
        long start = new Date().getTime();

        // run with compiled src/main/java
        runJob();

        long end = new Date().getTime();
        System.out.println("Test completed in " + (end - start) + " millis");
    }
    
}
