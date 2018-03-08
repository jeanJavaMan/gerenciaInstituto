/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.factory;

import javax.persistence.PersistenceException;
import jeanderson.DAO.AlunoDAO;
import jeanderson.DAO.CaixaDAO;
import jeanderson.DAO.ContatoDAO;
import jeanderson.DAO.CursoDAO;
import jeanderson.DAO.FrequenciaDAO;
import jeanderson.DAO.FuncionarioDAO;
import jeanderson.DAO.HistoricoDAO;
import jeanderson.DAO.LembreteDAO;
import jeanderson.DAO.MensalidadeDAO;
import jeanderson.DAO.TurmaDAO;
import jeanderson.DAO.UsuarioDAO;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import jeanderson.util.ResultadoBD;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author Jeanderson
 */
public class BancoDeDados {
    
    public static ResultadoBD save(Object objeto) {
        ResultadoBD resultado = new ResultadoBD();
        try (Session sessao = HibernateUtil.getSession()) {
            Transaction transacao = sessao.beginTransaction();
            sessao.persist(objeto);
            transacao.commit();
            sessao.close();
            resultado.setResultado(true);
            resultado.setMensagem("Executado com sucesso!");
            return resultado;
        } catch (HibernateException | NoClassDefFoundError ex) {
            resultado.setResultado(false);
            resultado.setMensagem(ex.getMessage());
            Log.salvaLogger(ex);
            return resultado;
        } catch (PersistenceException e) {
            resultado.setResultado(false);
            resultado.setMensagem(e.getMessage());
            Log.salvaLogger(e);
            return resultado;
        }
    }
    
    public static ResultadoBD update(Object objeto) {
        ResultadoBD resultado = new ResultadoBD();
        try (Session sessao = HibernateUtil.getSession()) {
            Transaction transacao = sessao.beginTransaction();
            sessao.update(objeto);
            transacao.commit();
            sessao.close();
            resultado.setResultado(true);
            resultado.setMensagem("Executado com sucesso!");
            return resultado;
        } catch (HibernateException | NoClassDefFoundError ex) {
            resultado.setResultado(false);
            resultado.setMensagem(ex.getMessage());
            Log.salvaLogger(ex);
            return resultado;
        } catch (PersistenceException e) {
            resultado.setResultado(false);
            resultado.setMensagem(e.getMessage());
            Log.salvaLogger(e);
            return resultado;
        }
    }
    
    public static ResultadoBD delete(Object objeto) {
        ResultadoBD resultado = new ResultadoBD();
        try (Session sessao = HibernateUtil.getSession()) {
            Transaction transacao = sessao.beginTransaction();
            sessao.delete(objeto);
            transacao.commit();
            sessao.close();
            resultado.setResultado(true);
            resultado.setMensagem("Executado com sucesso!");
            return resultado;
        } catch (HibernateException | NoClassDefFoundError ex) {
            resultado.setResultado(false);
            resultado.setMensagem(ex.getMessage());
            Log.salvaLogger(ex);
            return resultado;
        } catch (PersistenceException e) {
            resultado.setResultado(false);
            resultado.setMensagem(e.getMessage());
            Log.salvaLogger(e);
            return resultado;
        }
    }
    
    public static ResultadoBD executeSQL(String sql) {
        ResultadoBD resultado = new ResultadoBD();
        try (Session sessao = HibernateUtil.getSession()) {
            Transaction transacao = sessao.beginTransaction();
            Query createNativeQuery = sessao.createNativeQuery(sql);            
            int result = createNativeQuery.executeUpdate();
            transacao.commit();
            sessao.close();
            System.out.println(result);
            if (result == -1) {
                resultado.setResultado(false);
                resultado.setMensagem("Houve um erro na query");
            } else {
                resultado.setResultado(true);
                resultado.setMensagem("Executado com sucesso");
            }
            return resultado;
        } catch (HibernateException | NoClassDefFoundError ex) {
            resultado.setResultado(false);
            resultado.setMensagem(ex.getMessage());
            Log.salvaLogger(ex);
            return resultado;
        } catch (PersistenceException e) {
            resultado.setResultado(false);
            resultado.setMensagem(e.getMessage());
            Log.salvaLogger(e);
            return resultado;
        }
    }
    
    public static UsuarioDAO usuario() {
        return new UsuarioDAO();
    }
    
    public static LembreteDAO lembrete() {
        return new LembreteDAO();
    }
    
    public static ContatoDAO contato() {
        return new ContatoDAO();
    }
    
    public static CursoDAO curso() {
        return new CursoDAO();
    }
    
    public static TurmaDAO turma() {
        return new TurmaDAO();
    }
    
    public static MensalidadeDAO mensalidade() {
        return new MensalidadeDAO();
    }
    
    public static FrequenciaDAO frequencia() {
        return new FrequenciaDAO();
    }
    
    public static AlunoDAO aluno() {
        return new AlunoDAO();
    }
    
    public static HistoricoDAO historico(){
        return new HistoricoDAO();
    }
    
    public static FuncionarioDAO funcionario(){
        return new FuncionarioDAO();
    }
    
    public static CaixaDAO caixa(){
        return new CaixaDAO();
    }
        
}
