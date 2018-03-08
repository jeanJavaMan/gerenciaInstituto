/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import jeanderson.util.FuncoesUtil;

/**
 *
 * @author jeanderson
 */
public class TesteJuros {
    public static void main(String[] args) {
        Double valor = 100.00;
        Double juros = 0.0;
        System.out.println(FuncoesUtil.calculaJuros(valor, juros));
    }
}
