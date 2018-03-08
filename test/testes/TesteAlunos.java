/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import jeanderson.factory.BancoDeDados;
import jeanderson.util.HibernateUtil;

/**
 *
 * @author jeand
 */
public class TesteAlunos {
    public static void main(String[] args) {
        System.out.println(BancoDeDados.aluno().verificaRG("2.959.000"));
        HibernateUtil.shutdown();
    }
}
