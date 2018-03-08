/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Mensalidade;
import jeanderson.util.HibernateUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author jeand
 */
public class TesteBean {

    public static void main(String[] args) throws JRException {
        ObservableList<Mensalidade> mensalidades = BancoDeDados.mensalidade().getMensalidades(0, 80);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("Funcionario", "Jeanderson Silva Lopes");
        JRDataSource jrd = new JRBeanCollectionDataSource(mensalidades);
        File arquivo = new File("src/jeanderson/jasper/Mensalidade_relatorio.jrxml");
        System.out.println("arquivo existe? " + arquivo.exists());
        if (arquivo.exists()) {
            JasperDesign jd = JRXmlLoader.load(arquivo);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, jrd);
            JasperViewer.viewReport(jp, false);
        }
        HibernateUtil.shutdown();
    }
}
