/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.poslogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author eugen
 */
public class SpecialsGui1 extends javax.swing.JFrame {

    /**
     * Creates new form SpecialsGui1
     */
    public SpecialsGui1() {
        initComponents();
        retrieveDataFromDatabase();
    }
    
    private void retrieveDataFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) MainProductsTable.getModel();
        model.setRowCount(0);
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) {           
            String sql = "SELECT product_name, barcode, discount, ItemCountDis FROM products";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                
                while (rs.next()) {
                    String productName = rs.getString("product_name");  //Gets the product name from database
                    String barcode = rs.getString("barcode");           //Gets the barcode from database
                    Integer discount = rs.getInt("discount");           //Gets the dicount from the database
                    String ItemDiscount = rs.getString("ItemCountDis");
                    
                    
                    String temp1[] = ItemDiscount.split(",");
                    String TableOutputText = "Discount: " + temp1[0] + "% Per " + temp1[1] + " products";
                    
                    model.addRow(new Object[]{productName, barcode, discount, TableOutputText});//Sets the values of the table  
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            System.out.println("SpecialsGui Error 1");
        }
    }
    
    private void UpdateDatabase(String barcode, Integer discount) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) { 
            String sql = "UPDATE products SET discount = " + discount + " WHERE barcode = " + barcode;                      
            PreparedStatement pstmt = conn.prepareStatement(sql);  
            pstmt.executeUpdate();   
            System.out.println("Database updated");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            System.out.println("SpecialsGui Error 2");
        }
    }
    
    private void UpdateItemSpecial(String barcode, Integer discount, Integer ItemCount) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) { 
            String temp = "'" + ItemCount + "," + discount + "'";
            String sql = "UPDATE products SET ItemCountDis = " + temp + " WHERE barcode = " + barcode;                      
            PreparedStatement pstmt = conn.prepareStatement(sql);  
            pstmt.executeUpdate();   
            System.out.println("Database updated");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            System.out.println("SpecialsGui Error 3");
        }
    }
    
    private String GetBarcode() {        
        String barcode = JOptionPane.showInputDialog("Enter product barcode:");       
        while (barcode.length() == 0) {         //Checks that a value has been entered
            barcode = JOptionPane.showInputDialog("Invalid barcode enter product barcode:");
        }         
        return barcode;
    }
    
    private Integer GetItemCount() {
        Integer ItemCount = 0; 
        Integer Check = 0;
        String ItemCountS = JOptionPane.showInputDialog("Enter Item count to apply special to:");
        
        while (Check == 0) { 
            try {
                ItemCount = Integer.valueOf(ItemCountS);
                Check = 1;
            }
            catch (NumberFormatException e) {
                ItemCountS = JOptionPane.showInputDialog("Invalid number enter discount to apply:"); 
            }    
        } 
        return ItemCount;
    }
    
    private Integer GetDiscount() {
        Integer Discount = 0; 
        Integer Check = 0;
        String DiscountS = JOptionPane.showInputDialog("Enter discount to apply:");
        
        while (Check == 0) { 
            try {
                Discount = Integer.valueOf(DiscountS);
                Check = 1;
            }
            catch (NumberFormatException e) {
                DiscountS = JOptionPane.showInputDialog("Invalid number enter discount to apply:"); 
            }    
        } 
        return Discount;
    }
    
    private void AddItemCount() {
        try {
            String barcode = GetBarcode();
            Integer ItemCount = GetItemCount();
            Integer Discount = GetDiscount();  
            
            UpdateItemSpecial(barcode, Discount, ItemCount);
            UpdateDatabase(barcode, 0);
            retrieveDataFromDatabase();
        } catch (NullPointerException e) {
            System.out.println("Input Cancelled");
        }
    }
    
    private void AddDiscount() {
        try {
            String barcode = GetBarcode();
            Integer Discount = GetDiscount();  
            
            UpdateItemSpecial(barcode, 0, 0);
            UpdateDatabase(barcode, Discount);
            retrieveDataFromDatabase();
        } catch (NullPointerException e) {
            System.out.println("Input Cancelled");
        }
    }
    
    private void RemoveitemCount() {
        try {
            String barcode = GetBarcode();
            
            UpdateItemSpecial(barcode, 0, 0);
            retrieveDataFromDatabase();
        } catch (NullPointerException e) {
            System.out.println("Input Cancelled");
        }
    }
    
    private void RemoveDiscount() {  
        try {
            String barcode = GetBarcode();
            
            UpdateDatabase(barcode, 0);
            retrieveDataFromDatabase();
        } catch (NullPointerException e) {
            System.out.println("Input Cancelled");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        MainProductsTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        ExitButtonActionPerformed = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        AddSpecialButtonActionPerformed = new javax.swing.JButton();
        RemoveSpecialButtonActionPerformed = new javax.swing.JButton();
        AddItemCountButton = new javax.swing.JButton();
        RemoveItemCountButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 102));

        MainProductsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Barcode", "Discount", "Item D"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(MainProductsTable);
        if (MainProductsTable.getColumnModel().getColumnCount() > 0) {
            MainProductsTable.getColumnModel().getColumn(2).setMaxWidth(100);
        }

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ExitButtonActionPerformed.setText("Exit");
        ExitButtonActionPerformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ExitButtonActionPerformed, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(ExitButtonActionPerformed, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(0, 102, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );

        AddSpecialButtonActionPerformed.setText("Add Discount");
        AddSpecialButtonActionPerformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddSpecialButtonActionPerformedActionPerformed(evt);
            }
        });

        RemoveSpecialButtonActionPerformed.setText("Remove Discount");
        RemoveSpecialButtonActionPerformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveSpecialButtonActionPerformedActionPerformed(evt);
            }
        });

        AddItemCountButton.setText("Add Item Count");
        AddItemCountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddItemCountButtonActionPerformed(evt);
            }
        });

        RemoveItemCountButton.setText("Remove Item Count");
        RemoveItemCountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveItemCountButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AddItemCountButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(RemoveItemCountButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(RemoveSpecialButtonActionPerformed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddSpecialButtonActionPerformed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(AddSpecialButtonActionPerformed)
                        .addGap(18, 18, 18)
                        .addComponent(RemoveSpecialButtonActionPerformed)
                        .addGap(18, 18, 18)
                        .addComponent(AddItemCountButton)
                        .addGap(18, 18, 18)
                        .addComponent(RemoveItemCountButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ExitButtonActionPerformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformedActionPerformed
        // TODO add your handling code here:
        StockGUI stock = new StockGUI();
        stock.setVisible(true);
        dispose();
    }//GEN-LAST:event_ExitButtonActionPerformedActionPerformed

    private void AddSpecialButtonActionPerformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSpecialButtonActionPerformedActionPerformed
        // TODO add your handling code here:
        AddDiscount();
    }//GEN-LAST:event_AddSpecialButtonActionPerformedActionPerformed

    private void RemoveSpecialButtonActionPerformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveSpecialButtonActionPerformedActionPerformed
        // TODO add your handling code here:
        RemoveDiscount();
    }//GEN-LAST:event_RemoveSpecialButtonActionPerformedActionPerformed

    private void AddItemCountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddItemCountButtonActionPerformed
        // TODO add your handling code here:
        AddItemCount();
    }//GEN-LAST:event_AddItemCountButtonActionPerformed

    private void RemoveItemCountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveItemCountButtonActionPerformed
        // TODO add your handling code here:
        RemoveitemCount();
    }//GEN-LAST:event_RemoveItemCountButtonActionPerformed

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
            java.util.logging.Logger.getLogger(SpecialsGui1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpecialsGui1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpecialsGui1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpecialsGui1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SpecialsGui1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddItemCountButton;
    private javax.swing.JButton AddSpecialButtonActionPerformed;
    private javax.swing.JButton ExitButtonActionPerformed;
    private javax.swing.JTable MainProductsTable;
    private javax.swing.JButton RemoveItemCountButton;
    private javax.swing.JButton RemoveSpecialButtonActionPerformed;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
