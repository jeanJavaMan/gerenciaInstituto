/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.DAO;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Curso_;
import jeanderson.model.Turma;
import jeanderson.model.Turma_;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class TurmaDAO {

    public ObservableList<Turma> getTurmaPorCursoOtimizada(Curso curso) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> query = builder.createQuery(Turma.class);
        Root<Turma> from = query.from(Turma.class);
        Predicate condicao = builder.equal(from.get(Turma_.curso), curso);
        Query queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Turma> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }

    public ObservableList<Turma> getTurmaPorCurso(Curso curso) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> query = builder.createQuery(Turma.class);
        Root<Turma> from = query.from(Turma.class);
        Predicate condicao = builder.equal(from.get(Turma_.curso), curso);
        Query queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Turma> lista = FXCollections.observableArrayList(queryPronta.getResultList());        
        for (Turma turma : lista) {
            Hibernate.initialize(turma.getAlunos());
            Hibernate.initialize(turma.getDiasDaSemana());
        }
        sessao.close();
        return lista;
    }

    public List<Turma> getTurmaPorAluno(Aluno aluno) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> query = builder.createQuery(Turma.class);
        Root<Turma> from = query.from(Turma.class);
        Join<Turma, Aluno> join = from.join(Turma_.alunos);
        Predicate condicao = builder.equal(join.get(Aluno_.id), aluno.getId());
        Query<Turma> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        List<Turma> lista = queryPronta.getResultList();
        for (Turma turma : lista) {
            Hibernate.initialize(turma.getAlunos());
        }
        sessao.close();
        return lista;
    }

    public ObservableList<Turma> getTurmas(int start, int limit) {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Turma T ORDER BY T.id";
        Query<Turma> query = sessao.createQuery(hql);
        query.setFirstResult(start).setMaxResults(limit);
        ObservableList<Turma> lista = FXCollections.observableArrayList(query.getResultList());
        for (Turma turma : lista) {
            Hibernate.initialize(turma.getAlunos());
            Hibernate.initialize(turma.getDiasDaSemana());
        }
        sessao.close();
        return lista;
    }

    public ResultadoBD removeAluno(Aluno aluno, Turma turma) {
        String sql = "DELETE FROM alunos_turmas WHERE aluno_id=" + aluno.getId().toString() + " AND turma_id=" + turma.getId().toString();
        return BancoDeDados.executeSQL(sql);
    }
    
    public Turma getTurmaPorAlunoECurso(Aluno aluno, Curso curso){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> query = builder.createQuery(Turma.class);
        Root<Turma> from = query.from(Turma.class);
        Join<Turma, Aluno> join = from.join(Turma_.alunos);
        Join<Turma, Curso> joinCurso = from.join(Turma_.curso);
        Predicate condicao = builder.and(builder.equal(join.get(Aluno_.id), aluno.getId()), builder.equal(joinCurso.get(Curso_.id), curso.getId()));
        Query<Turma> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        Turma turma = queryPronta.uniqueResult();
        sessao.close();
        return turma;
    }
}
