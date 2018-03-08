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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Frequencia;
import jeanderson.model.Funcionario;
import jeanderson.model.Historico;
import jeanderson.model.Historico_;
import jeanderson.model.Mensalidade;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author jeanderson
 */
public class AlunoDAO {

    public ObservableList<Aluno> getAlunos(int start, int limit) {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Aluno A ORDER BY A.nome";
        Query<Aluno> query = sessao.createQuery(hql);
        query.setFirstResult(start).setMaxResults(limit);
        ObservableList<Aluno> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }

    public ObservableList<Aluno> getAllAlunos() {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Aluno A ORDER BY A.nome";
        Query<Aluno> query = sessao.createQuery(hql);
        ObservableList<Aluno> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        return lista;
    }

    public List<Aluno> getAllAlunosPorAno(Integer ano) {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Aluno WHERE YEAR(A.dataMatricula) =:ano";
        Query<Aluno> query = sessao.createQuery(hql);
        query.setParameter("ano", ano);
        List<Aluno> alunos = query.getResultList();
        sessao.close();
        return alunos;
    }

    public ResultadoBD excluirAluno(Aluno aluno) {
        removerDependencias(aluno);
        return BancoDeDados.delete(aluno);
    }

    private void removerDependencias(Aluno aluno) {
        String mensagemOperacao = "";
        String sql = "DELETE FROM alunos_turmas WHERE aluno_id=" + aluno.getId().toString() + "";
        ResultadoBD resultTurma = BancoDeDados.executeSQL(sql);
        if (!resultTurma.resultado()) {
            mensagemOperacao += "Houve um erro ao remover aluno da Turma. Motivo: " + resultTurma.mensagem() + "\n";
        }
        String sqlHistorico = "DELETE FROM historicos WHERE aluno_id=" + aluno.getId().toString();
        ResultadoBD resultHistorico = BancoDeDados.executeSQL(sqlHistorico);
        if (!resultHistorico.resultado()) {
            mensagemOperacao += "Houve um erro ao remover aluno da Turma. Motivo: " + resultHistorico.mensagem() + "\n";
        }
        List<Mensalidade> mensalidades = BancoDeDados.mensalidade().getMensalidadePorAluno(aluno);
        for (Mensalidade mensalidade : mensalidades) {
            ResultadoBD resultMensalidade = BancoDeDados.delete(mensalidade);
            if (!resultMensalidade.resultado()) {
                mensagemOperacao += "Houve um erro ao remover a mensalidade do aluno. Motivo: " + resultMensalidade.mensagem() + "\n";
            }
        }
        List<Frequencia> frequencias = BancoDeDados.frequencia().getFrequenciaPorAluno(aluno);
        for (Frequencia frequencia : frequencias) {
            ResultadoBD resultFrequencia = BancoDeDados.delete(frequencia);
            if (!resultFrequencia.resultado()) {
                mensagemOperacao += "Houve um erro ao remover a frequencia do aluno. Motivo: " + resultFrequencia.mensagem() + "\n";
            }
        }
        if (!mensagemOperacao.isEmpty()) {
            Log.salvaLogger(getClass().getName(), "excluirAluno()", mensagemOperacao);
        }
    }

    public List<Aluno> getAlunoPorFuncionario(Funcionario funcionario, Integer ano) {
        Session sessao = HibernateUtil.getSession();
        String hql = "FROM Aluno A WHERE YEAR(A.dataMatricula) =:ano AND A.funcionarioMatricula =:funcionario";
        Query<Aluno> cq = sessao.createQuery(hql);
        cq.setParameter("ano", ano).setParameter("funcionario", funcionario);
        List<Aluno> lista = cq.getResultList();
        sessao.close();
        return lista;
    }

    public List<Aluno> getAlunoPorFuncionarioECurso(Funcionario funcionario, Curso curso) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Aluno> cq = cb.createQuery(Aluno.class);
        Root<Aluno> from = cq.from(Aluno.class);
        Root<Historico> fromHistorico = cq.from(Historico.class);
        Predicate condicao = cb.equal(from.get(Aluno_.funcionarioMatricula), funcionario);
        condicao = cb.and(condicao, cb.equal(fromHistorico.get(Historico_.aluno), from.get(Aluno_.id)), cb.equal(fromHistorico.get(Historico_.curso), curso));
        Query<Aluno> query = sessao.createQuery(cq.select(from).where(condicao));
        List<Aluno> alunos = query.getResultList();
        sessao.close();
        return alunos;
    }

    public boolean verificaRG(String rg) {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Aluno> from = cq.from(Aluno.class);
        Predicate condicao = cb.equal(from.get(Aluno_.RG), rg);
        Query<Long> query = sessao.createQuery(cq.select(cb.count(from)).where(condicao));
        Long result = query.uniqueResult();
        sessao.close();
        System.out.println(result);
        return result > 0;
    }
    
    public boolean verificaCPF(String cpf){
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Aluno> from = cq.from(Aluno.class);
        Predicate condicao = cb.equal(from.get(Aluno_.cpf), cpf);
        Query<Long> query = sessao.createQuery(cq.select(cb.count(from)).where(condicao));
        Long result = query.uniqueResult();
        sessao.close();
        System.out.println(result);
        return result > 0;
    }
}
