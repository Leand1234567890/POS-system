/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.poslogic;

import java.io.*;
import java.sql.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Leandro
 */
public class salesGUI extends javax.swing.JFrame {

    /**
     * Creates new form salesGUI
     */
    public salesGUI() {
        initComponents();   
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        retrieveDataFromDatabase();
        retrieveCashCreditTotals();
    }
     
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
private void retrieveDataFromDatabase() {
    // Set the column names
    String[] columnNames = {"Sales ID", "Date", "Payment Method", "Amount"};
    
    // Get the table model
    DefaultTableModel tblModel = new DefaultTableModel(columnNames, 0);
    MainDiplayTable.setModel(tblModel);

    // SQLite database connection details
    String url = "jdbc:sqlite:dataBasePos.db"; // Your actual SQLite database path
    
    String query = "SELECT sales_id, sales_date, total_amount, payment_method FROM sales";
              
    
    // Establish the database connection and retrieve data
    try (Connection conn = DriverManager.getConnection(url);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        // Loop through the result set and add rows to the table
        while (rs.next()) {
            // Extract data from the result set
            String salesId = rs.getString("sales_id");
            String saleDate = rs.getString("sales_date");
            String paymentMethod = rs.getString("payment_method");
            String amount = String.valueOf(rs.getDouble("total_amount"));
            

            // Add the data to the table model
            String[] rowData = {salesId, saleDate, paymentMethod, amount};
            tblModel.addRow(rowData);
        }

    } catch (SQLException e) {
        e.printStackTrace(); // Handle the exception properly (e.g., show an error message)
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//method for second table
private void retrieveCashCreditTotals() {
    // Define column names
    String[] columnNames = {"Date" ,"Cash Total", "Credit Total", "Total amount"};
    DefaultTableModel tblModel = new DefaultTableModel(columnNames, 0);
    TotalsTable.setModel(tblModel);
    
    // SQL query for cash and credit totals
    String query = "SELECT "
                 + "(SELECT SUM(total_amount) FROM sales WHERE payment_method = 'cash') AS cashTotal, "
                 + "(SELECT SUM(total_amount) FROM sales WHERE payment_method = 'card') AS creditTotal,"
                 + "(SELECT SUM(total_amount) FROM sales) AS totalAmount";
    
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Retrieve totals and add to table
        if (rs.next()) {
            String saleDate = currentDate.format(formatter);
            String cashTotal = String.valueOf(rs.getDouble("cashTotal"));
            String creditTotal = String.valueOf(rs.getDouble("creditTotal"));
            String totalAmount = String.valueOf(rs.getDouble("totalAmount"));
            String[] rowData = {saleDate, cashTotal, creditTotal, totalAmount};
            tblModel.addRow(rowData);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//methods to save as pdf
private void exportDataToFiles() {
    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    
    // Create directories
    File salesDir = new File("SalesData");
    File paymentDir = new File("PaymentData");
    
    if (!salesDir.exists()) {
        salesDir.mkdir();
    }
    if (!paymentDir.exists()) {
        paymentDir.mkdir();
    }
    //Export sales data
    exportSalesData(salesDir, currentDate);
    
    // Export payment data
    exportPaymentData(paymentDir, currentDate);
}

//method for sales data to be exported.
private void exportSalesData(File directory, String date) {
    String url = "jdbc:sqlite:dataBasePos.db";
    String query = "SELECT "
                 + "(SELECT SUM(total_amount) FROM sales WHERE payment_method = 'cash') AS cashTotal, "
                 + "(SELECT SUM(total_amount) FROM sales WHERE payment_method = 'card') AS creditTotal, "
                 + "(SELECT SUM(total_amount) FROM sales) AS totalAmount";

    try (Connection conn = DriverManager.getConnection(url);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, "SalesData_" + date + ".txt")))) {

        // Write the headers
        writer.write("ELSBURG SUPERMARKET\n");
        writer.write(" \n");
        writer.write("Total for today\n");
         writer.write(" \n");
        writer.write(String.format("%-10s | %-12s | %-15s |%n", "Total Cash", "Total Card", "Total Amount"));
        writer.write("---------------------------------------------\n");
        
        // Write totals to the file
        if (rs.next()) {
            double cashTotal = rs.getDouble("cashTotal");
            double creditTotal = rs.getDouble("creditTotal");
            double totalAmount = rs.getDouble("totalAmount");
            
            writer.write(String.format("%-10.2f | %-12.2f | %-15.2f |%n", cashTotal, creditTotal, totalAmount));
        }
        writer.write("---------------------------------------------\n");
        System.out.println("Sales data exported to " + directory.getName() + "/SalesData_" + date + ".txt");
        JOptionPane.showMessageDialog(null, "Sales data exported successfully to " + directory.getName() +  date + ".txt", 
                                      "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException | IOException e) {
        e.printStackTrace();
    }
}
private void exportPaymentData(File directory, String date) {
    String url = "jdbc:sqlite:dataBasePos.db";
    String query = "SELECT sales_id, sales_date, total_amount, payment_method FROM sales";

    try (Connection conn = DriverManager.getConnection(url);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, "PaymentData_" + date + ".txt")))) {

        writer.write(String.format("%-12s | %-10s | %-10s | %-15s%n", "Sales ID", "Sales Date", "Amount", "Payment Method"));
        writer.write("-----------------------------------------------------------------\n");
        
        // Write data
        while (rs.next()) {
            writer.write(String.format("%-12d | %-10s | %-10.2f | %-15s%n",  
                rs.getInt("sales_id"), 
                rs.getString("sales_date"), 
                rs.getDouble("total_amount"), 
                rs.getString("payment_method")));
        }
        
        writer.write("-----------------------------------------------------------------\n");
        System.out.println("Payment data exported to " + directory.getName() + "/PaymentData_" + date + ".txt");
        JOptionPane.showMessageDialog(null, "Payment data exported successfully to " + directory.getName() +  date + ".txt", 
                                      "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } 
}


    //Method to delete all the data in sales
    public void deleteSales(){
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")){
            String query = "DELETE FROM sales";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            
            String resetQuery = "DELETE FROM sqlite_sequence WHERE name='sales'";
            PreparedStatement resetStmt = conn.prepareStatement(resetQuery);
            resetStmt.executeUpdate();
            
            stmt.close();
            resetStmt.close();
            
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error clearing the sales table!", "Error", JOptionPane.ERROR_MESSAGE);
        }  
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ExitButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        MainDiplayTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        TotalsTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        Export_And_Sales_button = new javax.swing.JButton();
        clear_Data_Base_Button = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        ExitButton.setBackground(new java.awt.Color(255, 255, 255));
        ExitButton.setForeground(new java.awt.Color(0, 0, 0));
        ExitButton.setText("EXIT");
        ExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1039, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        MainDiplayTable.setBackground(new java.awt.Color(255, 255, 255));
        MainDiplayTable.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        MainDiplayTable.setForeground(new java.awt.Color(0, 0, 0));
        MainDiplayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        MainDiplayTable.setSelectionBackground(new java.awt.Color(0, 102, 255));
        jScrollPane1.setViewportView(MainDiplayTable);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 810, 340));

        TotalsTable.setBackground(new java.awt.Color(255, 255, 255));
        TotalsTable.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(TotalsTable);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, 350, 50));

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Export daily totals and daily sales as file");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 100, -1, -1));

        Export_And_Sales_button.setText("Export sales and totals");
        Export_And_Sales_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Export_And_Sales_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(Export_And_Sales_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 150, -1, -1));

        clear_Data_Base_Button.setBackground(new java.awt.Color(0, 102, 255));
        clear_Data_Base_Button.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        clear_Data_Base_Button.setForeground(new java.awt.Color(0, 0, 0));
        clear_Data_Base_Button.setText("Clear data base");
        clear_Data_Base_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_Data_Base_ButtonActionPerformed(evt);
            }
        });
        jPanel2.add(clear_Data_Base_Button, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 310, -1, -1));

        jLabel2.setBackground(new java.awt.Color(51, 102, 255));
        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Clear data base");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 280, 130, -1));

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        // TODO add your handling code here:
        SalesGuii UserInterface = new SalesGuii();
        UserInterface.setVisible(true);
        dispose();
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void Export_And_Sales_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Export_And_Sales_buttonActionPerformed
        // TODO add your handling code here:
        exportDataToFiles();
    }//GEN-LAST:event_Export_And_Sales_buttonActionPerformed

    private void clear_Data_Base_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_Data_Base_ButtonActionPerformed
        // TODO add your handling code here:
        LocalTime currentTime =LocalTime.now();
        LocalTime earlyTime = LocalTime.of(18,0);
        if(currentTime.isBefore(earlyTime)){
           int confirm = JOptionPane.showConfirmDialog(null,"Are you sure you want to remove all data from the sales table? ",
                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
           if (confirm == JOptionPane.YES_OPTION){
                deleteSales();
                JOptionPane.showMessageDialog(null, "Sales data cleared successfully!");
           }else{
                deleteSales();
                JOptionPane.showMessageDialog(null, "Sales data cleared successfully!");
           }
        }
        retrieveDataFromDatabase();
        retrieveCashCreditTotals();
        
    }//GEN-LAST:event_clear_Data_Base_ButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(salesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(salesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(salesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(salesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new salesGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ExitButton;
    private javax.swing.JButton Export_And_Sales_button;
    private javax.swing.JTable MainDiplayTable;
    private javax.swing.JTable TotalsTable;
    private javax.swing.JButton clear_Data_Base_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
