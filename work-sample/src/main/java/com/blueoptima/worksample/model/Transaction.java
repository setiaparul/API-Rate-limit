package com.blueoptima.worksample.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Transaction implements Serializable {

    private long transactionId;
    public Transaction(){

    }
    public Transaction(long transactionId, double timeStamp) {
        this.transactionId = transactionId;
        this.timeStamp = timeStamp;
    }

    private double timeStamp;

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }
}