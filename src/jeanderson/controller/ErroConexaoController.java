/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import admin.Admin;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/ErroConexao.fxml", title = "Erro ao se conectar", resizable = false)
public class ErroConexaoController extends EasyFXFunctions implements Initializable{
    
    private ControlWindow<ErroConexaoController> janelaErro;
    private ControlWindow<Admin> janelaAdmin;
    @FXML
    private StackPane spErro;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.janelaAdmin = new ControlWindow(Admin.class);
    }    
    
    @FXML
    private void actionFechar(ActionEvent event) {
        this.janelaErro.close();
        System.exit(0);
    }
    
    @FXML
    private void actionAdmin(ActionEvent event) {
        String senhaDigitada = DialogFX.showInputText("Senha de autorização", "Informa a senha do administrador", "Senha: ");
        if (!senhaDigitada.isEmpty()) {
            if (senhaDigitada.equals("admin1023x")) {
                janelaAdmin.show();
                janelaErro.close();
            } else {
                DialogFX.showMessage("Senha incorreta!", "Acesso não autorizado", DialogType.ERRO);
            }
        }
    }
    
    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaErro = control;
    }
    
}
