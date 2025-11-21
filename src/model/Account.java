package model;

import java.sql.Timestamp;

public class Account {
    private int accountId;
    private int customerId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private Timestamp dateCreated;
    private String status;
    
    // Constructors
    public Account() {}
    
    public Account(int customerId, String accountNumber, String accountType) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = 0.0;
    }
    
    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public Timestamp getDateCreated() { return dateCreated; }
    public void setDateCreated(Timestamp dateCreated) { this.dateCreated = dateCreated; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}