package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.Account;
import dao.AccountDAO;

public class TransactionManagementPanel extends JPanel {
    private JTextField txtAccountNumber, txtAmount, txtDescription;
    private JComboBox<String> cmbTransactionType;
    private JButton btnExecute, btnCheckBalance, btnFindAccount;
    private JTextArea txtBalanceInfo;
    private AccountDAO accountDAO;
    
    public TransactionManagementPanel() {
        accountDAO = new AccountDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with two sections
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Transaction panel
        JPanel transactionPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Execute Transaction"));
        
        // Account number with find button
        transactionPanel.add(new JLabel("Account Number:"));
        JPanel accountPanel = new JPanel(new BorderLayout());
        txtAccountNumber = new JTextField();
        accountPanel.add(txtAccountNumber, BorderLayout.CENTER);
        
        btnFindAccount = new JButton("Find");
        btnFindAccount.setToolTipText("Search for account by number");
        accountPanel.add(btnFindAccount, BorderLayout.EAST);
        transactionPanel.add(accountPanel);
        
        transactionPanel.add(new JLabel("Transaction Type:"));
        cmbTransactionType = new JComboBox<>(new String[]{"DEPOSIT", "WITHDRAWAL"});
        transactionPanel.add(cmbTransactionType);
        
        transactionPanel.add(new JLabel("Amount:"));
        txtAmount = new JTextField();
        transactionPanel.add(txtAmount);
        
        transactionPanel.add(new JLabel("Description:"));
        txtDescription = new JTextField();
        transactionPanel.add(txtDescription);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnExecute = new JButton("Execute Transaction");
        btnCheckBalance = new JButton("Check Balance");
        
        buttonPanel.add(btnExecute);
        buttonPanel.add(btnCheckBalance);
        
        transactionPanel.add(buttonPanel);
        
        // Balance info panel
        JPanel balancePanel = new JPanel(new BorderLayout());
        balancePanel.setBorder(BorderFactory.createTitledBorder("Account Information"));
        
        txtBalanceInfo = new JTextArea(8, 50);
        txtBalanceInfo.setEditable(false);
        txtBalanceInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtBalanceInfo.setText("Enter an account number and click 'Check Balance' to view account details.");
        
        JScrollPane scrollPane = new JScrollPane(txtBalanceInfo);
        balancePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(transactionPanel);
        mainPanel.add(balancePanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers
        setupEventHandlers();
        
        // Add debug panel
        add(createDebugPanel(), BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        btnExecute.addActionListener(e -> executeTransaction());
        btnCheckBalance.addActionListener(e -> checkBalance());
        btnFindAccount.addActionListener(e -> findAccount());
        
        // Add sample data for testing
        JButton btnSampleData = new JButton("Load Sample Data");
        btnSampleData.addActionListener(e -> loadSampleData());
    }
    
    private JPanel createDebugPanel() {
        JPanel debugPanel = new JPanel(new FlowLayout());
        debugPanel.setBorder(BorderFactory.createTitledBorder("Debug Tools"));
        
        JButton btnTestConnection = new JButton("Test Database Connection");
        btnTestConnection.addActionListener(e -> testDatabaseConnection());
        
        JButton btnListAllAccounts = new JButton("List All Accounts");
        btnListAllAccounts.addActionListener(e -> listAllAccounts());
        
        JButton btnVerifyAccount = new JButton("Verify Account");
        btnVerifyAccount.addActionListener(e -> verifyCurrentAccount());
        
        debugPanel.add(btnTestConnection);
        debugPanel.add(btnListAllAccounts);
        debugPanel.add(btnVerifyAccount);
        
        return debugPanel;
    }
    
    private void findAccount() {
        String accountNumber = txtAccountNumber.getText().trim();
        
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number to search!");
            return;
        }
        
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            txtBalanceInfo.setText("❌ Account not found: " + accountNumber + 
                                 "\n\nPlease check:\n" +
                                 "• Account number spelling\n" +
                                 "• Account exists in database\n" +
                                 "• Use 'List All Accounts' to see available accounts");
        } else {
            displayAccountInfo(account);
        }
    }
    
