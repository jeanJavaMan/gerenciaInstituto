/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Usuario;
import jeanderson.util.Email;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/RecuperaSenha.fxml", title = "Recuperação de Senha e Usuário")
public class RecuperaSenhaController extends EasyFXFunctions implements Initializable {

    @FXML
    @ValidateField(fieldName = "Email")
    private JFXTextField txtEmail;
    @FXML
    private JFXSpinner spVerificando;
    @FXML
    private Text txtVerifica;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtVerifica.visibleProperty().bindBidirectional(spVerificando.visibleProperty());
        spVerificando.setVisible(false);
    }

    @FXML
    private void actionRecuperar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            spVerificando.setVisible(true);
            txtVerifica.setText("Verificando email... aguarde...");
            Thread t = new Thread(this::executarRecuperacao);
            t.setDaemon(true);
            t.start();
        }
    }

    private void executarRecuperacao() {
        Usuario usuario = BancoDeDados.usuario().whereFuncionarioEmail(txtEmail.getText());
        if (usuario != null) {
            txtVerifica.setText("Enviando para o email de recuperação... aguarde");
            Email email = new Email();
            ResultadoBD result = email.enviarRecuperacaoDeSenha(usuario);
            Platform.runLater(() -> {
                if (result.resultado()) {
                    spVerificando.setVisible(false);
                    DialogFX.showMessage("Dados de recuperação enviado com sucesso para o funcionario: " + usuario.getFuncionario().getNome(), "Sucesso", DialogType.SUCESS);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                } else {
                    spVerificando.setVisible(false);
                    DialogFX.showMessage("Houve um erro ao tentar enviar email! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
                }
            });
        } else {
            Platform.runLater(() -> {
                spVerificando.setVisible(false);
                DialogFX.showMessage("Não foi encontrado nenhum funcionário cadastrado com este email!", "Não encontrado", DialogType.ERRO);
            });
        }
    }

    @Override
    public void afterShow() {
        spVerificando.setVisible(false);
        txtVerifica.setText("Aguarde verificando...");
    }

}
