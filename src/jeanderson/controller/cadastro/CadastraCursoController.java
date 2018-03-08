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
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/cadastro/CadastraCurso.fxml", title = "Cadastro de Cursos")
public class CadastraCursoController implements Initializable {

    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Carga Horária")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCargaHoraria;
    @FXML
    @ValidateField(fieldName = "Valor")
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValor;
    @FXML
    private JFXTextArea txtDescricao;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void actionSalvar(ActionEvent event) {
        if(FunctionAnnotations.validateFields(this)){
            Curso curso = new Curso();
            curso.setNome(txtNome.getText().trim());
            curso.setCargaHoraria(Duration.ofHours(Long.parseLong(txtCargaHoraria.getText())));
            curso.setValor(FuncoesUtil.validaValor(txtValor.getText()));
            curso.setConteudo(txtDescricao.getText());
            ResultadoBD result = BancoDeDados.save(curso);
            if(result.resultado()){
                DialogFX.showMessage("Curso salvo com sucesso!", "Salvo com Sucesso!", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            }else{
                DialogFX.showMessage("Houve um erro ao salvar curso! Motivo: " + result.mensagem(), "Erro ao Salvar", DialogType.ERRO);
            }
        }
    }
}
