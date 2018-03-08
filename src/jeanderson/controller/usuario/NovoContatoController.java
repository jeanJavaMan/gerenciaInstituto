/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.enums.ValidateType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.Converter;
import jeanderson.enums.SessaoLogin;
import jeanderson.enums.Situacao;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Contato;
import jeanderson.model.Curso;
import jeanderson.model.Funcionario;
import jeanderson.model.Lembrete;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/NovoContato.fxml", title = "Adicionar Novo Contato")
public class NovoContatoController implements Initializable {

    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Telefone", type = ValidateType.TELEFONE) 
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    @ValidateField(fieldName = "Situacao")
    private JFXComboBox<Situacao> cbSituacao;
    @FXML
    @ValidateField(fieldName = "Curso Referente")
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXCheckBox ccLembrete;
    @FXML
    private JFXTextArea txtObservacao;
    @FXML
    private JFXDatePicker dpDataLembrar;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.dpDataLembrar.visibleProperty().bindBidirectional(ccLembrete.selectedProperty());
        this.cbSituacao.setItems(FXCollections.observableArrayList(Situacao.values()));        
    }    

    @FXML
    private void actionSalvar(ActionEvent event) {
        Thread t = new Thread(()->{
            Platform.runLater(()->{
                this.salvar();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void salvar() {
        if (FunctionAnnotations.validateFields(this)) {
            Funcionario funcionarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario();
            Contato contato = new Contato();
            contato.setFuncionario(funcionarioLogado);
            contato.setNome(txtNome.getText().trim());
            contato.setTelefone(txtTelefone.getText().trim());
            contato.setObservacao(txtObservacao.getText().trim());
            contato.setSituacao(cbSituacao.getValue());
            contato.setCursoInteresse(this.cbCurso.getValue());
            ResultadoBD resultContato = BancoDeDados.save(contato);
            if (resultContato.resultado()) {
                if (this.ccLembrete.isSelected()) {
                    Lembrete lembrete = new Lembrete();
                    lembrete.setContato(contato);
                    lembrete.setFuncionario(funcionarioLogado);
                    lembrete.setDataLembrar(this.dpDataLembrar.getValue());
                    ResultadoBD resultLembrar = BancoDeDados.save(lembrete);
                    if (resultLembrar.resultado()) {
                        DialogFX.showMessage("Contato e Lembrete salvos com sucesso!", "Salvos com Sucesso!", DialogType.SUCESS);
                        FunctionAnnotations.clearFieldsWithAnnotations(this);
                    } else {
                        DialogFX.showMessage("Contato foi salvo, mas o lembrete não foi salvo. Motivo: " + resultLembrar.mensagem(), "Contato salvo e lembrete não!", DialogType.WARNING);
                        FunctionAnnotations.clearFieldsWithAnnotations(this);
                    }
                } else {
                    DialogFX.showMessage("Contato salvo com sucesso!", "Salvo com Sucesso!", DialogType.SUCESS);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                }
                
            } else {
                DialogFX.showMessage("Houve um erro ao salvar contato! Motivo: " + resultContato.mensagem(), "Erro ao Salvar!", DialogType.ERRO);
            }
            
        }
    }

    public void carregarCursos(){
        Thread t = new Thread(()->{
            Platform.runLater(()->{
                this.cbCurso.setItems(BancoDeDados.curso().queryAll());
            });
        });
        t.setDaemon(true);
        t.start();
    }
    
}
