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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Leandro
 */
public class SalesGuii extends javax.swing.JFrame {

    
    /**
     * Creates new form SalesGuii
     */
    public SalesGuii() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        barcode_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcodeInputAction(evt);
            }
        });
    }
    
    //Method to handle barcode entry
    private void barcodeInputAction(java.awt.event.ActionEvent evt){
        String barcode =barcode_field.getText();
        if (barcode.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a barcode.");
            return;
        }
        fetchProductFromDatabase(barcode);
        barcode_field.setText("");
    }

    //Method to fetch product from databse
    private void fetchProductFromDatabase(String barcode){
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")){
            String query = "SELECT product_name, product_price FROM products WHERE barcode = ?"; //Set to barcode
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
        
            if (rs.next()){
                String productName =rs.getString("product_name");
                double productPrice =rs.getDouble("product_price");
                DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
                double total = calculateTotal(); 
                model.addRow(new Object[]{productName, productPrice});
                updateTotalTable();

            }else{
                JOptionPane.showMessageDialog(this, "Product not found. Please enter manually.");
                Manual_field.setText("Enter price manually");
            }
        }catch(SQLException ex){
            System.out.println("Error 4 SalesGuii");
            JOptionPane.showMessageDialog(this, "Error accessing database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    //method to add products manually
    private void addProductsManually(){
        String productName = JOptionPane.showInputDialog("Enter product name:");
        String productPrice = Manual_field.getText();

        if (productName != null && !productPrice.isEmpty()) {
            double productPriceDouble = Double.parseDouble(productPrice); 
            DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
            model.addRow(new Object[]{productName, productPriceDouble});
            updateTotalTable();
            barcode_field.setText("");
            Manual_field.setText(""); 
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid product name and price.");
        }
    }
    
    //Method to calculate the total
    private double calculateTotal() {
    double total = 0;
    DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
    
    for (int i = 0; i < model.getRowCount(); i++) {
        Object priceObj = model.getValueAt(i, 1); 
        if (priceObj != null) {
            total += Double.parseDouble(priceObj.toString()); 
        }
    }
    return total; 
}
    
    //Method to update total table
    private void updateTotalTable(){
        double total = calculateTotal();
        DefaultTableModel totalModel = (DefaultTableModel) Total_Table.getModel();
        totalModel.setRowCount(0);
        totalModel.addRow(new Object[]{total}); 
    }
    
    //Method to save the transactions into the database
    private void saveTransactionCash(String paymentType, double totalAmount){
        String salesSql = "INSERT INTO sales(sales_date, total_amount, payment_method) VALUES(?,?,?)";
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")){
            PreparedStatement stmt = conn.prepareStatement(salesSql);
            stmt.setString(1, currentDate); 
            stmt.setDouble(2, totalAmount);
            stmt.setString(3, paymentType);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaction saved successfully.");
        }catch(SQLException ex){
            System.out.println("Error 5 SalesGuii");
            JOptionPane.showMessageDialog(this, "Error saving transactions" + ex.getMessage());
            ex.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        Exit_button = new javax.swing.JButton();
        Sales_Button = new javax.swing.JButton();
        stockBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Main_cart_table = new javax.swing.JTable();
        barcode_field = new javax.swing.JTextField();
        Manual_field = new javax.swing.JTextField();
        Manual_button = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Total_Table = new javax.swing.JTable();
        Clear_Table_button = new javax.swing.JButton();
        Cash_button = new javax.swing.JButton();
        Card_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        Exit_button.setText("EXIT");
        Exit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Exit_buttonActionPerformed(evt);
            }
        });

        Sales_Button.setText("Sales");
        Sales_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Sales_ButtonActionPerformed(evt);
            }
        });

        stockBtn.setText("Stock");
        stockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(Exit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stockBtn)
                .addGap(31, 31, 31)
                .addComponent(Sales_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Exit_button)
                    .addComponent(Sales_Button)
                    .addComponent(stockBtn))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setAutoscrolls(true);

        Main_cart_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Product Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Main_cart_table);

        Manual_button.setText("Manual");
        Manual_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Manual_buttonActionPerformed(evt);
            }
        });

        Total_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(Total_Table);

        Clear_Table_button.setText("Clear all ");
        Clear_Table_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Clear_Table_buttonActionPerformed(evt);
            }
        });

        Cash_button.setText("Cash");
        Cash_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cash_buttonActionPerformed(evt);
            }
        });

        Card_button.setText("Card");
        Card_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Card_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(Cash_button, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(Card_button, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Clear_Table_button, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barcode_field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Manual_field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(Manual_button, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(156, 156, 156))))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(barcode_field, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(Manual_field, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(Manual_button, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Clear_Table_button, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Card_button)
                    .addComponent(Cash_button))
                .addContainerGap(104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Manual_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Manual_buttonActionPerformed
        // TODO add your handling code here:
        addProductsManually();
    }//GEN-LAST:event_Manual_buttonActionPerformed

    private void Clear_Table_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Clear_Table_buttonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
        model.setRowCount(0);
        updateTotalTable();
    }//GEN-LAST:event_Clear_Table_buttonActionPerformed

    private void Cash_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cash_buttonActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog(null, "Enter amount of cash given:");
        if(input != null && !input.isEmpty())
        try{
            double cashGiven = Double.parseDouble(input);
            double totalAmount = calculateTotal();
            double change = cashGiven - totalAmount;
            if (change<0 ){
                JOptionPane.showMessageDialog(null, "Insuffficient cash the customer needs to pay more");
            }else{
                JOptionPane.showMessageDialog(null, "cahnge: " + change);
                saveTransactionCash("cash", totalAmount);
            }
            DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
            DefaultTableModel model2 = (DefaultTableModel) Total_Table.getModel();
            model.setRowCount(0);
            model2.setRowCount(0);
            updateTotalTable();
        }catch(NumberFormatException e){
            System.out.println("Error 6 SalesGuii");
            JOptionPane.showMessageDialog(null,"please enter a valid number");
        }
    }//GEN-LAST:event_Cash_buttonActionPerformed

    private void Card_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Card_buttonActionPerformed
        // TODO add your handling code here:
        double totalAmount = calculateTotal();
        saveTransactionCash("card", totalAmount);
        DefaultTableModel model = (DefaultTableModel) Main_cart_table.getModel();
        DefaultTableModel model2 = (DefaultTableModel) Total_Table.getModel();
        model.setRowCount(0);
        model2.setRowCount(0);
        updateTotalTable();
    }//GEN-LAST:event_Card_buttonActionPerformed

    private void Exit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Exit_buttonActionPerformed
        // TODO add your handling code here:
        titleForm Title = new titleForm();
        Title.setVisible(true);
        dispose();
    }//GEN-LAST:event_Exit_buttonActionPerformed

    private void Sales_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Sales_ButtonActionPerformed
        // TODO add your handling code here:
        salesGUI sales = new salesGUI();
        sales.setVisible(true);
        dispose();
    }//GEN-LAST:event_Sales_ButtonActionPerformed

    private void stockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockBtnActionPerformed
        // TODO add your handling code here:
        StockGUI stock = new StockGUI();
        stock.setVisible(true);
        dispose();
    }//GEN-LAST:event_stockBtnActionPerformed
    private static final double VAT_RATE = 0.15;
    
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
            java.util.logging.Logger.getLogger(SalesGuii.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SalesGuii.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SalesGuii.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SalesGuii.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SalesGuii().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Card_button;
    private javax.swing.JButton Cash_button;
    private javax.swing.JButton Clear_Table_button;
    private javax.swing.JButton Exit_button;
    private javax.swing.JTable Main_cart_table;
    private javax.swing.JButton Manual_button;
    private javax.swing.JTextField Manual_field;
    private javax.swing.JButton Sales_Button;
    private javax.swing.JTable Total_Table;
    private javax.swing.JTextField barcode_field;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton stockBtn;
    // End of variables declaration//GEN-END:variables
}
