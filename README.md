Banking System Application
A comprehensive GUI-based banking system developed in Java with MySQL integration. This application provides a complete banking management solution with customer management, account operations, and transaction processing.

🏦 About the Project
This is a full-featured banking management system that allows bank employees to manage customers, accounts, and transactions through an intuitive graphical user interface. The system demonstrates proper software engineering principles including database integration, and user authentication.



✨ Features

🔐 User Authentication
-Secure login system with role-based access (Admin/Teller)
-Session management
-Different privileges based on user roles

👥 Customer Management
-Add new customers with complete details
-View and update customer information
-Search and manage customer records
-Comprehensive customer profiles

💰 Account Management
-Create different account types (Savings, Current, Fixed Deposit)
-Auto-generated account numbers
-Account status management (Active/Inactive/Suspended)
-View customer account relationships

💸 Transaction Processing
-Deposits: Add funds to accounts
-Withdrawals: Remove funds with balance validation
-Transaction history and receipts
-Real-time balance updates

📊 Reporting & Administration
-System dashboard with statistics
-Transaction history viewing
-User management (Admin only)
-Database maintenance tools

🛠 Technologies Used
-Backend
-Java SE - Core application logic
-MySQL - Database management
-JDBC - Database connectivity

Frontend
-Java Swing - Graphical user interface
-AWT - Window toolkit for UI components
-DAO Pattern - Data Access Object for database operations
-Layered Architecture - Proper separation of concerns

🚀 How to Run
Prerequisites:-
-Java JDK 8 or higher
-MySQL Server 5.7 or higher
-MySQL Connector/J

-> Database Setup
1. Create Database:

CREATE DATABASE banking_system;
USE banking_system;

2. Run SQL Script:
Execute the provided "Database.sql" script
This creates all necessary tables and sample data

3. Configure Database Connection:
  -> Update DatabaseConnection.java with your MySQL credentials:
private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";


-> Compilation and Execution (Using Command Line)
# Compile all Java files
javac -cp "lib/mysql-connector-java-8.0.33.jar" -d bin src/model/*.java src/dao/*.java src/util/*.java src/gui/*.java

# Run the application
java -cp "bin:lib/*" gui.LoginFrame