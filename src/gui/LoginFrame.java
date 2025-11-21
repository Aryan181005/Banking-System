package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import util.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    
    public LoginFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Banking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("Banking System Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);
        
        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        
        formPanel.add(new JLabel("")); // Empty cell
        formPanel.add(buttonPanel);
        
        // Add test credentials hint
        JLabel lblHint = new JLabel("Try: admin/admin123 or teller1/teller123", SwingConstants.CENTER);
        lblHint.setForeground(Color.GRAY);
        mainPanel.add(lblHint, BorderLayout.SOUTH);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Event handlers
        btnLogin.addActionListener(e -> authenticateUser());
        btnCancel.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> authenticateUser());
        
        // Auto-fill for testing
        txtUsername.setText("admin");
        txtPassword.setText("admin123");
    }
    
    private void authenticateUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                showMessage("Login successful! Welcome " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                new BankingSystemFrame(role).setVisible(true);
                this.dispose();
            } else {
                showMessage("Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            showMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}