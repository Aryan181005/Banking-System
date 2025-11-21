# Banking System Application
A comprehensive GUI-based banking system developed in Java with MySQL integration. This application provides a complete banking management solution with customer management, account operations, and transaction processing.

# 🏦 About the Project
This is a full-featured banking management system that allows bank employees to manage customers, accounts, and transactions through an intuitive graphical user interface. The system demonstrates proper software engineering principles including database integration, and user authentication.



# ✨ Features

🔐 User Authentication <br>
-Secure login system with role-based access (Admin/Teller) <br>
-Session management <br>
-Different privileges based on user roles <br>

👥 Customer Management <br>
-Add new customers with complete details <br>
-View and update customer information <br>
-Search and manage customer records <br>
-Comprehensive customer profiles <br>

💰 Account Management <br>
-Create different account types (Savings, Current, Fixed Deposit) <br>
-Auto-generated account numbers <br>
-Account status management (Active/Inactive/Suspended) <br>
-View customer account relationships <br>

💸 Transaction Processing <br>
-Deposits: Add funds to accounts <br>
-Withdrawals: Remove funds with balance validation <br>
-Transaction history and receipts <br>
-Real-time balance updates <br>

📊 Reporting & Administration <br>
-System dashboard with statistics <br>
-Transaction history viewing <br>
-User management (Admin only) <br>
-Database maintenance tools <br>

🛠 Technologies Used <br>
-Backend <br>
-Java SE - Core application logic <br>
-MySQL - Database management <br>
-JDBC - Database connectivity <br>

Frontend <br>
-Java Swing - Graphical user interface <br>
-AWT - Window toolkit for UI components <br>
-DAO Pattern - Data Access Object for database operations <br>
-Layered Architecture - Proper separation of concerns <br>

# 🚀 How to Run
Prerequisites:- <br>
-Java JDK 8 or higher <br>
-MySQL Server 5.7 or higher <br>
-MySQL Connector/J <br>

-> Database Setup
1. Create Database: <br>
CREATE DATABASE banking_system;<br>
USE banking_system;

2. Run SQL Script: <br>
Execute the provided "Database.sql" script <br>
This creates all necessary tables and sample data

3. Configure Database Connection: <br>
Update DatabaseConnection.java with your MySQL credentials: <br>
private static final String URL = "jdbc:mysql://localhost:3306/banking_system"; <br>
private static final String USERNAME = "your_username"; <br>
private static final String PASSWORD = "your_password";
<br>

# Compilation and Execution (Using Command Line)
-> Compile all Java files <br>
"javac -cp "lib/mysql-connector-java-8.0.33.jar" -d bin src/model/*.java src/dao/*.java src/util/*.java src/gui/*.java"

-> Run the application <br>
"java -cp "bin:lib/*" gui.LoginFrame"