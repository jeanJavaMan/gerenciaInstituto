/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jeanderson.model.Funcionario;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class FuncionarioDAO {

    public ObservableList<Funcionario> getFuncionarios() {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Funcionario F ORDER BY F.nome";
        Query<Funcionario> query = sessao.createQuery(hql);
        ObservableList<Funcionario> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
    
    public ObservableList<Funcionario> getFuncionarios(int start, int limit) {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Funcionario F ORDER BY F.nome";
        Query<Funcionario> query = sessao.createQuery(hql);
        query.setFirstResult(start).setMaxResults(limit);
        ObservableList<Funcionario> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
}
