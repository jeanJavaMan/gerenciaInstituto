/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Lembrete;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/InfoLembrete.fxml", title = "Informações de Lembrete Pessoal")
public class InfoLembreteController extends EasyFXFunctions implements Initializable {

    @FXML
    @ValidateField(fieldName = "Descrição")
    private JFXTextArea txtDescricao;
    @FXML
    @ValidateField(fieldName = "Lembrar")
    private JFXDatePicker dpLembrar;
    @FXML
    private JFXButton btnEditar;
    private Lembrete lembrete;
    private BooleanProperty editar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.editar = new SimpleBooleanProperty(false);
        txtDescricao.editableProperty().bindBidirectional(editar);
        dpLembrar.editableProperty().bindBidirectional(editar);
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (editar.get()) {
            if (FunctionAnnotations.validateFields(this)) {
                this.lembrete.setObservacoes(txtDescricao.getText());
                lembrete.setDataLembrar(dpLembrar.getValue());
                ResultadoBD result = BancoDeDados.update(lembrete);
                if(result.resultado()){
                    DialogFX.showMessage("Lembrete editado com sucesso!", "Lembrete editado!", DialogType.SUCESS);
                }else{
                    DialogFX.showMessage("Houve um erro ao alterar lembrete! Motivo: " + result.mensagem(), "Erro ao editar", DialogType.ERRO);
                }
            }
        } else {
            this.editar.set(true);
            btnEditar.setText("Salvar");
        }
    }

    public void passarlembrete(Lembrete lembrete) {
        this.lembrete = lembrete;
        txtDescricao.setText(lembrete.getObservacoes());
        dpLembrar.setValue(lembrete.getDataLembrar());
    }

    @Override
    public void afterShow() {
        editar.set(false);
        btnEditar.setText("Editar");
    }
}