    private void executeTransaction() {
        if (!validateTransactionForm()) {
            return;
        }
        
        try {
            String accountNumber = txtAccountNumber.getText().trim();
            String transactionType = cmbTransactionType.getSelectedItem().toString();
            double amount = Double.parseDouble(txtAmount.getText());
            String description = txtDescription.getText().trim();
            
            System.out.println("Attempting transaction: " + transactionType + 
                             " Amount: " + amount + " for account: " + accountNumber);
            
            // First, verify account exists
            if (!accountDAO.verifyAccountExists(accountNumber)) {
                JOptionPane.showMessageDialog(this, 
                    "Account not found: " + accountNumber + 
                    "\n\nPlease verify the account number and try again.", 
                    "Account Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                JOptionPane.showMessageDialog(this, 
                    "Could not retrieve account details for: " + accountNumber, 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double newBalance = account.getBalance();
            
            if (transactionType.equals("DEPOSIT")) {
                newBalance += amount;
            } else if (transactionType.equals("WITHDRAWAL")) {
                if (amount > account.getBalance()) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Insufficient funds!\n" +
                        "Available balance: $" + account.getBalance() + 
                        "\nAttempted withdrawal: $" + amount,
                        "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                newBalance -= amount;
            }
            
            // Update balance in database
            if (accountDAO.updateBalance(accountNumber, newBalance)) {
                String successMessage = String.format(
                    "✅ Transaction Successful!\n\n" +
                    "Account: %s\n" +
                    "Transaction: %s\n" +
                    "Amount: $%.2f\n" +
                    "Previous Balance: $%.2f\n" +
                    "New Balance: $%.2f\n" +
                    "Description: %s",
                    accountNumber, transactionType, amount, 
                    account.getBalance(), newBalance, description
                );
                
                JOptionPane.showMessageDialog(this, successMessage, 
                    "Transaction Complete", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form and refresh balance
                txtAmount.setText("");
                txtDescription.setText("");
                checkBalance(); // Refresh the display
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Transaction failed! Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid amount!\nExample: 100.50", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error processing transaction: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void checkBalance() {
        String accountNumber = txtAccountNumber.getText().trim();
        
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number!");
            return;
        }
        
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            txtBalanceInfo.setText("❌ Account not found: " + accountNumber + 
                                 "\n\nTroubleshooting:\n" +
                                 "1. Check account number spelling\n" +
                                 "2. Verify account exists in database\n" +
                                 "3. Use 'List All Accounts' button to see all accounts\n" +
                                 "4. Try the 'Find' button to search");
            return;
        }
        
        displayAccountInfo(account);
    }
    
    private void displayAccountInfo(Account account) {
        // Format balance information
        String info = String.format(
            "✅ Account Found!\n\n" +
            "Account Number: %s\n" +
            "Account Type: %s\n" +
            "Current Balance: $%.2f\n" +
            "Status: %s\n" +
            "Account ID: %d\n" +
            "Customer ID: %d\n" +
            "Created: %s\n\n" +
            "💡 Transaction Tips:\n" +
            "• Minimum withdrawal: $1.00\n" +
            "• Minimum deposit: $1.00\n" +
            "• Available for withdrawal: $%.2f",
            account.getAccountNumber(),
            account.getAccountType(),
            account.getBalance(),
            account.getStatus(),
            account.getAccountId(),
            account.getCustomerId(),
            account.getDateCreated() != null ? 
                account.getDateCreated().toString().substring(0, 16) : "N/A",
            account.getBalance()
        );
        
        txtBalanceInfo.setText(info);
    }
    
    private boolean validateTransactionForm() {
        if (txtAccountNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    // Debug methods
    private void testDatabaseConnection() {
        try {
            Account testAccount = accountDAO.getAccountByNumber("TEST");
            JOptionPane.showMessageDialog(this, 
                "✅ Database connection is working!\n" +
                "AccountDAO methods are accessible.",
                "Connection Test", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Database connection failed!\nError: " + e.getMessage(),
                "Connection Test Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listAllAccounts() {
        // This would normally query all accounts from database
        // For now, show a message about how to see accounts
        JOptionPane.showMessageDialog(this,
            "To view all accounts:\n" +
            "1. Go to 'Account Management' tab\n" +
            "2. All created accounts are listed in the table\n" +
            "3. Use 'View Customer Accounts' to see accounts by customer\n\n" +
            "Current account in field: " + txtAccountNumber.getText(),
            "View All Accounts", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void verifyCurrentAccount() {
        String accountNumber = txtAccountNumber.getText().trim();
        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an account number first!");
            return;
        }
        
        boolean exists = accountDAO.verifyAccountExists(accountNumber);
        if (exists) {
            JOptionPane.showMessageDialog(this, 
                "✅ Account VERIFIED in database: " + accountNumber,
                "Account Verification", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Account NOT FOUND in database: " + accountNumber + 
                "\n\nPossible reasons:\n" +
                "• Account number misspelled\n" +
                "• Account not created properly\n" +
                "• Database connection issue",
                "Account Verification Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSampleData() {
        // Pre-fill with sample data for testing
        txtAccountNumber.setText("ACC001");
        txtAmount.setText("500");
        txtDescription.setText("Test transaction");
        checkBalance();
    }
}