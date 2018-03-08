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
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.enums.Semana;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Turma;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoTurmas.fxml", title = "Informações da Turma")
public class InfoTurmasController extends EasyFXFunctions implements Initializable {

    @FXML
    private JFXTextField txtCodigoTurma;
    @FXML
    @ValidateField(fieldName = "Horário da Turma")
    private JFXTextField txtHorarioTurma;
    @FXML
    @ValidateField(fieldName = "Limite de alunos")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimiteAlunos;
    @FXML
    private JFXTextField txtQuantidadeAlunos;
    @FXML
    @ValidateField(fieldName = "Curso referente")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXCheckBox ccSegunda;
    @FXML
    private JFXCheckBox ccTerca;
    @FXML
    private JFXCheckBox ccQuinta;
    @FXML
    private JFXCheckBox ccQuarta;
    @FXML
    private JFXCheckBox ccSexta;
    @FXML
    private JFXCheckBox ccSabado;
    @FXML
    private JFXCheckBox ccDomingo;
    @FXML
    private JFXButton btnEditarInformacoes;
    @FXML
    private TableView<Aluno> tvAlunoTurmas;
    @FXML
    private TableColumn<Aluno, Integer> tcMatriculaAlunoTurma;
    @FXML
    private TableColumn<Aluno, String> tcNomeAlunoTurma;
    @FXML
    private JFXButton btnAdicionarAluno;
    @FXML
    private Separator spLado;
    @FXML
    private Text textAdicionarAluno;
    @FXML
    private TableView<Aluno> tvAlunos;
    @FXML
    private TableColumn<Aluno, Integer> tcMatricula;
    @FXML
    private TableColumn<Aluno, String> tcNome;
    @FXML
    private TableColumn<Aluno, String> tcCPF;
    @FXML
    private JFXTextField txtMatricula;
    @FXML
    private JFXTextField txtNome;
    @FXML
    private JFXTextField txtCPF;
    @FXML
    private JFXButton btnFiltrar;
    @FXML
    private JFXButton btnAdiciona;
    @FXML
    private JFXButton btnCancela;
    private Turma turma;
    private BooleanProperty isEditarInformacoes;
    private BooleanProperty isAdicionarAluno;
    private boolean jaCarregouTabelaAluno;
    private ControlWindow<InfoTurmasController> janelaInfoTurma;
    private ControlWindow<InfoAlunoController> janelaInfoAluno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tcMatriculaAlunoTurma.setCellValueFactory(objeto -> objeto.getValue().idProperty().asObject());
        this.tcNomeAlunoTurma.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());

        this.tcMatricula.setCellValueFactory(objeto -> objeto.getValue().idProperty().asObject());
        this.tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.tcCPF.setCellValueFactory(objeto -> objeto.getValue().cpfProperty());

        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());

        this.isEditarInformacoes = new SimpleBooleanProperty(false);
        this.isAdicionarAluno = new SimpleBooleanProperty(false);

        this.txtHorarioTurma.editableProperty().bindBidirectional(isEditarInformacoes);
        this.txtLimiteAlunos.editableProperty().bindBidirectional(isEditarInformacoes);
        this.spLado.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.textAdicionarAluno.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.txtMatricula.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.txtNome.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.txtCPF.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.tvAlunos.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.btnAdiciona.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.btnCancela.visibleProperty().bindBidirectional(isAdicionarAluno);
        this.btnFiltrar.visibleProperty().bindBidirectional(isAdicionarAluno);

        this.janelaInfoAluno = new ControlWindow(InfoAlunoController.class);

    }

    @FXML
    private void actionEditarInformacoes(ActionEvent event) {
        if (isEditarInformacoes.get() && FunctionAnnotations.validateFields(this)) {
            this.turma.setHorario(txtHorarioTurma.getText());
            turma.setCurso(cbCurso.getValue());
            turma.setLimiteAlunos(Integer.parseInt(txtLimiteAlunos.getText()));
            turma.setDiasDaSemana(pegarSemanasSelecionadas());
            ResultadoBD result = BancoDeDados.update(turma);
            if (result.resultado()) {
                this.isEditarInformacoes.set(false);
                this.btnEditarInformacoes.setText("Editar Informações");
                DialogFX.showMessage("Informações Editadas com sucesso!", "Editado com Sucesso!", DialogType.SUCESS);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar editar informações! Motivo: " + result.mensagem(), "Erro ao tentar editar", DialogType.ERRO);
            }
        } else {
            isEditarInformacoes.set(true);
            this.btnEditarInformacoes.setText("Salvar Edição");
        }
    }

    @FXML
    private void actionExcluirTurma(ActionEvent event) {
        if (DialogFX.showConfirmation("Tem certeza que deseja excluir está turma?", "Excluir realmente a turma?")) {
            ResultadoBD result = BancoDeDados.delete(turma);
            if (result.resultado()) {
                DialogFX.showMessageAndWait("Turma excluída com sucesso! Atualize a Tabela", "Turma excluida", DialogType.SUCESS);
                this.janelaInfoTurma.close();
            } else {
                DialogFX.showMessageAndWait("Houve um erro ao tentar excluir a turma! Motivo: " + result.mensagem(), "Erro ao excluir a turma", DialogType.ERRO);
            }
        }
    }

    @FXML
    private void mouseClickAlunos(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvAlunoTurmas.getSelectionModel().getSelectedIndex() != -1) {
                Aluno aluno = tvAlunoTurmas.getSelectionModel().getSelectedItem();
                this.janelaInfoAluno.show(janelaInfoTurma);
                this.janelaInfoAluno.getController().carregarCursos();
                this.janelaInfoAluno.getController().mostrarAluno(aluno);
            }
        }
    }

    @FXML
    private void actionAdicionarAluno(ActionEvent event) {
        if (!jaCarregouTabelaAluno) {
            this.carregarAlunos();
        }
        this.isAdicionarAluno.set(true);
        this.btnAdicionarAluno.setDisable(true);
    }

    @FXML
    private void actionRemoverAluno(ActionEvent event) {
        if (this.tvAlunoTurmas.getSelectionModel().getSelectedIndex() != -1) {
            if (DialogFX.showConfirmation("Tem certeza que deseja remover este aluno da Turma?", "Remover aluno da turma")) {
                Aluno aluno = tvAlunoTurmas.getSelectionModel().getSelectedItem();
                ResultadoBD result = BancoDeDados.turma().removeAluno(aluno,turma);
                if (result.resultado()) {
                    tvAlunoTurmas.getItems().remove(aluno);
                    this.turma.removeAluno(aluno);
                    this.txtQuantidadeAlunos.setText(Integer.toString(tvAlunoTurmas.getItems().size()));
                    DialogFX.showMessage("Aluno foi removido com sucesso da Turma! Atualize da Tabela", "Aluno removido da turma com sucesso", DialogType.SUCESS);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar remover aluno da turma! Motivo: " + result.mensagem(), "Erro ao remover aluno", DialogType.ERRO);
                }
            }
        } else {
            DialogFX.showMessage("Selecione um aluno antes", "Selecione um aluno", DialogType.INFORMATION);
        }
    }

    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.filtrar();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionAdiciona(ActionEvent event) {
        if (this.tvAlunos.getSelectionModel().getSelectedIndex() != -1) {
            Aluno aluno = tvAlunos.getSelectionModel().getSelectedItem();
            boolean contemAluno = false;
            for (Aluno a : turma.getAlunos()) {
                if (Objects.equals(a.getId(), aluno.getId())) {
                    contemAluno = true;
                    break;
                }
            }
            if (!contemAluno) {
                this.turma.addAluno(aluno);
                ResultadoBD result = BancoDeDados.update(turma);
                if (result.resultado()) {
                    this.tvAlunoTurmas.getItems().add(aluno);
                    this.txtQuantidadeAlunos.setText(Integer.toString(turma.getAlunos().size()));
                    DialogFX.showMessage("Aluno adicionado com sucesso na Turma!", "Aluno Adicionado com sucesso!", DialogType.SUCESS);
                } else {
                    this.turma.removeAluno(aluno);
                    DialogFX.showMessage("Houve um erro ao tentar adicionar o aluno na turma! Motivo: " + result.mensagem(), "Erro ao adicionar aluno", DialogType.ERRO);
                }
            } else {
                DialogFX.showMessage("O Aluno já está na Turma!", "Aluno existente na Turma", DialogType.WARNING);
            }
        }
    }

    @FXML
    private void actionCancelar(ActionEvent event) {
        this.isAdicionarAluno.set(false);
        this.btnAdicionarAluno.setDisable(false);
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Aluno> query = builder.createQuery(Aluno.class);
        Root<Aluno> from = query.from(Aluno.class);
        Predicate condicao = builder.and();
        if (!this.txtMatricula.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.equal(from.get(Aluno_.id), Integer.parseInt(txtMatricula.getText())));
        }
        if (!this.txtNome.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(from.get(Aluno_.nome), txtNome.getText() + "%"));
        }
        if (!this.txtCPF.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(from.get(Aluno_.cpf), txtCPF.getText() + "%"));
        }
        Query<Aluno> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        this.tvAlunos.setItems(FXCollections.observableArrayList(queryPronta.getResultList()));
        sessao.close();
    }

    public void carregarCurso() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.cbCurso.setItems(BancoDeDados.curso().queryAll());
            });
        });
        t.setDaemon(true);
        t.start();
    }

    public void carregarAlunos() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.tvAlunos.setItems(BancoDeDados.aluno().getAlunos(0, 20));
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private List<Semana> pegarSemanasSelecionadas() {
        List<Semana> listaSemana = new ArrayList<>();
        if (this.ccSegunda.isSelected()) {
            listaSemana.add(Semana.SEGUNDA);
        }
        if (this.ccTerca.isSelected()) {
            listaSemana.add(Semana.TERCA);
        }
        if (this.ccQuarta.isSelected()) {
            listaSemana.add(Semana.QUARTA);
        }
        if (this.ccQuinta.isSelected()) {
            listaSemana.add(Semana.QUINTA);
        }
        if (this.ccSexta.isSelected()) {
            listaSemana.add(Semana.SEXTA);
        }
        if (this.ccSabado.isSelected()) {
            listaSemana.add(Semana.SABADO);
        }
        if (this.ccDomingo.isSelected()) {
            listaSemana.add(Semana.DOMINGO);
        }
        return listaSemana;
    }

    public void carregarTurma(Turma turma) {
        this.carregarCurso();
        this.turma = turma;
        this.txtCodigoTurma.setText(turma.getId().toString());
        this.txtHorarioTurma.setText(turma.getHorario());
        this.txtLimiteAlunos.setText(turma.getLimiteAlunos().toString());
        this.txtQuantidadeAlunos.setText(turma.getQuantidadeDeAlunos().toString());
        this.cbCurso.getSelectionModel().select(turma.getCurso());
        limparDiasDaSemana();
        List<Semana> semanaDeAulas = turma.getDiasDaSemana();
        semanaDeAulas.forEach((diaDaSemana) -> {
            switch (diaDaSemana) {
                case SEGUNDA:
                    this.ccSegunda.setSelected(true);
                    break;
                case TERCA:
                    this.ccTerca.setSelected(true);
                    break;
                case QUARTA:
                    this.ccQuarta.setSelected(true);
                    break;
                case QUINTA:
                    this.ccQuinta.setSelected(true);
                    break;
                case SEXTA:
                    this.ccSexta.setSelected(true);
                    break;
                case SABADO:
                    this.ccSabado.setSelected(true);
                    break;
                case DOMINGO:
                    this.ccDomingo.setSelected(true);
                    break;
            }
        });
        this.tvAlunoTurmas.setItems(FXCollections.observableArrayList(turma.getAlunos()));
        this.btnAdicionarAluno.setDisable(!turma.isDisponivel().get());
    }

    private void limparDiasDaSemana() {
        this.ccSegunda.setSelected(false);
        this.ccTerca.setSelected(false);
        this.ccQuarta.setSelected(false);
        this.ccQuinta.setSelected(false);
        this.ccSexta.setSelected(false);
        this.ccSabado.setSelected(false);
        this.ccDomingo.setSelected(false);
    }

    @Override
    public void afterShow() {
        this.isAdicionarAluno.set(false);
        this.isEditarInformacoes.set(false);
        this.jaCarregouTabelaAluno = false;
        this.btnEditarInformacoes.setText("Editar Informações");
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfoTurma = control;
    }
}
