package jeanderson.DAO;


import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.model.Aluno;
import jeanderson.model.Frequencia;
import jeanderson.model.Frequencia_;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeanderson
 */
public class FrequenciaDAO {
    public ObservableList<Frequencia> getFrequencias(int start, int limit){
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Frequencia F ORDER BY F.porcentagemConcluida";
        Query<Frequencia> query = sessao.createQuery(hql);
        query.setFirstResult(start).setMaxResults(limit);
        ObservableList<Frequencia> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }
    
    public List<Frequencia> getFrequenciaPorAluno(Aluno aluno){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Frequencia> query = builder.createQuery(Frequencia.class);
        Root<Frequencia> from = query.from(Frequencia.class);
        Predicate condicao = builder.equal(from.get(Frequencia_.aluno), aluno);
        Query<Frequencia> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        List<Frequencia> lista = queryPronta.getResultList();
        sessao.close();
        return lista;
    }
}
