package com.upshft.upshiftapp.modal;

import java.io.Serializable;

/**
 * Created by new on 3/24/2017.
 */
public class PaymentDetails implements Serializable
{
    public String userId;
    public String paymentStatus;
    public String paymentStartDate;
    public String paymentEndDate;

    public PaymentDetails() {
    }

    public PaymentDetails(String userId, String paymentStatus, String paymentStartDate, String paymentEndDate) {
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.paymentStartDate = paymentStartDate;
        this.paymentEndDate = paymentEndDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStartDate() {
        return paymentStartDate;
    }

    public void setPaymentStartDate(String paymentStartDate) {
        this.paymentStartDate = paymentStartDate;
    }

    public String getPaymentEndDate() {
        return paymentEndDate;
    }

    public void setPaymentEndDate(String paymentEndDate) {
        this.paymentEndDate = paymentEndDate;
    }
}
