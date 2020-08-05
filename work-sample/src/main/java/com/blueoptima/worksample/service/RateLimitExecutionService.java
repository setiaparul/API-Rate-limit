package com.blueoptima.worksample.service;

import com.blueoptima.worksample.model.RateLimitExecutor;
import com.blueoptima.worksample.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Queue;
@Service
public class RateLimitExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitExecutionService.class);
    /*
     * Api to evaluate the hit by a user exceed threshold limit defined in configuration file*/
    public boolean evalute(RateLimitExecutor executor){
        executor.getTransactions().incrementAndGet();
        Queue<Transaction> queue = executor.getQueue();
            while(!queue.isEmpty()){
                Transaction txnObj = queue.peek();
                long currentTimeStamp = System.currentTimeMillis();
                if((currentTimeStamp - txnObj.getTimeStamp()) >= RateLimitExecutor.thresholdTimeToResetMap){
                    queue.poll();
                }
                else{
                    break;
                }
            }
            if(queue.size()< executor.getThresholdHits()){
                queue.add(new Transaction(executor.getTransactions().get(),System.currentTimeMillis()));
                logger.info("Request Accepted at time {}",System.currentTimeMillis());
                return true;
            }else {
                logger.info("Request Rejected at time {}",System.currentTimeMillis());
                return false;
            }
    }

}
