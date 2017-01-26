package com.bitwiseninja.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple tasklet that logs an incrementing counter
 */
public class SimpleTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTasklet.class);

    private static AtomicLong counter = new AtomicLong();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Tasklet call number: {}", counter.incrementAndGet());
        return null;
    }
}
