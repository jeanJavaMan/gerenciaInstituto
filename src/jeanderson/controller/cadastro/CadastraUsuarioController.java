/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.cadastro;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Funcionario;
import jeanderson.model.Usuario;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/cadastro/CadastrarUsuario.fxml", title = "Cadastro de Usuário")
public class CadastraUsuarioController extends EasyFXFunctions implements Initializable {

    @FXML
    @ValidateField(fieldName = "Usuario")
    private JFXTextField txtUsuario;
    @FXML
    @ValidateField(fieldName = "Senha")
    private JFXPasswordField txtSenha;
    @FXML
    @ValidateField(fieldName = "Funcionario")
    private JFXComboBox<Funcionario> cbFuncionario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFuncionario.setConverter(Converter.FUNCIONARIO.getStringConverter());

    }

    @FXML
    private void actionCadastrar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Usuario usuario = new Usuario();
            usuario.setFuncionario(cbFuncionario.getValue());
            usuario.setUsuario(txtUsuario.getText().trim());
            usuario.setSenha(txtSenha.getText().trim());
            ResultadoBD result = BancoDeDados.save(usuario);
            if (result.resultado()) {
                DialogFX.showMessage("Usuário cadastrado com sucesso!", "Usuário cadastrado", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar cadastrar o Usuario! Motivo: " + result.mensagem(), "Erro ao cadastrar", DialogType.ERRO);
            }
        }
    }

    public void carregarFuncionarios() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.cbFuncionario.setItems(BancoDeDados.funcionario().getFuncionarios());
            });
        });
        t.setDaemon(true);
        t.start();
    }
}
