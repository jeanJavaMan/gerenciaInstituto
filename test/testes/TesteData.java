/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author jeand
 */
public class TesteData {
    public static void main(String[] args) {
        LocalDate data = LocalDate.now();
        LocalDate dataAnterior = LocalDate.of(1995, 10, 15);
        System.out.println(data.format(DateTimeFormatter.ofPattern("")));
        
    }
}
