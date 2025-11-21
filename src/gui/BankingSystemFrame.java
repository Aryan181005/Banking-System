package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BankingSystemFrame extends JFrame {
    private String userRole;
    private JTabbedPane tabbedPane;
    
    public BankingSystemFrame(String userRole) {
        this.userRole = userRole;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Banking System - " + userRole);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs based on user role
        if ("ADMIN".equals(userRole)) {
            tabbedPane.addTab("Customer Management", new CustomerManagementPanel());
            tabbedPane.addTab("Account Management", new AccountManagementPanel());
            tabbedPane.addTab("Transaction Management", new TransactionManagementPanel());
            tabbedPane.addTab("Reports", new ReportsPanel());
        } else {
            tabbedPane.addTab("Customer Management", new CustomerManagementPanel());
            tabbedPane.addTab("Account Management", new AccountManagementPanel());
            tabbedPane.addTab("Transaction Management", new TransactionManagementPanel());
        }
        
        add(tabbedPane);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Banking System Application\nVersion 1.0\nDeveloped by 'PseudoCoders'",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
}