package org.example.barber_shop.DTO.Payment;

import org.example.barber_shop.Constants.TransactionStatus;
import org.example.barber_shop.DTO.User.UserResponseNoFile;

import java.sql.Timestamp;

public class PaymentResponse {
    public long id;
    public UserResponseNoFile user;
    public long amount;
    public String txnRef;
    public String orderInfo;
    public String bankCode;
    public String transactionNo;
    public TransactionStatus transactionStatus;
    public String cardType;
    public String bankTranNo;
    public Timestamp paid_at;
    public String voucherCode;
    public int voucherDiscount;
    public long discountAmount;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
