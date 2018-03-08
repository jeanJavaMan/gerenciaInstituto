/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.control.ControlWindow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/admin/Admin.fxml", title = "Janela Admin", resizable = false)
public class Admin implements Initializable{

    @FXML
    private AnchorPane apPrincipal;
    private ControlWindow<AdminBancoDeDados> janelaBancoDeDados;
    private ControlWindow<AdminLogs> janelaLog;
    private ControlWindow<AdminClassModel> janelaClassModel;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.janelaBancoDeDados = new ControlWindow(AdminBancoDeDados.class);
        this.janelaLog = new ControlWindow(AdminLogs.class);
        this.janelaClassModel = new ControlWindow(AdminClassModel.class);
    }    

    @FXML
    private void actionBancoDeDados(ActionEvent event) {
        this.apPrincipal.getChildren().setAll(janelaBancoDeDados.getRoot());
        janelaBancoDeDados.getController().carregarConfiguracoes();
        janelaBancoDeDados.getController().afterShow();
    }

    @FXML
    private void actionLogs(ActionEvent event) {
        this.apPrincipal.getChildren().setAll(janelaLog.getRoot());
        this.janelaLog.getController().carregarLog();
    }

    @FXML
    private void actionUsuarios(ActionEvent event) {
    }

    @FXML
    private void actionClassModel(ActionEvent event) {
        this.apPrincipal.getChildren().setAll(janelaClassModel.getRoot());
        janelaClassModel.getController().afterShow();
        janelaClassModel.getController().carregar();        
    }
    
}
