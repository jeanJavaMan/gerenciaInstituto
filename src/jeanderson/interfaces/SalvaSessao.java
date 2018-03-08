/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.interfaces;

import jeanderson.model.Usuario;

/**
 *
 * @author jeanderson
 */
public interface SalvaSessao {
    public Usuario getCurrentUser();
    public void addCurrentUser(Usuario usuario);
}
