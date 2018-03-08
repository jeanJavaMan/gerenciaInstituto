/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.FuncionarioTipo;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Funcionario;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoFuncionario.fxml", title = "Informações do Funcionário")
public class InfoFuncionario extends EasyFXFunctions implements Initializable{

    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Telefone")
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    @ValidateField(fieldName = "Função")
    private JFXComboBox<FuncionarioTipo> cbFuncao;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXCheckBox ccPrivilegios;
    @FXML
    private JFXCheckBox ccInativo;
    private Funcionario funcionario;
    private BooleanProperty editar;
    @FXML
    private JFXButton btnEditar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFuncao.setItems(FXCollections.observableArrayList(FuncionarioTipo.values()));
        this.editar = new SimpleBooleanProperty(false);
    }

    public void carregarFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        txtNome.setText(funcionario.getNome());
        txtEmail.setText(funcionario.getEmail());
        txtTelefone.setText(funcionario.getTelefone());
        ccPrivilegios.setSelected(funcionario.getPrivilegioAdmin());
        ccInativo.setSelected(!funcionario.getAtivo());
        cbFuncao.getSelectionModel().select(funcionario.getFuncao());

        txtNome.editableProperty().bindBidirectional(editar);
        txtEmail.editableProperty().bindBidirectional(editar);
        txtTelefone.editableProperty().bindBidirectional(editar);
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (editar.get()) {
            if (FunctionAnnotations.validateFields(this)) {
                this.funcionario.setNome(txtNome.getText());
                funcionario.setEmail(txtEmail.getText());
                funcionario.setFuncao(cbFuncao.getSelectionModel().getSelectedItem());
                funcionario.setTelefone(txtTelefone.getText());
                funcionario.setPrivilegioAdmin(ccPrivilegios.isSelected());
                funcionario.setAtivo(!ccInativo.isSelected());

                ResultadoBD result = BancoDeDados.update(funcionario);
                if (result.resultado()) {
                    DialogFX.showMessage("Funcionário editado com sucesso! Atualize a tabela", "Sucesso!", DialogType.SUCESS);
                    editar.set(false);
                    btnEditar.setText("Editar");
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar alterar dados! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
                }
            }
        } else {
            this.editar.set(true);
            btnEditar.setText("Salvar");
        }
    }

    @Override
    public void afterShow() {
        editar.set(false);
        btnEditar.setText("Editar");
    }
}
