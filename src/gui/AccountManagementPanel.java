package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.Account;
import model.Customer;
import dao.AccountDAO;
import dao.CustomerDAO;

public class AccountManagementPanel extends JPanel {
    private JTextField txtAccountNumber, txtInitialDeposit;
    private JComboBox<String> cmbCustomer, cmbAccountType;
    private JButton btnCreateAccount, btnViewAccounts, btnRefresh, btnViewAllCustomers;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    
    public AccountManagementPanel() {
        accountDAO = new AccountDAO();
        customerDAO = new CustomerDAO();
        initializeUI();
        loadCustomers();
        loadAccountsData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Account"));
        
        // Customer selection with ID
        formPanel.add(new JLabel("Customer:"));
        JPanel customerPanel = new JPanel(new BorderLayout());
        cmbCustomer = new JComboBox<>();
        customerPanel.add(cmbCustomer, BorderLayout.CENTER);
        
        btnViewAllCustomers = new JButton("View All");
        btnViewAllCustomers.setToolTipText("View all customers with their IDs");
        customerPanel.add(btnViewAllCustomers, BorderLayout.EAST);
        formPanel.add(customerPanel);
        
        formPanel.add(new JLabel("Account Type:"));
        cmbAccountType = new JComboBox<>(new String[]{"SAVINGS", "CURRENT", "FIXED_DEPOSIT"});
        formPanel.add(cmbAccountType);
        
        formPanel.add(new JLabel("Account Number:"));
        txtAccountNumber = new JTextField();
        txtAccountNumber.setEditable(false); // Auto-generated, not editable
        txtAccountNumber.setBackground(Color.LIGHT_GRAY);
        formPanel.add(txtAccountNumber);
        
        formPanel.add(new JLabel("Initial Deposit:"));
        txtInitialDeposit = new JTextField("0.00");
        formPanel.add(txtInitialDeposit);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCreateAccount = new JButton("Create Account");
        btnViewAccounts = new JButton("View Customer Accounts");
        btnRefresh = new JButton("Refresh");
        
        buttonPanel.add(btnCreateAccount);
        buttonPanel.add(btnViewAccounts);
        buttonPanel.add(btnRefresh);
        
        formPanel.add(buttonPanel);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Accounts List"));
        
        String[] columnNames = {"Account ID", "Account Number", "Customer ID", "Customer Name", "Type", "Balance", "Status", "Created Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        accountsTable = new JTable(tableModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Event handlers
        setupEventHandlers();
        
        // Generate account number automatically
        generateAccountNumber();
    }
    
    private void setupEventHandlers() {
        btnCreateAccount.addActionListener(e -> createAccount());
        btnViewAccounts.addActionListener(e -> viewCustomerAccounts());
        btnRefresh.addActionListener(e -> {
            loadCustomers();
            loadAccountsData();
        });
        btnViewAllCustomers.addActionListener(e -> showAllCustomers());
        
        // Auto-generate account number when account type changes
        cmbAccountType.addActionListener(e -> generateAccountNumber());
        
        // Add tooltips
        cmbCustomer.setToolTipText("Select a customer by ID and name");
        cmbAccountType.setToolTipText("Select account type: SAVINGS, CURRENT, or FIXED_DEPOSIT");
        txtAccountNumber.setToolTipText("Auto-generated account number");
        txtInitialDeposit.setToolTipText("Enter initial deposit amount");
    }
    
    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        cmbCustomer.removeAllItems();
        
        if (customers.isEmpty()) {
            cmbCustomer.addItem("No customers found - Please add customers first");
            cmbCustomer.setEnabled(false);
            btnCreateAccount.setEnabled(false);
        } else {
            cmbCustomer.setEnabled(true);
            btnCreateAccount.setEnabled(true);
            
            // Add each customer with ID and name
            for (Customer customer : customers) {
                String displayText = String.format("ID: %d - %s %s", 
                    customer.getCustomerId(), 
                    customer.getFirstName(), 
                    customer.getLastName());
                cmbCustomer.addItem(displayText);
            }
            
            // Add a count of customers
            cmbCustomer.setToolTipText(customers.size() + " customers available");
        }
    }
    
    private void showAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No customers found in the system.\nPlease add customers first.", 
                "No Customers", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to show all customers with their IDs
        StringBuilder customerList = new StringBuilder();
        customerList.append("ALL CUSTOMERS IN SYSTEM:\n\n");
        
        for (Customer customer : customers) {
            customerList.append(String.format("ID: %-4d | %s %s\n", 
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName()));
            customerList.append(String.format("       Email: %s | Phone: %s\n", 
                customer.getEmail(),
                customer.getPhone() != null ? customer.getPhone() : "N/A"));
            customerList.append("       " + "-".repeat(50) + "\n");
        }
        
        customerList.append(String.format("\nTotal: %d customers", customers.size()));
        
