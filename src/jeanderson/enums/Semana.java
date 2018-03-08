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
public enum Semana {
    SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO;

    @Override
    public String toString() {
        switch (this) {
            case SEGUNDA:
                return "Segunda-Feira";
            case TERCA:
                return "Terça-Feira";
            case QUARTA:
                return "Quarta-Feira";
            case QUINTA:
                return "Quinta-Feira";
            case SEXTA:
                return "Sexta-Feira";
            case SABADO:
                return "Sábado";
            case DOMINGO:
                return "Domingo";
            default:
                return "";
        }
    }

}
