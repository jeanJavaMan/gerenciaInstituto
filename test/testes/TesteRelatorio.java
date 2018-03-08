/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import br.jeanderson.jasper.JasperViewFX;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.stage.Stage;
import jeanderson.util.HibernateUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

/**
 *
 * @author jeand
 */
public class TesteRelatorio extends Application {

    public static void main(String[] args) throws JRException, SQLException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Session sessao = HibernateUtil.getSession();
        Connection c = sessao.getSessionFactory().
                getSessionFactoryOptions().getServiceRegistry().
                getService(ConnectionProvider.class).getConnection();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("Funcionario", "Jeanderson Silva Lopes");
        File arquivo = new File("C:\\Users\\jeand\\Google Drive\\My Projects\\NetBeans Projects\\GerenciaInstituto\\src\\jeanderson\\jasper\\Mensalidade_relatorio.jrxml");
        File arquivoJasper = new File("C:\\Users\\jeand\\Google Drive\\My Projects\\NetBeans Projects\\GerenciaInstituto\\src\\jeanderson\\jasper\\Mensalidade_relatorio.jasper");
        System.out.println("arquivo existe: " + arquivo.exists());
        if (arquivo.exists()) {
            JasperDesign jd = JRXmlLoader.load(arquivo);
            String sql = "SELECT mensalidades.cursos_referentes,mensalidades.data_de_geracao,\n"
                    + "mensalidades.data_de_pagamento,mensalidades.valor_mensalidade,\n"
                    + "mensalidades.id ,mensalidades.data_vencimento ,\n"
                    + "alunos.nome FROM mensalidades \n"
                    + "inner join alunos ON mensalidades.aluno_id = alunos.aluno_id\n"
                    + " WHERE mensalidades.situacao = true";
            JRDesignQuery query = new JRDesignQuery();
            query.setText(sql);
            jd.setQuery(query);
           JasperReport jr = JasperCompileManager.compileReport(jd);
           // JasperReport jr = (JasperReport) JRLoader.loadObject(arquivoJasper);
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, c);
            JasperViewFX view = new JasperViewFX(jp, "Testando");
            view.show();
        }
        sessao.close();
        HibernateUtil.shutdown();
    }
}
