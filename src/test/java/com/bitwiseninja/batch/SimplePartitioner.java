package com.bitwiseninja.batch;

import org.springframework.batch.item.ExecutionContext;

import java.util.Map;

/**
 * Essentially the same as the default Spring Batch SimplePartitioner
 * but allows for late-binding-injectable grid size. Defaults to 1.
 */
public class SimplePartitioner extends org.springframework.batch.core.partition.support.SimplePartitioner {

    private int gridSize = 1;

    @Override
    public Map<String, ExecutionContext> partition(int originalGridSizeIgnored) {
        return super.partition(gridSize);
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
