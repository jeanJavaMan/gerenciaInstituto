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
import jeanderson.model.Aluno;
import jeanderson.model.Curso;
import jeanderson.model.Historico;
import jeanderson.model.Historico_;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class HistoricoDAO {

    public ObservableList<Historico> getHistoricoPorAluno(Aluno aluno) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Historico> query = builder.createQuery(Historico.class);
        Root<Historico> from = query.from(Historico.class);
        Predicate condicao = builder.equal(from.get(Historico_.aluno), aluno);
        Query<Historico> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Historico> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }

    public Historico getHistoricoAlunoECurso(Aluno aluno, Curso curso) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Historico> query = builder.createQuery(Historico.class);
        Root<Historico> from = query.from(Historico.class);
        Predicate condicao = builder.and(builder.equal(from.get(Historico_.aluno), aluno), builder.equal(from.get(Historico_.curso), curso), builder.equal(from.get(Historico_.situacao), false));
        Query<Historico> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        Historico historico = queryPronta.uniqueResult();
        sessao.close();
        return historico;
    }
}
