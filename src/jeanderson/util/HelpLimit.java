/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.util;

/**
 * Classe que ajuda na operações referente ao limit de elementos que são pesquisados no banco de dados.
 * @author jeanderson
 */
public class HelpLimit {
    private int valorMaximo;
    private int valorInicial;
    private boolean isMaiorQueAnterior;

    /**
     * Necessário informa um valor maximo inicial.
     * @param valorMaximoInicial 
     */
    public HelpLimit(int valorMaximoInicial) {
        this.valorMaximo = valorMaximoInicial;
        this.valorInicial = 0;
    }
    /**
     * Se o novo limite passado for maior q o anterior, então ele irá atualizar os valores inicial e máximo.
     * @param novoLimite 
     */
    public void atualizaLimit(int novoLimite){
        if(novoLimite > valorMaximo){
            this.valorInicial = (this.valorMaximo +1);
            this.valorMaximo = novoLimite;
            this.isMaiorQueAnterior = true;
        }else{
            this.isMaiorQueAnterior = false;
            this.valorInicial = 0;
            this.valorMaximo = novoLimite;
        }
    }
    
    public int getValorInicial(){
        return this.valorInicial;
    }
    
    public int getValorMaximo(){
        return this.valorMaximo;
    }
    public boolean isMaiorQueAnterior(){
        return this.isMaiorQueAnterior;
    }
}
