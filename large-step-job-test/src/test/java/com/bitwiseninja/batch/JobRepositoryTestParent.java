package com.bitwiseninja.batch;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.batch.core.ExitStatus;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class JobRepositoryTestParent {

    // create x outer partitions, and run them 1 at time (pool size locked at one)
    // so we can measure how much it slows down between each step execution split
    static final long GRID_SIZE_OUTER_PARTITIONER = 5L;
    private static final long POOL_SIZE_OUTER = 1;

    // create 500 sub partitions and run them as fast as possible for a quick test
    // (because we're only profiling the splitting)
    private static final long GRID_SIZE_INNER_PARTITIONER = 500L;
    private static final long POOL_SIZE_INNER = 4;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private StepExecutionSplitterProfiler profiler;

    @Test
    public void runJob() throws Exception {

        long start = System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("VERSION", 1L)
                .addLong("grid.size.outerPartitioner", GRID_SIZE_OUTER_PARTITIONER)
                .addLong("grid.size.innerPartitioner", GRID_SIZE_INNER_PARTITIONER)
                .addLong("pool.size.outer", POOL_SIZE_OUTER)
                .addLong("pool.size.inner", POOL_SIZE_INNER)
                .toJobParameters();
        JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);
        Assert.assertEquals(ExitStatus.COMPLETED, result.getExitStatus());


        long end = System.currentTimeMillis();
        System.out.println("Test completed in " + (end - start) + " millis");

        VerifyBenchmarkIT.saveResults(this.getClass().getSimpleName(), profiler.getProfileTimes());

    }
    
}
