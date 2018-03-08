/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

/**
 *
 * @author jeanderson
 */
public enum PagamentoTipo {
    A_VISTA, CARTAO_CREDITO, BOLETO, CHEQUE;

    @Override
    public String toString() {
        switch (this) {
            case A_VISTA:
                return "A Vista";
            case CARTAO_CREDITO:
                return "Cartão de Crédito";
            case BOLETO:
                return "Boleto";
            case CHEQUE:
                return "Cheque";
            default:
                return "";
        }
    }

}
