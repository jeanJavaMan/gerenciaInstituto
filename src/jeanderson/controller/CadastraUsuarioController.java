/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.FuncionarioTipo;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/CadastraUsuario.fxml", title = "Cadastro de Usuários", resizable = false)
public class CadastraUsuarioController implements Initializable{

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
    @ValidateField(fieldName = "Senha de Autorização")
    private JFXPasswordField txtSenhaAutorizacao;
    @FXML
    @ValidateField(fieldName = "Usuario")
    private JFXTextField txtUsuario;
    @FXML
    @ValidateField(fieldName = "Senha")
    private JFXPasswordField txtSenha;
    @FXML
    private JFXTextField txtEmail;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFuncao.setItems(FXCollections.observableArrayList(FuncionarioTipo.values()));        
    }

    @FXML
    private void actionCadastrar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            if (this.txtSenhaAutorizacao.getText().equals("marques1023x")) {
                this.salvarDados();
            } else {
                DialogFX.showMessage("Senha de autorização inválida. Informe a senha correta para se cadastrar!", "Senha de Autorização Incorreta", DialogType.WARNING);
            }
        }
    }

    private void salvarDados() {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(txtNome.getText().trim());
        funcionario.setTelefone(txtTelefone.getText().trim());
        funcionario.setFuncao(cbFuncao.getValue());
        funcionario.setEmail(txtEmail.getText());
        funcionario.setAtivo(Boolean.TRUE);
        if(cbFuncao.getValue() == FuncionarioTipo.GERENTE){
            funcionario.setPrivilegioAdmin(true);
        }else{
            funcionario.setPrivilegioAdmin(false);
        }
        ResultadoBD result = BancoDeDados.save(funcionario);
        if (result.resultado()) {
            Usuario usuario = new Usuario();
            usuario.setFuncionario(funcionario);
            usuario.setUsuario(txtUsuario.getText().trim());
            usuario.setSenha(txtSenha.getText().trim());

            result = BancoDeDados.save(usuario);
            if (result.resultado()) {
                DialogFX.showMessage("Funcionario e Usuário cadastrado com sucesso!", "Sucesso", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Não foi possível salvar o usuário. Motivo: " + result.mensagem(), "Usuário não salvo", DialogType.ERRO);
            }
        } else {
            DialogFX.showMessage("Não foi possível salvar o funcionario. Motivo: " + result.mensagem(), "Funcionario não salvo", DialogType.ERRO);
        }
    }
}
