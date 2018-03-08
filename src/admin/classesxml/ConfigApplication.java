/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author jeand
 */
@XStreamAlias(value = "Config-Application")
public class ConfigApplication {
    @XStreamAlias(value = "Versao-Atual")
    private String versaoAtual;
    @XStreamAlias(value = "Codigo-Operacao")
    private String codigoDeOperacao;

    public String getVersaoAtual() {
        return versaoAtual;
    }

    public void setVersaoAtual(String versaoAtual) {
        this.versaoAtual = versaoAtual;
    }

    public String getCodigoDeOperacao() {
        return codigoDeOperacao;
    }

    public void setCodigoDeOperacao(String codigoDeOperacao) {
        this.codigoDeOperacao = codigoDeOperacao;
    }
    
    
}
