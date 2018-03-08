/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import admin.classesxml.ConfigApplication;
import br.jeanderson.control.ControlWindow;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Modality;
import jeanderson.application.InfoApplication;
import jeanderson.controller.ExecutaOperacoesController;

/**
 *
 * @author jeand
 */
public class ExecutaOperacoes {

    private final ConfigApplication configApplication;
    private final XStream stream;

    public ExecutaOperacoes(ConfigApplication configApplication, XStream stream) {
        this.configApplication = configApplication;
        this.stream = stream;
    }

    public boolean verificaSeTemOperacoes() {
        return !configApplication.getCodigoDeOperacao().equals(InfoApplication.CODIGO_EXECUTA_OPERACAO);                
    }

    public void operacaoExecutada() {
        configApplication.setCodigoDeOperacao(InfoApplication.CODIGO_EXECUTA_OPERACAO);
        try {
            stream.toXML(configApplication,new FileOutputStream(new File("Config/ConfigApplication.xml")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExecutaOperacoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ChamarJanelaDeOperacao() {
        ControlWindow<ExecutaOperacoesController> janela = new ControlWindow(ExecutaOperacoesController.class);
        janela.getStage().initModality(Modality.APPLICATION_MODAL);
        janela.getController().receberOperacao(this);
        janela.show();
    }
}