        JTextArea textArea = new JTextArea(customerList.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "All Customers (" + customers.size() + ")", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateAccountNumber() {
        String accountType = cmbAccountType.getSelectedItem().toString();
        String prefix = "";
        
        switch (accountType) {
            case "SAVINGS": prefix = "SAV"; break;
            case "CURRENT": prefix = "CUR"; break;
            case "FIXED_DEPOSIT": prefix = "FD"; break;
        }
        
        // Generate account number with timestamp and random component
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        String accountNumber = prefix + String.format("%03d%03d", timestamp % 1000, random);
        txtAccountNumber.setText(accountNumber);
    }
    
    private void createAccount() {
        if (!validateAccountForm()) {
            return;
        }
        
        try {
            // Extract customer ID from combo box
            String customerSelection = cmbCustomer.getSelectedItem().toString();
            int customerId = extractCustomerId(customerSelection);
            
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid customer selection. Please select a valid customer.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String accountNumber = txtAccountNumber.getText().trim();
            String accountType = cmbAccountType.getSelectedItem().toString();
            double initialDeposit = Double.parseDouble(txtInitialDeposit.getText());


            // Verify customer exists
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                JOptionPane.showMessageDialog(this, 
                    "Selected customer not found in database!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                loadCustomers(); // Refresh customer list
                return;
            }

            
            
            Account account = new Account(customerId, accountNumber, accountType);
            account.setBalance(initialDeposit);
            
            if (accountDAO.createAccount(account)) {
                // Success message with details
                String successMessage = String.format(
                    "✅ Account Created Successfully!\n\n" +
                    "Account Details:\n" +
                    "• Account Number: %s\n" +
                    "• Account Type: %s\n" +
                    "• Customer: %s %s (ID: %d)\n" +
                    "• Initial Balance: $%.2f\n\n" +
                    "The account is now active and ready for transactions.",
                    accountNumber, accountType, 
                    customer.getFirstName(), customer.getLastName(), customerId,
                    initialDeposit
                );
                
                JOptionPane.showMessageDialog(this, successMessage, 
                    "Account Created", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form and reload data
                generateAccountNumber();
                txtInitialDeposit.setText("0.00");
                loadAccountsData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create account! Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid amount for initial deposit!\nExample: 1000.00", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error creating account: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private int extractCustomerId(String customerSelection) {
        try {
            // Format: "ID: 123 - John Doe"
            if (customerSelection.startsWith("ID:")) {
                String idPart = customerSelection.split(" - ")[0]; // "ID: 123"
                String idStr = idPart.replace("ID:", "").trim(); // "123"
                return Integer.parseInt(idStr);
            }
        } catch (Exception e) {
            System.err.println("Error extracting customer ID from: " + customerSelection);
        }
        return -1;
    }
    
    private void viewCustomerAccounts() {
        if (cmbCustomer.getSelectedIndex() == -1 || cmbCustomer.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a customer first!", 
                "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String customerSelection = cmbCustomer.getSelectedItem().toString();
        int customerId = extractCustomerId(customerSelection);
        
        if (customerId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Invalid customer selection.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Account> accounts = accountDAO.getAccountsByCustomerId(customerId);
        Customer customer = customerDAO.getCustomerById(customerId);
        
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                String.format("No accounts found for customer:\n%s %s (ID: %d)", 
                    customer.getFirstName(), customer.getLastName(), customerId),
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create detailed account information
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("📊 Accounts for: %s %s (ID: %d)\n", 
            customer.getFirstName(), customer.getLastName(), customerId));
        sb.append(String.format("📧 Email: %s\n", customer.getEmail()));
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            sb.append(String.format("📞 Phone: %s\n", customer.getPhone()));
        }
        sb.append("\n" + "=".repeat(50) + "\n\n");
        
        double totalBalance = 0;
        for (Account account : accounts) {
            sb.append(String.format("🏦 Account: %s\n", account.getAccountNumber()));
            sb.append(String.format("   Type: %s\n", account.getAccountType()));
            sb.append(String.format("   Balance: $%.2f\n", account.getBalance()));
            sb.append(String.format("   Status: %s\n", account.getStatus()));
            sb.append(String.format("   Created: %s\n", 
                account.getDateCreated().toString().substring(0, 16)));
            sb.append("   " + "-".repeat(30) + "\n");
            totalBalance += account.getBalance();
        }
        
        sb.append(String.format("\n💰 Total Balance Across All Accounts: $%.2f\n", totalBalance));
        sb.append(String.format("📈 Number of Accounts: %d", accounts.size()));
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(245, 245, 245));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Customer Accounts - " + customer.getFirstName() + " " + customer.getLastName(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean validateAccountForm() {
        if (cmbCustomer.getSelectedIndex() == -1 || 
            cmbCustomer.getSelectedItem().toString().contains("No customers")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a customer!\n\nIf no customers are available, " +
                "go to Customer Management tab to add customers first.", 
                "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtAccountNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Account number is required!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double deposit = Double.parseDouble(txtInitialDeposit.getText());
            if (deposit < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Initial deposit cannot be negative!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            // Check minimum deposit for certain account types
            String accountType = cmbAccountType.getSelectedItem().toString();
            if (accountType.equals("FIXED_DEPOSIT") && deposit < 1000) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Fixed Deposit accounts typically require minimum $1000 deposit.\n" +
                    "Do you want to continue with $" + deposit + "?",
                    "Low Initial Deposit", JOptionPane.YES_NO_OPTION);
                return result == JOptionPane.YES_OPTION;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid amount for initial deposit!\nExample: 1000.00", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void loadAccountsData() {
        tableModel.setRowCount(0);
        
        // Get all customers to display customer names
        List<Customer> customers = customerDAO.getAllCustomers();
        
        for (Customer customer : customers) {
            List<Account> accounts = accountDAO.getAccountsByCustomerId(customer.getCustomerId());
            
            for (Account account : accounts) {
                Object[] rowData = {
                    account.getAccountId(),
                    account.getAccountNumber(),
                    customer.getCustomerId(), // Show customer ID in table
                    customer.getFirstName() + " " + customer.getLastName(),
                    account.getAccountType(),
                    String.format("$%.2f", account.getBalance()),
                    account.getStatus(),
                    account.getDateCreated() != null ? 
                        account.getDateCreated().toString().substring(0, 16) : "N/A"
                };
                tableModel.addRow(rowData);
            }
        }
        
        // Update status bar or display count
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No accounts found in the system.\nCreate accounts using the form above.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}