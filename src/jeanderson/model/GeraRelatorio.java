/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jeanderson.enums.SessaoLogin;
import jeanderson.util.Log;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author jeand
 */
public class GeraRelatorio {   

    public GeraRelatorio() {
        
    }

    public JasperPrint gerarRelatorioMensalidade(Collection<Mensalidade> listaMensalidades) {
        Map<String, Object> parametro = new HashMap<>();
        parametro.put("Funcionario", SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario().getNome());
        return this.gerarRelatorios("Mensalidade_relatorio.jasper", parametro, listaMensalidades);
    }

    public JasperPrint gerarRelatorioTurma(Turma turma) {
        Map<String, Object> parametro = new HashMap<>();
        parametro.put("Funcionario", SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario().getNome());
        parametro.put("Turma_numero", turma.getId().toString());
        parametro.put("Horario_turma", turma.getHorario());
        parametro.put("Curso_nome", turma.getCurso().getNome());
        return this.gerarRelatorios("turma_relatorio.jasper", parametro, turma.getAlunos());
    }

    public JasperPrint gerarRelatorioCaixa(Collection<Caixa> listaCaixa) {
        Map<String, Object> parametro = new HashMap<>();
        parametro.put("Funcionario", SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario().getNome());
        return this.gerarRelatorios("caixa_relatorio.jasper", parametro, listaCaixa);
    }

    private JasperPrint gerarRelatorios(String arquivoNome, Map parametros, Collection lista) {
        try {
            //JasperDesign jd = JRXmlLoader.load(this.getClass().getResourceAsStream("/jeanderson/jasper/"+arquivoNome));
            JRDataSource js = new JRBeanCollectionDataSource(lista);
            //JasperReport jr = JasperCompileManager.compileReport(this.getClass().getResourceAsStream("/jeanderson/jasper/Mensalidade_relatorio.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(this.getClass().getResourceAsStream("/jeanderson/jasper/"+arquivoNome), parametros, js);
            return jp;
        } catch (JRException ex) {
            Log.salvaLogger(ex);
            return null;
        }

    }

}
