package com.propster.paymentRecords;

public class PaymentRecordsListItem {

    private final int paymentRecordId;
    private final String description;
    private final int senderId;
    private final String senderName;
    private final int recipientId;
    private final String recipientName;
    private final boolean isDirectionIn;
    private final String paymentType;
    private final String paymentDate;
    private final String amount;

    public PaymentRecordsListItem(int paymentRecordId, String description, int senderId, String senderName, int recipientId, String recipientName, boolean isDirectionIn, String paymentType, String paymentDate, String amount) {
        this.paymentRecordId = paymentRecordId;
        this.description = description;
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.isDirectionIn = isDirectionIn;
        this.paymentType = paymentType;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public int getPaymentRecordId() {
        return paymentRecordId;
    }

    public String getDescription() {
        return description;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public boolean getIsDirectionIn() {
        return isDirectionIn;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getAmount() {
        return amount;
    }
}
