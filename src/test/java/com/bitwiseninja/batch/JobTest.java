package com.bitwiseninja.batch;


import com.igormaznitsa.jute.annotations.JUteTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:sample-job-context.xml", "classpath:test-context.xml"})
@DirtiesContext(classMode =  AFTER_EACH_TEST_METHOD)
public class JobTest {

    public static final long GRID_SIZE = 500L;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("VERSION", 1L)
                .addLong("GRID.SIZE", GRID_SIZE, false).toJobParameters();
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
