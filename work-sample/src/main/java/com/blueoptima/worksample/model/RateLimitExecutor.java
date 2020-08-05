package com.blueoptima.worksample.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class RateLimitExecutor implements Serializable {
    private AtomicLong transactions;
    private long thresholdHits;
    private ConcurrentLinkedQueue<Transaction> queue;
    public static long thresholdTimeToResetMap;


}
