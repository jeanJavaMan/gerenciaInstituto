/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jeanderson.model.Curso;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class CursoDAO {
    
    public ObservableList<Curso> queryAll(){
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Curso C ORDER BY C.nome";
        Query query = sessao.createQuery(hql);
        ObservableList<Curso> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
}
