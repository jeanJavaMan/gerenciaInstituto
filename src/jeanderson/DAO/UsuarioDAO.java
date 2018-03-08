/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.model.Funcionario;
import jeanderson.model.Funcionario_;
import jeanderson.model.Usuario;
import jeanderson.model.Usuario_;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class UsuarioDAO {

    /**
     *
     * @param usuario
     * @return
     */
    public Usuario whereUsuarioAndSenha(Usuario usuario) {
        try {
            Session sessao = HibernateUtil.getSession();
            String hql = "FROM Usuario U WHERE U.usuario = :usuario_nome AND U.senha = :usuario_senha";
            Query<Usuario> query = sessao.createQuery(hql);
            query.setParameter("usuario_nome", usuario.getUsuario()).setParameter("usuario_senha", usuario.getSenha());
            Usuario usuarioRetorno = query.uniqueResult();
            sessao.close();
            return usuarioRetorno;
        } catch (Exception ex) {
            Log.salvaLogger(ex);
            return null;
        }

    }

    public ObservableList<Usuario> getAll() {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Usuario";
        Query<Usuario> query = sessao.createQuery(hql);
        ObservableList<Usuario> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
    
    public Usuario whereFuncionarioEmail(String email){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> from = cq.from(Usuario.class);
        Root<Funcionario> fromFuncionario = cq.from(Funcionario.class);
        Predicate condicao = cb.and(cb.equal(fromFuncionario.get(Funcionario_.email), email),cb.equal(fromFuncionario.get(Funcionario_.id), from.get(Usuario_.funcionario)));
        Query<Usuario> query = sessao.createQuery(cq.select(from).where(condicao));
        Usuario usuario = query.uniqueResult();
        sessao.close();
        return usuario;
    }
}
