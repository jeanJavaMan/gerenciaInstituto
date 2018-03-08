/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

/**
 *
 * @author Jeanderson
 */
public enum FuncionarioTipo {
    PROFESSOR, ATENDENTE,GERENTE;

    @Override
    public String toString() {
        switch (this) {
            case PROFESSOR:
                return "Professor";
            case ATENDENTE:
                return "Atendente";
            case GERENTE:
                return "Gerente";
            default:
                return "";
        }
    }

}
