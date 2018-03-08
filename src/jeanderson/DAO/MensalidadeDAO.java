/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.MensalidadeTipo;
import jeanderson.model.Aluno;
import jeanderson.model.Mensalidade;
import jeanderson.model.Mensalidade_;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class MensalidadeDAO {

    public ObservableList<Mensalidade> getMensalidades(int start, int limit) {
//        Session sessao = HibernateUtil.getSession();
//        String hql = "FROM Mensalidade M ORDER BY M.dataVencimento";
//        Query<Mensalidade> query = sessao.createQuery(hql);
//        query.setFirstResult(start).setMaxResults(limit);
//        ObservableList<Mensalidade> lista = FXCollections.observableArrayList(query.getResultList());
//        sessao.close();
//        return lista;
          Session sessao = HibernateUtil.getSession();
          CriteriaBuilder cb = sessao.getCriteriaBuilder();
          CriteriaQuery<Mensalidade> query = cb.createQuery(Mensalidade.class);
          Root<Mensalidade> from = query.from(Mensalidade.class);
          Predicate condicao = cb.greaterThanOrEqualTo(from.get(Mensalidade_.dataVencimento), LocalDate.now());
          Query<Mensalidade> queryPronta = sessao.createQuery(query.select(from).where(condicao).orderBy(cb.asc(from.get(Mensalidade_.dataVencimento))));
          queryPronta.setFirstResult(start).setMaxResults(limit);
          ObservableList<Mensalidade> lista = FXCollections.observableArrayList(queryPronta.getResultList());
          sessao.close();
          return lista;
    }
    
    public Long countMensalidadesPorDataHoje(){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Mensalidade> from = query.from(Mensalidade.class);
        Predicate condicao = builder.and(builder.equal(from.get(Mensalidade_.dataVencimento), LocalDate.now()));
        condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.situacao), false));
        Query<Long> queryPronta = sessao.createQuery(query.select(builder.count(from)).where(condicao));
        Long resultado = queryPronta.getSingleResult();
        sessao.close();
        return resultado;
    }
    
    public Long countMensalidadesVencidas(){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Mensalidade> from = query.from(Mensalidade.class);
        Predicate condicao = builder.lessThan(from.get(Mensalidade_.dataVencimento), LocalDate.now());
        condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.situacao), false));
        Query<Long> queryPronta = sessao.createQuery(query.select(builder.count(from)).where(condicao));
        Long resultado = queryPronta.getSingleResult();
        sessao.close();
        return resultado;
    }
    
    public List<Mensalidade> getMensalidadePorAluno(Aluno aluno){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Mensalidade> query = builder.createQuery(Mensalidade.class);
        Root<Mensalidade> from = query.from(Mensalidade.class);
        Predicate condicao = builder.equal(from.get(Mensalidade_.aluno), aluno);
        Query<Mensalidade> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        List<Mensalidade> lista = queryPronta.getResultList();
        sessao.close();
        return lista;
    }
    
    public Long countMatriculasNaoPagas(){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Mensalidade> from = cq.from(Mensalidade.class);
        Predicate condicao = cb.lessThan(from.get(Mensalidade_.dataDeGeracao), LocalDate.now().minusDays(7));
        condicao = cb.and(condicao,cb.equal(from.get(Mensalidade_.tipoDaMensalidade), MensalidadeTipo.MATRICULA));
        condicao = cb.and(condicao, cb.equal(from.get(Mensalidade_.situacao), false));
        Query<Long> query = sessao.createQuery(cq.select(cb.count(from)).where(condicao));
        Long resultado = query.getSingleResult();
        sessao.close();
        return resultado;
    }
}
