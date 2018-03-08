/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

/**
 *
 * @author jeand
 */
public enum CaixaTipo {
    ENTRADA, SAIDA;

    @Override
    public String toString() {
        switch (this) {
            case ENTRADA:
                return "Entrada";
            case SAIDA:
                return "Saída";
            default:
                return "";
        }
    }

}
