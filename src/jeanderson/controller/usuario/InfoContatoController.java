/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.enums.ValidateType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.Converter;
import jeanderson.enums.Situacao;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Contato;
import jeanderson.model.Curso;
import jeanderson.model.Lembrete;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/InfoContato.fxml", title = "Informações Sobre o Contato")
public class InfoContatoController extends EasyFXFunctions implements Initializable {

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
    @ValidateField(fieldName = "Curso Interesse")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXCheckBox ccLembrete;
    @FXML
    private JFXTextArea txtObservacao;
    @FXML
    private JFXDatePicker dpDataLembrar;;
    private ControlWindow<InfoContatoController> janelaInfoContato;
    private Contato contato;
    private boolean salvar;
    private Lembrete lembrete;
    @FXML
    private JFXButton btnEditar;
    private BooleanProperty editar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.dpDataLembrar.visibleProperty().bindBidirectional(ccLembrete.selectedProperty());
        this.cbSituacao.setItems(FXCollections.observableArrayList(Situacao.values()));
        this.cbCurso.setEditable(true);
        this.editar = new SimpleBooleanProperty(false);
        this.txtNome.editableProperty().bindBidirectional(editar);
        txtTelefone.editableProperty().bindBidirectional(editar);
        txtObservacao.editableProperty().bindBidirectional(editar);

    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (salvar) {
            alterarDados();
            this.salvar = false;
            this.btnEditar.setText("Editar");
            this.habilitarCampos(false);
        } else {
            this.ccLembrete.setDisable(false);
            this.habilitarCampos(true);
            this.btnEditar.setText("Salvar");
            this.salvar = true;
        }
    }

    @FXML
    private void actionApagar(ActionEvent event) {
        if (DialogFX.showConfirmation("Tem certeza que deseja apagar este contato?", "Apagar contato")) {
            ResultadoBD resul = BancoDeDados.contato().removeContato(contato);
            if (resul.resultado()) {
                DialogFX.showMessageAndWait("Contato foi apagado com sucesso! Atualize a Tabela.", "Apagado com sucesso!", DialogType.SUCESS);
                this.janelaInfoContato.close();
            } else {
                DialogFX.showMessage("Houve um erro ao tentar apagar contato! Motivo: " + resul.mensagem(), "Erro ao apagar", DialogType.ERRO);
            }
        }
    }

    public void carregarCursosELembrete() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {

                ObservableList<Curso> queryAll = BancoDeDados.curso().queryAll();
                this.cbCurso.setItems(queryAll);
                this.cbCurso.getSelectionModel().select(contato.getCursoInteresse());
                this.lembrete = BancoDeDados.lembrete().pegarPorContato(contato);
                this.verificaSeTemLembrete();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    public void passarContato(Contato contato) {
        this.carregarCursosELembrete();
        this.contato = contato;
        this.txtNome.setText(contato.getNome());
        this.txtTelefone.setText(contato.getTelefone());
        this.txtObservacao.setText(contato.getObservacao());
        this.cbSituacao.getSelectionModel().select(contato.getSituacao());
    }

    private void verificaSeTemLembrete() {
        if (this.lembrete != null) {
            this.ccLembrete.setSelected(true);
            this.dpDataLembrar.setValue(this.lembrete.getDataLembrar());
        }
    }

    private void alterarDados() {
        this.contato.setNome(txtNome.getText().trim());
        this.contato.setTelefone(txtTelefone.getText().trim());
        this.contato.setObservacao(txtObservacao.getText().trim());
        this.contato.setSituacao(this.cbSituacao.getValue());
        this.contato.setCursoInteresse(this.cbCurso.getValue());
        ResultadoBD resultContato = BancoDeDados.update(contato);
        if (resultContato.resultado()) {
            if (this.ccLembrete.isSelected()) {
                ResultadoBD resultLembrar;
                if (this.lembrete == null) {
                    lembrete = new Lembrete();
                    lembrete.setContato(contato);
                    lembrete.setFuncionario(contato.getFuncionario());
                    lembrete.setDataLembrar(this.dpDataLembrar.getValue());
                    resultLembrar = BancoDeDados.save(lembrete);
                } else {
                    lembrete.setDataLembrar(this.dpDataLembrar.getValue());
                    resultLembrar = BancoDeDados.update(lembrete);
                }
                if (resultLembrar.resultado()) {
                    DialogFX.showMessage("Alterado dados com sucesso!", "Alterado com Sucesso!", DialogType.SUCESS);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                } else {
                    DialogFX.showMessage("Contato foi altarado, mas o lembrete não foi alterado. Motivo: " + resultLembrar.mensagem(), "Contato Alterado e lembrete não!", DialogType.WARNING);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                }
            } else {
                DialogFX.showMessage("Contato alterado com sucesso!", "Alterado com Sucesso!", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            }

        } else {
            DialogFX.showMessage("Houve um erro ao alterar dados do contato! Motivo: " + resultContato.mensagem(), "Erro ao Alterar!", DialogType.ERRO);
        }
    }

    private void habilitarCampos(boolean habilitar) {
        this.editar.set(habilitar);
    }

    @Override
    public void afterShow() {
        this.btnEditar.setText("Editar");
        this.salvar = false;
        this.ccLembrete.setDisable(true);
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfoContato = control;
    }

}
