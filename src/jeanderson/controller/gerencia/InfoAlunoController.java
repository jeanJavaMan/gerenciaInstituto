/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

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
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Curso;
import jeanderson.model.Historico;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoAluno.fxml", title = "Informações do Aluno")
public class InfoAlunoController extends EasyFXFunctions implements Initializable{

    @FXML
    private JFXTextField txtMatricula;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    @ValidateField(fieldName = "Data de Matrícula")
    private JFXDatePicker dpMatricula;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.CPF_DIG)
    @ValidateField(fieldName = "CPF", type = ValidateType.CPF)
    private JFXTextField txtCPF;
    @FXML
    @ValidateField(fieldName = "RG")
    private JFXTextField txtRG;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    @ValidateField(fieldName = "Data de Nascimento")
    private JFXDatePicker dpNascimento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    @ValidateField(fieldName = "Celular", type = ValidateType.TELEFONE)
    private JFXTextField txtCelular;
    @FXML
    @ValidateField(fieldName = "Endereço")
    private JFXTextField txtEndereco;
    @FXML
    @ValidateField(fieldName = "Bairro")
    private JFXTextField txtBairro;
    @FXML
    @ValidateField(fieldName = "Cidade")
    private JFXTextField txtCidade;
    @FXML
    @ValidateField(fieldName = "UF")
    private JFXTextField txtUF;
    @FXML
    private JFXCheckBox ccResponsavel;
    @FXML
    private JFXTextField txtResponsavelPor;
    @FXML
    private JFXTextArea txtObservacao;
    @FXML
    private JFXTextField txtMatriculadoPor;
    @FXML
    private TableView<Historico> tvHistorico;
    @FXML
    private TableColumn<Historico, String> tcCurso;
    @FXML
    private TableColumn<Historico, LocalDate> tcDataMatriculaCurso;
    @FXML
    private TableColumn<Historico, LocalDate> tcDataConcluido;
    @FXML
    private TableColumn<Historico, Boolean> tcSituacao;
    @FXML
    private Separator spLado1;
    @FXML
    private Separator spLado2;
    @FXML
    private Label lbEditarHistorico;
    @FXML
    private Separator spBaixo;
    @FXML
    private JFXButton btnSalvar;
    @FXML
    private JFXButton btnCancelar;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXDatePicker dpHistoricoMatricula;
    @FXML
    private JFXDatePicker dpHistoricoConclusao;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    private BooleanProperty editarHistorico;
    private Boolean novoHistorico;
    private ControlWindow<InfoAlunoController> janelaInfoAluno;
    private Aluno aluno;
    private BooleanProperty editar;
    private Historico historico;
    @FXML
    private Text textDataMatricula;
    @FXML
    private Text textDataConclusao;
    @FXML
    private JFXButton btnEditarInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.editar = new SimpleBooleanProperty(false);
        this.editarHistorico = new SimpleBooleanProperty(false);
        
        this.txtNome.editableProperty().bindBidirectional(editar);
        this.txtCPF.editableProperty().bindBidirectional(editar);
        this.txtRG.editableProperty().bindBidirectional(editar);
        this.txtTelefone.editableProperty().bindBidirectional(editar);
        this.txtCelular.editableProperty().bindBidirectional(editar);
        this.txtEndereco.editableProperty().bindBidirectional(editar);
        this.txtBairro.editableProperty().bindBidirectional(editar);
        this.txtCidade.editableProperty().bindBidirectional(editar);
        this.txtUF.editableProperty().bindBidirectional(editar);
        this.txtResponsavelPor.editableProperty().bindBidirectional(editar);
        this.txtObservacao.editableProperty().bindBidirectional(editar);
        this.dpMatricula.getEditor().editableProperty().bindBidirectional(editar);
        this.dpNascimento.getEditor().editableProperty().bindBidirectional(editar);
        this.txtResponsavelPor.editableProperty().bindBidirectional(editar);

        this.spLado1.visibleProperty().bindBidirectional(editarHistorico);
        this.spLado2.visibleProperty().bindBidirectional(editarHistorico);
        this.spBaixo.visibleProperty().bindBidirectional(editarHistorico);
        this.lbEditarHistorico.visibleProperty().bindBidirectional(editarHistorico);
        this.cbCurso.visibleProperty().bindBidirectional(editarHistorico);
        this.cbSituacao.visibleProperty().bindBidirectional(editarHistorico);
        this.textDataMatricula.visibleProperty().bindBidirectional(editarHistorico);
        this.textDataConclusao.visibleProperty().bindBidirectional(editarHistorico);
        this.btnSalvar.visibleProperty().bindBidirectional(editarHistorico);
        this.btnCancelar.visibleProperty().bindBidirectional(editarHistorico);
        this.dpHistoricoConclusao.visibleProperty().bindBidirectional(editarHistorico);
        this.dpHistoricoMatricula.visibleProperty().bindBidirectional(editarHistorico);
        this.txtResponsavelPor.visibleProperty().bindBidirectional(ccResponsavel.selectedProperty());

        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.cbSituacao.setConverter(Converter.SITUACAO_HISTORICO.getStringConverter());
        this.cbSituacao.setItems(FXCollections.observableArrayList(true, false));       

        this.tcCurso.setCellValueFactory(objeto -> objeto.getValue().getCurso().nomeProperty());
        this.tcDataConcluido.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataConclusao()));
        this.tcDataConcluido.setCellFactory(TextFieldTableCell.forTableColumn(Converter.DATAS.getStringConverter()));
        this.tcDataMatriculaCurso.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataMatriculaCurso()));
        this.tcDataMatriculaCurso.setCellFactory(TextFieldTableCell.forTableColumn(Converter.DATAS.getStringConverter()));
        this.tcSituacao.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getSituacao()));
        this.tcSituacao.setCellFactory(cell -> FuncoesUtil.factoryColunaSituacaoHistorico());
    }

    @FXML
    private void actionEditarInformacoes(ActionEvent event) {
        System.out.println(editar.get());
        if (!editar.get()) {
            this.editar.set(true);
            this.btnEditarInfo.setText("Salvar");
        } else {
            if (FunctionAnnotations.validateFields(this)) {
                this.aluno.setNome(txtNome.getText());
                this.aluno.setCpf(txtCPF.getText());
                this.aluno.setRG(txtRG.getText());
                this.aluno.setTelefone(txtTelefone.getText());
                this.aluno.setCelular(txtCelular.getText());
                this.aluno.setEndereco(txtEndereco.getText());
                this.aluno.setBairro(txtBairro.getText());
                this.aluno.setCidade(txtCidade.getText());
                this.aluno.setUf(txtUF.getText());
                this.aluno.setObservacao(txtObservacao.getText());
                this.aluno.setDataMatricula(dpMatricula.getValue());
                this.aluno.setDataNascimento(dpNascimento.getValue());
                if (ccResponsavel.isSelected()) {
                    this.aluno.setResponsavel(true);
                    this.aluno.setResponsavelPor(txtResponsavelPor.getText());
                }

                ResultadoBD result = BancoDeDados.update(this.aluno);
                if (result.resultado()) {
                    DialogFX.showMessage("Edições Salvas com Sucesso! Atualize a Tabela!", "Editado com sucesso!", DialogType.SUCESS);
                    this.editar.set(false);
                    this.btnEditarInfo.setText("Editar Informações");
                } else {
                    DialogFX.showMessage("Houve um erro ao editar! Motivo: " + result.mensagem(), "Erro ao salvar alterações", DialogType.ERRO);
                }
            }
        }
    }

    @FXML
    private void actionExcluirAluno(ActionEvent event) {
        if (DialogFX.showConfirmation("Tudo referente ao Aluno será excluído, como Frequencia, Mensalidade e será removido da Turma.\nTem certeza que Deseja excluir este aluno?", "Excluir Aluno")) {
            ResultadoBD result = BancoDeDados.aluno().excluirAluno(aluno);
            if (result.resultado()) {
                DialogFX.showMessageAndWait("O Aluno foi excluído com Sucesso! Atualize a Tabela", "Aluno Excluído com sucesso!", DialogType.SUCESS);
                this.janelaInfoAluno.close();
            } else {
                DialogFX.showMessage("Houve um erro ao tentar excluir o aluno! Motivo: " + result.mensagem(), "Erro ao excluir aluno!", DialogType.ERRO);
            }
        }
    }

    @FXML
    private void actionNovoHistorico(ActionEvent event) {
        this.lbEditarHistorico.setText("Novo Histórico");
        this.editarHistorico.set(true);
        this.carregarCursos();
        this.novoHistorico = true;
        FunctionAnnotations.clearFieldsWithAnnotations(this);
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        this.carregarCursos();
        if (this.tvHistorico.getSelectionModel().getSelectedIndex() != -1) {
            this.lbEditarHistorico.setText("Editar Histórico");
            this.novoHistorico = false;
            this.historico = tvHistorico.getSelectionModel().getSelectedItem();
            this.editarHistorico.set(true);
            this.cbCurso.getSelectionModel().select(historico.getCurso());
            this.dpHistoricoMatricula.setValue(historico.getDataMatriculaCurso());
            this.dpHistoricoConclusao.setValue(historico.getDataConclusao());
            this.cbSituacao.getSelectionModel().select(historico.getSituacao());
        } else {
            DialogFX.showMessage("Selecione um Histórico antes!", "Nenhum Histórico selecionado.", DialogType.INFORMATION);
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (this.tvHistorico.getSelectionModel().getSelectedIndex() != -1) {
            this.historico = tvHistorico.getSelectionModel().getSelectedItem();
            if (DialogFX.showConfirmation("Deseja realmente apagar este histórico?", "Apagar Histórico")) {
                ResultadoBD result = BancoDeDados.delete(historico);
                if (result.resultado()) {
                    tvHistorico.getItems().remove(historico);
                    DialogFX.showMessage("Histórico removido com sucesso! Atualize a Tabela", "Histórico apagado!", DialogType.SUCESS);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar apagar histórico. Motivo: " + result.mensagem(), "Erro ao tentar apagar histórico", DialogType.ERRO);
                }
            }
        } else {
            DialogFX.showMessage("Selecione um Histórico antes!", "Nenhum Histórico selecionado.", DialogType.INFORMATION);
        }
    }

    @FXML
    private void actionSalvar(ActionEvent event) {
        if (novoHistorico) {
            this.historico = new Historico();
            this.historico.setAluno(aluno);
            this.historico.setCurso(cbCurso.getValue());
            this.historico.setDataConclusao(dpHistoricoConclusao.getValue());
            this.historico.setDataMatriculaCurso(dpHistoricoMatricula.getValue());
            this.historico.setSituacao(cbSituacao.getValue());
            ResultadoBD result = BancoDeDados.save(historico);
            if (result.resultado()) {
                this.editarHistorico.set(false);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
                this.novoHistorico = false;
                this.tvHistorico.getItems().add(historico);
                DialogFX.showMessage("Historico Salvo com sucesso!", "Historico Salvo!", DialogType.SUCESS);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar salvar Histórico! Motivo: " + result.mensagem(), "Erro ao salvar Histórico", DialogType.ERRO);
            }
        } else {
            this.historico.setCurso(cbCurso.getValue());
            this.historico.setDataConclusao(dpHistoricoConclusao.getValue());
            this.historico.setDataMatriculaCurso(dpHistoricoMatricula.getValue());
            this.historico.setSituacao(cbSituacao.getValue());
            ResultadoBD result = BancoDeDados.update(historico);
            if (result.resultado()) {
                this.editarHistorico.set(false);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
                DialogFX.showMessage("Historico editado com sucesso!", "Historico Editado!", DialogType.SUCESS);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar editar Histórico! Motivo: " + result.mensagem(), "Erro ao editar Histórico", DialogType.ERRO);
            }
        }
    }

    @FXML
    private void actionCancelar(ActionEvent event) {
        this.editarHistorico.set(false);
        this.novoHistorico = false;
    }

    public void mostrarAluno(Aluno aluno) {
        this.aluno = aluno;
        txtMatricula.setText(aluno.getId().toString());
        txtNome.setText(aluno.getNome());
        txtCPF.setText(aluno.getCpf());
        txtRG.setText(aluno.getRG());
        txtTelefone.setText(aluno.getTelefone());
        txtCelular.setText(aluno.getCelular());
        txtEndereco.setText(aluno.getEndereco());
        txtBairro.setText(aluno.getBairro());
        txtCidade.setText(aluno.getCidade());
        txtUF.setText(aluno.getUf());
        txtMatriculadoPor.setText(aluno.getFuncionarioMatricula().getNome());
        txtObservacao.setText(aluno.getObservacao());
        dpMatricula.setValue(aluno.getDataMatricula());
        dpNascimento.setValue(aluno.getDataNascimento());
        ccResponsavel.setSelected(aluno.getResponsavel());
        txtResponsavelPor.setText(aluno.getResponsavelPor());
        this.carregarHistorico();
    }

    public void carregarCursos() {
        if (this.cbCurso.getItems().isEmpty()) {
            Thread t = new Thread(() -> {
                Platform.runLater(() -> {
                    this.cbCurso.setItems(BancoDeDados.curso().queryAll());
                });
            });
            t.setDaemon(true);
            t.start();
        }
    }

    private void carregarHistorico() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.tvHistorico.setItems(BancoDeDados.historico().getHistoricoPorAluno(aluno));
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void afterShow() {
        this.editarHistorico.set(false);
        this.novoHistorico = false;
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfoAluno = control;
    }
}
