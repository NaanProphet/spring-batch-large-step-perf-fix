package com.bitwiseninja.batch;

import org.apache.commons.collections.list.SynchronizedList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Measures how long it takes to split partitions
 */
@Aspect
public class StepExecutionSplitterProfiler {

    private static final Logger logger = LoggerFactory.getLogger(StepExecutionSplitterProfiler.class);

    private final List<Long> profileTimes = SynchronizedList.decorate(new ArrayList<Long>());

    @Pointcut("execution(* org.springframework.batch.core.partition.StepExecutionSplitter.split(..))")
    public void methodsOfInterest() {    }

    @Around("methodsOfInterest()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        Object output = pjp.proceed();

        long end = System.currentTimeMillis();
        logger.info("Method of interest took " + (end - start) + " ms");
        profileTimes.add(end - start);

        return output;
    }

    public List<Long> getProfileTimes() {
        return profileTimes;
    }


}
