package com.mycompany.poslogic;

/**
 *
 * @author Leandro
 */
import java.time.LocalDate;
import javax.swing.text.DateFormatter;

public class timeLogic {
    
    public void dateDisplay (){
    LocalDate myObj = LocalDate.now();
    DateFormatter format = DateFormatter.ofPattern("E, MMM-dd-yyy");
    }
}
