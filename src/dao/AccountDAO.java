// src/dao/AccountDAO.java
package dao;

import model.Account;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    
    public boolean createAccount(Account account) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, balance) VALUES (?, ?, ?, ?)";
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated account ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        account.setAccountId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Account created successfully: " + account.getAccountNumber());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Account getAccountByNumber(String accountNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Account account = null;
        
        String sql = "SELECT a.*, c.first_name, c.last_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id = c.customer_id " +
                    "WHERE a.account_number = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setCustomerId(rs.getInt("customer_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setDateCreated(rs.getTimestamp("date_created"));
                account.setStatus(rs.getString("status"));
                
                // Debug output
                System.out.println("Found account: " + account.getAccountNumber() + 
                                 " Balance: " + account.getBalance() + 
                                 " Customer: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            } else {
                System.out.println("No account found with number: " + accountNumber);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting account by number: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return account;
    }
    
    public boolean updateBalance(String accountNumber, double newBalance) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Balance updated for account " + accountNumber + 
                             " to " + newBalance + ". Rows affected: " + affectedRows);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setCustomerId(rs.getInt("customer_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setDateCreated(rs.getTimestamp("date_created"));
                account.setStatus(rs.getString("status"));
                
                accounts.add(account);
            }
            
            System.out.println("Found " + accounts.size() + " accounts for customer ID: " + customerId);
            
        } catch (SQLException e) {
            System.err.println("Error getting accounts by customer ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return accounts;
    }
    
    // New method: Verify account exists immediately after creation
    public boolean verifyAccountExists(String accountNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();
            
            boolean exists = rs.next();
            System.out.println("Account verification for " + accountNumber + ": " + (exists ? "EXISTS" : "NOT FOUND"));
            return exists;
            
        } catch (SQLException e) {
            System.err.println("Error verifying account: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}