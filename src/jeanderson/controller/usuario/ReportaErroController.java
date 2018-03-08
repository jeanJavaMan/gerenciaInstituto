/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import jeanderson.enums.SessaoLogin;
import jeanderson.util.Email;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/ReportaErro.fxml", title = "Reporta Erros")
public class ReportaErroController implements Initializable{

    @FXML
    private JFXTextArea txtMensagem;
    private Email email;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    private JFXSpinner spEnviando;
    @FXML
    private Text txtEnviando;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.email = new Email();
        txtEnviando.visibleProperty().bindBidirectional(spEnviando.visibleProperty());
        spEnviando.setVisible(false);
    }

    @FXML
    private void actionReportar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            spEnviando.setVisible(true);
            Thread t = new Thread(() -> {
                ResultadoBD result = email.reportarErros(txtMensagem.getText(), txtNome.getText());
                Platform.runLater(() -> {
                    if (result.resultado()) {
                        DialogFX.showMessage("Mensagem enviada com sucesso!", "Enviado!", DialogType.SUCESS);
                        FunctionAnnotations.clearFieldsWithAnnotations(this);                        
                    } else {
                        DialogFX.showMessage("Houve um erro tentar enviar mensagem!Motivo: " + result.mensagem(), "Erro ao enviar", DialogType.ERRO);
                    }
                    spEnviando.setVisible(false);
                });
            });
            t.setDaemon(true);
            t.start();
        }
    }

    public void carregaNome() {
        this.txtNome.setText(SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario().getNome());
    }
}
