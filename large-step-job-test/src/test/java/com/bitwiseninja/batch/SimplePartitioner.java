package com.bitwiseninja.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Essentially the same as the default Spring Batch SimplePartitioner
 * but allows for late-binding of grid size and creates universally unique names.
 * Partition size defaults to 1.
 */
public class SimplePartitioner implements Partitioner {

    private int gridSize = 1;

    private static final String PARTITION_KEY = "partition";

    private static final AtomicLong partitionCount = new AtomicLong();

    @Override
    public Map<String, ExecutionContext> partition(int originalGridSizeIgnored) {
        HashMap map = new HashMap(gridSize);

        String partitionPrefix = PARTITION_KEY + partitionCount.incrementAndGet() + "-";

        for(int i = 0; i < gridSize; ++i) {
            map.put(partitionPrefix + i, new ExecutionContext());
        }

        return map;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
