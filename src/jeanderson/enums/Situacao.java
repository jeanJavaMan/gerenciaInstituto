/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

/**
 *Enum que representa as situações gerais como de contato.
 * @author Jeanderson
 */
public enum Situacao {
    TEM_INTERESSE, SEM_INTERESSE, FAZER_VISITA, ENTRAR_CONTATO, FEZ_MATRICULA;

    @Override
    public String toString() {
        switch (this) {
            case TEM_INTERESSE:
                return "Tem Interesse";
            case SEM_INTERESSE:
                return "Não tem interesse";
            case FAZER_VISITA:
                return "Vai Fazer uma visita";
            case ENTRAR_CONTATO:
                return "Entrar em Contato";
            case FEZ_MATRICULA:
                return "Fez a Matrícula";
            default:
                return "";
        }

    }

}
