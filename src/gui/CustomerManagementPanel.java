// src/gui/CustomerManagementPanel.java
package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import model.Customer;
import dao.CustomerDAO;

public class CustomerManagementPanel extends JPanel {
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress;
    private JButton btnAdd, btnUpdate, btnClear, btnRefresh;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerDAO customerDAO;
    
    public CustomerManagementPanel() {
        customerDAO = new CustomerDAO();
        initializeUI();
        loadCustomerData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        
        formPanel.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        formPanel.add(txtFirstName);
        
        formPanel.add(new JLabel("Last Name:"));
        txtLastName = new JTextField();
        formPanel.add(txtLastName);
        
        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);
        
        formPanel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);
        
        formPanel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add Customer");
        btnUpdate = new JButton("Update Customer");
        btnClear = new JButton("Clear");
        btnRefresh = new JButton("Refresh");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        
        formPanel.add(buttonPanel);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Customers List"));
        
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone", "Address", "Date Created"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Event handlers
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });
        
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerData();
            }
        });
        
        // Table selection listener
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    displaySelectedCustomer(selectedRow);
                }
            }
        });
    }
    
    private void addCustomer() {
        if (!validateForm()) {
            return;
        }
        
        Customer customer = new Customer(
            txtFirstName.getText().trim(),
            txtLastName.getText().trim(),
            txtEmail.getText().trim(),
            txtPhone.getText().trim(),
            txtAddress.getText().trim()
        );
        
        if (customerDAO.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearForm();
            loadCustomerData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update!");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        Customer customer = new Customer(
            txtFirstName.getText().trim(),
            txtLastName.getText().trim(),
            txtEmail.getText().trim(),
            txtPhone.getText().trim(),
            txtAddress.getText().trim()
        );
        customer.setCustomerId(customerId);
        
        if (customerDAO.updateCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            clearForm();
            loadCustomerData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtFirstName.getText().trim().isEmpty() ||
            txtLastName.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields (First Name, Last Name, Email)!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        customerTable.clearSelection();
    }
    
    private void displaySelectedCustomer(int row) {
        txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
        txtLastName.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3).toString());
        txtPhone.setText(tableModel.getValueAt(row, 4).toString());
        txtAddress.setText(tableModel.getValueAt(row, 5).toString());
    }
    
    private void loadCustomerData() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();
        
        for (Customer customer : customers) {
            Object[] rowData = {
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getDateCreated()
            };
            tableModel.addRow(rowData);
        }
    }
}