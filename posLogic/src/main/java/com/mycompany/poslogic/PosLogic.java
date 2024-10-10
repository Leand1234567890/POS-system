package com.mycompany.poslogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 *
 * @author Leandro
 */
public class PosLogic {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        // Run the GUI in a separate thread
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new titleForm().setVisible(true);
            }
        });
        
        // Create the necessary tables in the database
        createTables();
    }

    // Method to establish a database connection
    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:pos.db"; 
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error 3 PosLogic");
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Method to create necessary tables
    private static void createTables() {
        String productsTable = "CREATE TABLE IF NOT EXISTS Products ("
                + "ProductID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ProductName TEXT NOT NULL, "
                + "Category TEXT NOT NULL, "
                + "Price DECIMAL(10, 2) NOT NULL, "
                + "StockQuantity INTEGER NOT NULL"
                + ");";

        String customersTable = "CREATE TABLE IF NOT EXISTS Customers ("
                + "CustomerID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "FirstName TEXT NOT NULL, "
                + "LastName TEXT NOT NULL, "
                + "Email TEXT, "
                + "PhoneNumber TEXT"
                + ");";

        String ordersTable = "CREATE TABLE IF NOT EXISTS Orders ("
                + "OrderID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CustomerID INTEGER NOT NULL, "
                + "OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "TotalAmount DECIMAL(10, 2) NOT NULL, "
                + "FOREIGN KEY (CustomerID) REFERENCES Customers (CustomerID)"
                + ");";

        String orderDetailsTable = "CREATE TABLE IF NOT EXISTS OrderDetails ("
                + "OrderDetailID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "OrderID INTEGER NOT NULL, "
                + "ProductID INTEGER NOT NULL, "
                + "Quantity INTEGER NOT NULL, "
                + "Price DECIMAL(10, 2) NOT NULL, "
                + "Discount DECIMAL(5, 2) DEFAULT 0, "  // Added Discount !!!!
                + "FOREIGN KEY (OrderID) REFERENCES Orders (OrderID), "
                + "FOREIGN KEY (ProductID) REFERENCES Products (ProductID)"
                + ");";

        String paymentsTable = "CREATE TABLE IF NOT EXISTS Payments ("
                + "PaymentID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "OrderID INTEGER NOT NULL, "
                + "PaymentDate DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "AmountPaid DECIMAL(10, 2) NOT NULL, "
                + "PaymentMethod TEXT NOT NULL, "
                + "FOREIGN KEY (OrderID) REFERENCES Orders (OrderID)"
                + ");";

        String inventoryTable = "CREATE TABLE IF NOT EXISTS Inventory ("
                + "InventoryID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ProductID INTEGER NOT NULL, "
                + "Quantity INTEGER NOT NULL, "
                + "LastUpdated DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (ProductID) REFERENCES Products (ProductID)"
                + ");";

        // Execute table creation
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(productsTable);
            stmt.execute(customersTable);
            stmt.execute(ordersTable);
            stmt.execute(orderDetailsTable);
            stmt.execute(paymentsTable);
            stmt.execute(inventoryTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error 2 PosLogic");
            System.out.println(e.getMessage());
        }
    }
}
