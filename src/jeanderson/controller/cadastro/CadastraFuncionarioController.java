/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.cadastro;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.enums.ValidateType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.FuncionarioTipo;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Funcionario;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/cadastro/CadastraFuncionario.fxml", title = "Cadastra Funcionário")
public class CadastraFuncionarioController extends EasyFXFunctions implements Initializable{
    
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Telefone", type = ValidateType.TELEFONE)
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    @ValidateField(fieldName = "Função do Funcionário")
    private JFXComboBox<FuncionarioTipo> cbFuncao;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXCheckBox ccPrivilegio;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFuncao.setItems(FXCollections.observableArrayList(FuncionarioTipo.values()));
        
    }    
    
    @FXML
    private void actionCadastrar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Funcionario funcionario = new Funcionario();
            funcionario.setNome(txtNome.getText());
            funcionario.setTelefone(txtTelefone.getText());
            funcionario.setFuncao(cbFuncao.getValue());
            funcionario.setEmail(txtEmail.getText());
            funcionario.setAtivo(true);
            funcionario.setPrivilegioAdmin(ccPrivilegio.isSelected());
            ResultadoBD result = BancoDeDados.save(funcionario);
            if (result.resultado()) {
                DialogFX.showMessage("Funcionário cadastrado com sucesso!", "Funcionário Cadastrado", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Não foi possivel cadastrar o funcionário. Motivo: " + result.mensagem(), "Não cadastrado", DialogType.ERRO);
            }
        }
    }
    @FXML
    private void actionPrivilegioAdmin(ActionEvent event) {
        Funcionario funcionario = SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario();
        if (funcionario.getFuncao() != FuncionarioTipo.GERENTE || funcionario.getPrivilegioAdmin()) {
            DialogFX.showMessage("Somente um Gerente ou um funcionário com privilégios de administrador podem marcar está opção!", "Não autorizado!", DialogType.WARNING);
            ccPrivilegio.setSelected(false);
        }
    }
}
