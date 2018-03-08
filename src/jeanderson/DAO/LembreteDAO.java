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
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Contato;
import jeanderson.model.Funcionario;
import jeanderson.model.Lembrete;
import jeanderson.model.Lembrete_;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class LembreteDAO {

//    public List<Lembrete> pegarPorData(LocalDate data) {
//        List<Lembrete> lista = new ArrayList<>();
//        try {
//            Session sessao = HibernateUtil.getSession();
//            String hql = "FROM Lembrete L WHERE L.dataLembrar = :data";
//            Query query = sessao.createQuery(hql);
//            query.setParameter("data", data);
//            lista.addAll(query.getResultList());
//            sessao.close();
//        } catch (Exception ex) {
//            System.err.println(ex);
//            Log.salvaLogger(getClass().getName(), "pegarPorData()", ex);
//        }
//        return lista;
//    }
    
    public ObservableList<Lembrete> pegarPorData(LocalDate data, Funcionario funcionarioLogado){        
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Lembrete> query = builder.createQuery(Lembrete.class);
        Root<Lembrete> from = query.from(Lembrete.class);
        Predicate condicao = builder.equal(from.get(Lembrete_.funcionario), funcionarioLogado);
        condicao = builder.and(condicao,builder.equal(from.get(Lembrete_.dataLembrar), data));
        Query<Lembrete> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Lembrete> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }
    
    public Lembrete pegarPorContato(Contato contato){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Lembrete> query = builder.createQuery(Lembrete.class);
        Root<Lembrete> from = query.from(Lembrete.class);
        Predicate condicao = builder.equal(from.get(Lembrete_.contato), contato);       
        Query<Lembrete> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        Lembrete lembrete = queryPronta.uniqueResult();
        sessao.close();
        return lembrete;
    }
    
    public Lembrete pegarPorAluno(Aluno aluno){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Lembrete> query = builder.createQuery(Lembrete.class);
        Root<Lembrete> from = query.from(Lembrete.class);
        Predicate condicao = builder.equal(from.get(Lembrete_.aluno), aluno);       
        Query<Lembrete> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        Lembrete lembrete = queryPronta.uniqueResult();
        sessao.close();
        return lembrete;
    }
    
    public ObservableList<Lembrete> pegarTodos(){
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Lembrete L ORDER BY L.dataLembrar";
        Query<Lembrete> queryPronta = sessao.createQuery(hql);
        ObservableList<Lembrete> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }
    
    public ObservableList<Lembrete> pegarTodos(Funcionario funcionarioLogado){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Lembrete> query = builder.createQuery(Lembrete.class);
        Root<Lembrete> from = query.from(Lembrete.class);
        Predicate condicao = builder.equal(from.get(Lembrete_.funcionario), funcionarioLogado);       
        Query<Lembrete> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Lembrete> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }
    
    public ResultadoBD apagarTodos(){
        String sql = "DELETE FROM lembretes";
        return BancoDeDados.executeSQL(sql);
    }
}
