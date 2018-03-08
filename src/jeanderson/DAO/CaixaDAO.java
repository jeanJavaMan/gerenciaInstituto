/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.model.Caixa;
import jeanderson.model.Caixa_;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeand
 */
public class CaixaDAO {
    public ObservableList<Caixa> getCaixaPorData(LocalDate data){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Caixa> cq = cb.createQuery(Caixa.class);
        Root<Caixa> from = cq.from(Caixa.class);
        Predicate condicao = cb.equal(from.get(Caixa_.dataGeracao), data);
        Query<Caixa> query = sessao.createQuery(cq.select(from).where(condicao));
        ObservableList<Caixa> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
}
