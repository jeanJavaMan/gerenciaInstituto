/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Usuario;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoUsuario.fxml", title = "Informações do usuário")
public class InfoUsuarioController extends EasyFXFunctions implements Initializable {

    @FXML
    private JFXTextField txtFuncionario;
    @FXML
    @ValidateField(fieldName = "Usuário")
    private JFXTextField txtUsuario;
    @FXML
    @ValidateField(fieldName = "Senha")
    private JFXTextField txtSenha;
    @FXML
    private JFXButton btnEditar;
    private BooleanProperty editar;
    private Usuario usuario;
    private ControlWindow<InfoUsuarioController> janelaInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.editar = new SimpleBooleanProperty(false);
        txtUsuario.editableProperty().bindBidirectional(editar);
        txtSenha.editableProperty().bindBidirectional(editar);
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (editar.get()) {
            if (FunctionAnnotations.validateFields(this)) {
                usuario.setUsuario(txtUsuario.getText());
                usuario.setSenha(txtSenha.getText());
                ResultadoBD result = BancoDeDados.update(usuario);
                if (result.resultado()) {
                    DialogFX.showMessage("Usuário editado com sucesso! Atualize a tabela", "Sucesso!", DialogType.SUCESS);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar editar usuário! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
                }
            }
        } else {
            btnEditar.setText("Salvar");
            editar.set(true);
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (DialogFX.showConfirmation("Deseja realmente apagar esse usuário?", "Apagar Usuário")) {
            ResultadoBD result = BancoDeDados.delete(usuario);
            if (result.resultado()) {
                DialogFX.showMessageAndWait("Usuário apagado com sucesso! Atualize a tabela", "Sucesso!", DialogType.SUCESS);
                this.janelaInfo.close();
            } else {
                DialogFX.showMessage("Houve um erro ao tentar excluir usuario! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
            }
        }
    }

    public void carregarUsuario(Usuario usuario) {
        this.usuario = usuario;
        txtFuncionario.setText(usuario.getFuncionario().getNome());
        txtUsuario.setText(usuario.getUsuario());
        txtSenha.setText(usuario.getSenha());
    }

    @Override
    public void afterShow() {
        editar.set(false);
        btnEditar.setText("Editar");
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfo = control;
    }

}
