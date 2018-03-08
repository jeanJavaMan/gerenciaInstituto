/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.IOException;
import jeanderson.util.Log;

/**
 *
 * @author jeand
 */
public class TesteLog {

    public static void main(String[] args) throws IOException {
        String number = "10dw";
        try {   
                       
            Integer valor = Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            Log.salvaLogger(ex);
        }
    }
}
