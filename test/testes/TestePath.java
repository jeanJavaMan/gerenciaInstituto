/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

/**
 *
 * @author jeand
 */
public class TestePath {
    public static void main(String[] args) {
        
        System.out.println(TestePath.class.getClassLoader().getResource("jeanderson/jasper/Mensalidade_relatorio.jrxml").getPath());
    }
}
