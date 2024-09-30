/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.poslogic;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
//slips 
   // public String ReceiptFormat(int salesId, String salesDate,double totalAmount, double product_price){
   // return  "ELSBURG SUPERMARKET"+"\n" +
           // "--------------------------"+ "\n" +
           // "----------------------"+ "\n" +
           // "Sales ID:" + salesId + "" + "" + "" + "R" + product_price + "\n" +
            
           // }
   
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
private void retrieveDataFromDatabase() {
    // Set the column names
    String[] columnNames = {"Sales ID", "Date", "Payment Method", "Amount"};
    
    // Get the table model
    DefaultTableModel tblModel = new DefaultTableModel(columnNames, 0);
    jTable1.setModel(tblModel);

    // SQLite database connection details
    String url = "jdbc:sqlite:C:/Users/Leandro/Desktop/POS-system/dataBasePos.db"; // Your actual SQLite database path
    
    String query = "SELECT s.sales_id, s.sales_date, p.amount, p.payment_method "
                 + "FROM sales s JOIN payment p ON s.sales_id = p.sales_id";
    
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
            String amount = String.valueOf(rs.getDouble("amount"));
            

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
    jTable2.setModel(tblModel);
    
    // SQL query for cash and credit totals
    String query = "SELECT "
                 + "(SELECT SUM(amount) FROM payment WHERE payment_method = 'Cash') AS cashTotal, "
                 + "(SELECT SUM(amount) FROM payment WHERE payment_method = 'Card') AS creditTotal,"
                 + "(SELECT SUM(amount) FROM payment) AS totalAmount";
    
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/Leandro/Desktop/POS-system/dataBasePos.db");
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
    String url = "jdbc:sqlite:C:/Users/Leandro/Desktop/POS-system/dataBasePos.db";
    String query = "SELECT "
                 + "(SELECT SUM(amount) FROM payment WHERE payment_method = 'Cash') AS cashTotal, "
                 + "(SELECT SUM(amount) FROM payment WHERE payment_method = 'Card') AS creditTotal, "
                 + "(SELECT SUM(amount) FROM payment) AS totalAmount";

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
    String url = "jdbc:sqlite:C:/Users/Leandro/Desktop/POS-system/dataBasePos.db";
    String query = "SELECT * FROM payment";

    try (Connection conn = DriverManager.getConnection(url);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, "PaymentData_" + date + ".txt")))) {

        writer.write(String.format("%-12s | %-10s | %-10s | %-10s | %-15s%n", "Payment ID", "Sales ID", "Amount", "Date", "Payment Method"));
        writer.write("-----------------------------------------------------------------\n");
        
        // Write data
        while (rs.next()) {
            writer.write(String.format("%-12d | %-10d | %-10.2f | %-10s | %-15s%n", 
                rs.getInt("payment_id"), 
                rs.getInt("sales_id"), 
                rs.getDouble("amount"), 
                rs.getString("date"), 
                rs.getString("payment_method")));
        }
        
        writer.write("-----------------------------------------------------------------\n");
        System.out.println("Payment data exported to " + directory.getName() + "/PaymentData_" + date + ".txt");
         JOptionPane.showMessageDialog(null, "Payment data exported successfully to " + directory.getName() +  date + ".txt", 
                                      "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException | IOException e) {
        e.printStackTrace();
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
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        exportFiles = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("EXIT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1028, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 720, 310));

        jTable2.setBackground(new java.awt.Color(255, 255, 255));
        jTable2.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 390, 350, 70));

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Export daily totals and daily sales as file");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 100, -1, -1));

        exportFiles.setText("Export sales and totals");
        exportFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportFilesActionPerformed(evt);
            }
        });
        jPanel2.add(exportFiles, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 140, -1, -1));

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        userInterface UserInterface = new userInterface();
        UserInterface.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void exportFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportFilesActionPerformed
        // TODO add your handling code here:
        exportDataToFiles();
    }//GEN-LAST:event_exportFilesActionPerformed

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
    private javax.swing.JButton exportFiles;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
