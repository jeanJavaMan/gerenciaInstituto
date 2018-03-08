/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.enums.ValidateType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Lembrete;
import jeanderson.util.HelpLimit;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/AlunoV2.fxml", title = "Informações dos Alunos")
public class AlunoV2Controller extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Aluno> tvAluno;
    @FXML
    private TableColumn<Aluno, Integer> tcMatricula;
    @FXML
    private TableColumn<Aluno, String> tcNome;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtMatriculaFiltro;
    @FXML
    private JFXTextField txtNomeFiltro;
    @FXML
    private JFXTabPane tbInformacoes;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.CPF_DIG)
    @ValidateField(fieldName = "CPF", type = ValidateType.CPF)
    private JFXTextField txtCPF;
    @FXML
    @ValidateField(fieldName = "RG")
    private JFXTextField txtRG;
    @FXML
    private JFXTextField txtTelefone;
    @FXML
    @ValidateField(fieldName = "RG", type = ValidateType.TELEFONE)
    private JFXTextField txtCelular;
    @FXML
    @ValidateField(fieldName = "Endereço")
    private JFXTextField txtEndereco;
    @FXML
    @ValidateField(fieldName = "Cidade")
    private JFXTextField txtCidade;
    @FXML
    @ValidateField(fieldName = "UF")
    private JFXTextField txtUF;
    @FXML
    private JFXTextField txtResponsavel;
    @FXML
    @MaskFormatter(type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpNascimento;
    @FXML
    private JFXTextArea txtObservacoes;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtMatricula;
    @FXML
    @MaskFormatter(type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpMatricula;
    @FXML
    private JFXButton btnEditar;
    private HelpLimit helpLimit;
    private BooleanProperty editar;
    @FXML
    private JFXTextField txtBairro;
    @FXML
    private JFXTextField txtMatriculadoPor;
    private Aluno aluno;
    private Lembrete lembrete;
    @FXML
    private Text txtAnoIdade;
    @FXML
    private JFXCheckBox ccLembrar;
    @FXML
    private Text txtNaData;
    @FXML
    @MaskFormatter(type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpDataLembrar;
    @FXML
    private ImageView ivLembrarNaData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.editar = new SimpleBooleanProperty(false);
        this.tcMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());

        this.txtNome.editableProperty().bindBidirectional(editar);
        this.txtCPF.editableProperty().bindBidirectional(editar);
        this.txtRG.editableProperty().bindBidirectional(editar);
        this.txtTelefone.editableProperty().bindBidirectional(editar);
        this.txtCelular.editableProperty().bindBidirectional(editar);
        this.txtEndereco.editableProperty().bindBidirectional(editar);
        this.txtBairro.editableProperty().bindBidirectional(editar);
        this.txtCidade.editableProperty().bindBidirectional(editar);
        this.txtUF.editableProperty().bindBidirectional(editar);
        this.txtResponsavel.editableProperty().bindBidirectional(editar);
        this.txtObservacoes.editableProperty().bindBidirectional(editar);
        this.dpMatricula.getEditor().editableProperty().bindBidirectional(editar);
        this.dpNascimento.getEditor().editableProperty().bindBidirectional(editar);
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));
        this.ivLembrarNaData.visibleProperty().bind(editar.not());
        txtNaData.visibleProperty().bind(ccLembrar.selectedProperty());
        dpDataLembrar.visibleProperty().bind(ccLembrar.selectedProperty());
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        Thread t = new Thread(() -> {
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (this.helpLimit.isMaiorQueAnterior()) {
                this.tvAluno.getItems().addAll(BancoDeDados.aluno().getAlunos(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(this::filtrar);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            if (!editar.get()) {
                this.editar.set(true);
                this.btnEditar.setText("Salvar");
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
                    this.aluno.setObservacao(txtObservacoes.getText());
                    this.aluno.setDataMatricula(dpMatricula.getValue());
                    this.aluno.setDataNascimento(dpNascimento.getValue());
                    this.aluno.setResponsavel(!txtResponsavel.getText().isEmpty());
                    this.aluno.setResponsavelPor(txtResponsavel.getText());

                    ResultadoBD result = BancoDeDados.update(this.aluno);
                    ResultadoBD resultLembrete = null;
                    if (result.resultado()) {
                        if (ccLembrar.isSelected() && !dpDataLembrar.getEditor().getText().isEmpty()) {
                            LocalDate data = dpDataLembrar.getValue();
                            if (this.lembrete != null) {
                                if (!lembrete.getDataLembrar().equals(data)) {
                                    lembrete.setDataLembrar(data);
                                    resultLembrete = BancoDeDados.update(lembrete);
                                }
                            } else {
                                this.lembrete = new Lembrete();
                                this.lembrete.setAluno(aluno);
                                this.lembrete.setDataLembrar(data);
                                this.lembrete.setFuncionario(SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario());
                                resultLembrete = BancoDeDados.save(lembrete);
                            }
                        } else if (!ccLembrar.isSelected() && lembrete != null) {
                            resultLembrete = BancoDeDados.delete(lembrete);
                        }
                        if (resultLembrete == null) {
                            DialogFX.showMessage("Edições Salvas com Sucesso! Atualize a Tabela!", "Editado com sucesso!", DialogType.SUCESS);
                        } else {
                            if (!resultLembrete.resultado()) {
                                DialogFX.showMessage("Edições referente ao aluno foi salvo com sucesso! mas houve problema com o lembrete referente ao mesmo! Motivo: " + result.mensagem(), "Problema referente ao lembrete", DialogType.WARNING);
                            } else {
                                DialogFX.showMessage("Edições Salvas com Sucesso! Atualize a Tabela!", "Editado com sucesso!", DialogType.SUCESS);
                            }
                        }
                        this.editar.set(false);
                        this.btnEditar.setText("Editar Dados");
                    } else {
                        DialogFX.showMessage("Houve um erro ao editar! Motivo: " + result.mensagem(), "Erro ao salvar alterações", DialogType.ERRO);
                    }
                }
            }
        } else {
            DialogFX.showMessage("Você deve selecionar um aluno antes!", "Nenhum aluno selecionado", DialogType.INFORMATION);
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            if (DialogFX.showConfirmation("Tem certeza que deseja realmente excluir o aluno seleciona?\nTodo referente ao aluno será perdido!", "Excluir?")) {
                ResultadoBD result = BancoDeDados.aluno().excluirAluno(aluno);
                if (result.resultado()) {
                    DialogFX.showMessage("Aluno foi excluído com sucesso!", "Sucesso", DialogType.SUCESS);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                    tvAluno.getSelectionModel().select(-1);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar excluir! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
                }
            }
        } else {
            DialogFX.showMessage("Você deve selecionar um aluno antes!", "Nenhum aluno selecionado", DialogType.INFORMATION);
        }
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Aluno> query = builder.createQuery(Aluno.class);
        Root<Aluno> from = query.from(Aluno.class);
        Predicate condicao = builder.and();
        if (!this.txtMatriculaFiltro.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.equal(from.get(Aluno_.id), Integer.parseInt(txtMatriculaFiltro.getText())));
        }
        if (!this.txtNomeFiltro.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(from.get(Aluno_.nome), txtNomeFiltro.getText() + "%"));
        }
        Query<Aluno> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        queryPronta.setMaxResults(Integer.parseInt(txtLimite.getText()));
        this.tvAluno.setItems(FXCollections.observableArrayList(queryPronta.getResultList()));
        sessao.close();
    }

    @Override
    public void afterShow() {
        this.txtLimite.setText("50");
        this.btnEditar.setText("Editar Dados");
        this.editar.set(false);
        this.carregar();
    }

    public void carregar() {
        Thread t = new Thread(() -> {
            this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 50));
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void mouseClickAluno(MouseEvent event) {
        if (tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            this.btnEditar.setText("Editar Dados");
            this.editar.set(false);
            this.aluno = tvAluno.getSelectionModel().getSelectedItem();
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
            txtObservacoes.setText(aluno.getObservacao());
            dpMatricula.setValue(aluno.getDataMatricula());
            dpNascimento.setValue(aluno.getDataNascimento());
            txtResponsavel.setText(aluno.getResponsavelPor());
            txtAnoIdade.setText("" + dpNascimento.getValue().until(LocalDate.now()).getYears());

            Thread t = new Thread(this::verificaSeALunoTemLembrete);
            t.setDaemon(true);
            t.start();
        }

    }

    private void verificaSeALunoTemLembrete() {
        this.lembrete = BancoDeDados.lembrete().pegarPorAluno(aluno);
        if(lembrete != null){
            this.ccLembrar.setSelected(true);
            dpDataLembrar.setValue(lembrete.getDataLembrar());
        }
    }
}
