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
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dell-PC
 */
public class StockGUI extends javax.swing.JFrame {

    /**
     * Creates new form StockGUI
     */
    public StockGUI() {
        initComponents();
        setExtendedState(StockGUI.MAXIMIZED_BOTH);
        loadStockData();
        populateItemStock();
    }
    
    
    //Adding stock
    private void addStock() {
    String barcodeStr = barcodeInput.getText().trim();  // Get barcode input and trim whitespace
    long barcode;
    
    try {
        if (barcodeStr.isEmpty()) {
            // Prompt for manual barcode input if scanner input is empty
            String manualBarcode = JOptionPane.showInputDialog(null, "Enter Barcode:");
            if (manualBarcode == null || manualBarcode.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Input cancelled.");
                return;
            }
            barcodeStr = manualBarcode.trim();
        }
        
        barcode = Long.parseLong(barcodeStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid barcode format. Please enter a valid integer.");
        return;
    }
    
    // Prompt for product name and price
    String productName = JOptionPane.showInputDialog(null, "Enter Product Name:");
    if (productName == null || productName.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Input cancelled.");
        return;
    }
    
    String productPrice = JOptionPane.showInputDialog(null, "Enter Price:");
    if (productPrice == null || productPrice.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Input cancelled.");
        return;
    }
    
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) {
        String sql = "INSERT INTO products (barcode, product_name, product_price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, barcode);
            pstmt.setString(2, productName.trim());
            pstmt.setDouble(3, Double.parseDouble(productPrice));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Stock added successfully!");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid input: please enter numeric values for price.");
    }

    barcodeInput.setText("");  // Clear barcode input after use
}
    //load to table
    private void loadStockData() {
        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) {
            String sql = "SELECT product_name, barcode, product_price FROM products";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    String barcode = rs.getString("barcode");
                    String productPrice = rs.getString("product_price");
                    model.addRow(new Object[]{productName, barcode, productPrice});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }
    
    //remove from table
    private void removeStock() {
    String barcode = barcodeInput.getText();  // Check if barcode is entered via scanner
    if (barcode.isEmpty()) {
        barcode = JOptionPane.showInputDialog(null, "Enter the Barcode of the product to remove:");  // Prompt for manual input if no barcode
    }

    if (barcode != null) {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the product with barcode: " + barcode + "?", "Remove Stock", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db")) {
                String sql = "DELETE FROM products WHERE barcode = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, barcode);
                    int rowsDeleted = pstmt.executeUpdate();
                    
                    if (rowsDeleted > 0) {
                        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                        
                        for (int i = 0; i < model.getRowCount(); i++) {
                            if (model.getValueAt(i, 1).toString().equals(barcode)) {  // Match barcode in table
                                model.removeRow(i);
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Stock removed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "No product found with the given barcode.");
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Stock removal cancelled.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Input cancelled.");
    }
    
    barcodeInput.setText("");  // Clear barcode input after use
}

    private void populateItemStock() {
        Set<String> uniqueProducts = new HashSet<>();
        String sql = "SELECT DISTINCT product_name FROM products ORDER BY product_name";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                uniqueProducts.add(rs.getString("product_name"));
            }
            
            itemStock.removeAllItems();
            for (String product : uniqueProducts) {
                itemStock.addItem(product);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }

    private void updateStockLevels() {
        String selectedProduct = (String) itemStock.getSelectedItem();
        if (selectedProduct == null || selectedProduct.isEmpty()) {
            StockLevels.setText("");
            return;
        }

        String sql = "SELECT COUNT(*) as count FROM products WHERE product_name = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dataBasePos.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, selectedProduct);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    StockLevels.setText(String.valueOf(count));
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Exit_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        addStockBtn = new javax.swing.JButton();
        removeStockBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        barcodeInput = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        itemStock = new javax.swing.JComboBox<>();
        StockLevels = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1046, 668));

        jPanel1.setBackground(new java.awt.Color(0, 102, 225));
        jPanel1.setToolTipText("");

        Exit_button.setText("Exit");
        Exit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Exit_buttonActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Bahnschrift", 1, 24)); // NOI18N
        jLabel1.setText("Stock");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Exit_button)
                .addGap(378, 378, 378)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Exit_button)
                    .addComponent(jLabel1))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        addStockBtn.setText("Add Stock");
        addStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStockBtnActionPerformed(evt);
            }
        });

        removeStockBtn.setText("Remove Stock");
        removeStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStockBtnActionPerformed(evt);
            }
        });

        stockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Barcode", "Product Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(stockTable);

        barcodeInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcodeInputActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Barcode:");

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Stock Levels: ");

        itemStock.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        itemStock.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        itemStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemStockActionPerformed(evt);
            }
        });

        StockLevels.setEditable(false);
        StockLevels.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        StockLevels.setForeground(new java.awt.Color(0, 0, 0));
        StockLevels.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        StockLevels.setCaretColor(new java.awt.Color(0, 0, 0));
        StockLevels.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        StockLevels.setEnabled(false);

        jLabel4.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Items");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(addStockBtn)
                        .addComponent(removeStockBtn)
                        .addComponent(barcodeInput)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(itemStock, 0, 171, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(StockLevels, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(addStockBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removeStockBtn)
                .addGap(52, 52, 52)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barcodeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StockLevels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Exit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Exit_buttonActionPerformed
        // TODO add your handling code here:
        SalesGuii sales = new SalesGuii();
        sales.setVisible(true);
        dispose();
    }//GEN-LAST:event_Exit_buttonActionPerformed

    private void addStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStockBtnActionPerformed
        // TODO add your handling code here:
        addStock();
        loadStockData();
        populateItemStock();
    }//GEN-LAST:event_addStockBtnActionPerformed

    private void removeStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStockBtnActionPerformed
        // TODO add your handling code here:
        removeStock();
        loadStockData();
        populateItemStock();
    }//GEN-LAST:event_removeStockBtnActionPerformed

    private void barcodeInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barcodeInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barcodeInputActionPerformed

    private void itemStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemStockActionPerformed
        // TODO add your handling code here:
        updateStockLevels();
    }//GEN-LAST:event_itemStockActionPerformed

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
            java.util.logging.Logger.getLogger(StockGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StockGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StockGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StockGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StockGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Exit_button;
    private javax.swing.JTextField StockLevels;
    private javax.swing.JButton addStockBtn;
    private javax.swing.JTextField barcodeInput;
    private javax.swing.JComboBox<String> itemStock;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeStockBtn;
    private javax.swing.JTable stockTable;
    // End of variables declaration//GEN-END:variables
}
