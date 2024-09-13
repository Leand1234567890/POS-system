
package com.mycompany.poslogic;

/**
 *
 * @author Leandro
 */
public class PosLogic {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new titleForm().setVisible(true);
            }
        });
        
    }
}
