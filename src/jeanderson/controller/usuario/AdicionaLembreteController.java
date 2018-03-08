/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Lembrete;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/AdicionaLembrete.fxml", title = "Adicionar Lembrete Pessoal")
public class AdicionaLembreteController extends EasyFXFunctions implements Initializable {
    
    @FXML
    @ValidateField(fieldName = "Descrição")
    private JFXTextArea txtDescricao;
    @FXML
    @ValidateField(fieldName = "Lembrar Dia")
    private JFXDatePicker dpLembrar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML
    private void actionSalvar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Lembrete lembrete = new Lembrete();
            lembrete.setObservacoes(txtDescricao.getText());
            lembrete.setDataLembrar(dpLembrar.getValue());
            lembrete.setFuncionario(SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario());
            ResultadoBD result = BancoDeDados.save(lembrete);
            if (result.resultado()) {
                DialogFX.showMessage("Lembrete salvo com sucesso!", "Lembrete salvo!", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar salvar! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
            }
        }
    }
}
