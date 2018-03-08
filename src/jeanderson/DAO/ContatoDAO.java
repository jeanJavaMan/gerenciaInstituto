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
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Contato;
import jeanderson.model.Contato_;
import jeanderson.model.Funcionario;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class ContatoDAO {

    public ObservableList<Contato> queryAll(int start, int limit) {
        try {
            Session sessao = HibernateUtil.getSession();
            String hql = "FROM Contato C ORDER BY C.nome";
            ObservableList<Contato> lista = FXCollections.observableArrayList(sessao.createQuery(hql).setFirstResult(start).setMaxResults(limit).getResultList());
            sessao.close();
            return lista;
        } catch (Exception ex) {
            Log.salvaLogger(ex);
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Contato> queryPorFuncionario(int start, int limit) {
        Funcionario funcionarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario();
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Contato> query = builder.createQuery(Contato.class);
        Root<Contato> from = query.from(Contato.class);
        Predicate condicao = builder.equal(from.get(Contato_.funcionario), funcionarioLogado);
        Query<Contato> queryPronta = sessao.createQuery(query.select(from).where(condicao).orderBy(builder.asc(from.get(Contato_.nome))));
        queryPronta.setFirstResult(start).setMaxResults(limit);
        ObservableList<Contato> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }

    public ResultadoBD removeContato(Contato contato) {
        String sql = "DELETE FROM lembretes WHERE contato_id =" + contato.getId();
        ResultadoBD result = BancoDeDados.executeSQL(sql);
        if (result.resultado()) {
            result = BancoDeDados.delete(contato);
            return result;
        } else {
            return result;
        }
    }
}
