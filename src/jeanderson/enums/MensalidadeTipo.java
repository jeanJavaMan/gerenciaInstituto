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
public enum MensalidadeTipo {
    MATRICULA, MENSALIDADE;

    @Override
    public String toString() {
        switch (this) {
            case MATRICULA:
                return "Matrícula";
            case MENSALIDADE:
                return "Mensalidade";
            default:
                return "";
        }
    }

}
