/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.time.Duration;
import jeanderson.util.FuncoesUtil;

/**
 *
 * @author jeanderson
 */
public class TesteHoras {
    public static void main(String[] args) {
        Duration duracao = Duration.ofHours(10);
        Duration cumprida = Duration.ofHours(0);
        Duration plus = cumprida.plusHours(2).plusMinutes(30);
        Duration plus2 = Duration.ofHours(5).plusMinutes(0);
        System.out.println(plus2.plus(plus));
        System.out.println("duracao: " + duracao.toHours());
        System.out.println("Falta: " + duracao.minus(plus2));
        System.out.println("Porcentagem: " + FuncoesUtil.calculaPorcentagem((int)duracao.toHours(), plus2.toHours()));
//        System.out.println("cmd /c start /B C:\\outro.jar\\"+System.getProperty("user.name")+"\\teste");
        
    }
    
}
