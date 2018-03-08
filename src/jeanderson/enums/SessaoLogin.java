/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

import jeanderson.interfaces.SalvaSessao;
import jeanderson.model.Usuario;

/**
 *
 * @author jeanderson
 */
public enum SessaoLogin implements SalvaSessao {
    USUARIO_LOGADO();
    private Usuario usuarioLogado;

    SessaoLogin() {

    }

    @Override
    public Usuario getCurrentUser() {
        return this.usuarioLogado;
    }

    @Override
    public void addCurrentUser(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
}
