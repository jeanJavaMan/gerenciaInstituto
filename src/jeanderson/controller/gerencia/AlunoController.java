/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Historico;
import jeanderson.model.Historico_;
import jeanderson.util.HelpLimit;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Aluno.fxml", title = "Alunos Matrículados")
public class AlunoController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Aluno> tvAluno;
    @FXML
    private TableColumn<Aluno, Integer> tcMatricula;
    @FXML
    private TableColumn<Aluno, String> tcNome;
    @FXML
    private TableColumn<Aluno, String> tcCPF;
    @FXML
    private TableColumn<Aluno, String> tcCelular;
    @FXML
    private TableColumn<Aluno, String> tcCidade;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    private Separator spCima1;
    @FXML
    private Label lbFiltroExtra;
    @FXML
    private Separator spCima2;
    @FXML
    private Separator spLado;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtMatricula;
    @FXML
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.CPF_DIG)
    private JFXTextField txtCPF;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtCelular;
    @FXML
    private JFXTextField txtCidade;
    @FXML
    private JFXCheckBox ccFiltroExtra;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpNascimento;
    @FXML
    private JFXTextField txtRG;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpMatricula;
    private HelpLimit helpLimit;
    private ControlWindow<AlunoController> janelaAluno;
    private ControlWindow<InfoAlunoController> janelaInfoAluno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.spCima1.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.spCima2.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.spLado.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.lbFiltroExtra.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.dpNascimento.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.dpMatricula.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.txtRG.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.cbCurso.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());

        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));

        this.tcMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.tcCPF.setCellValueFactory(objeto -> objeto.getValue().cpfProperty());
        this.tcCelular.setCellValueFactory(objeto -> objeto.getValue().celularProperty());
        this.tcCidade.setCellValueFactory(objeto -> objeto.getValue().cidadeProperty());        

        this.janelaInfoAluno = new ControlWindow(InfoAlunoController.class);
    }

    @FXML
    private void mouseClickAluno(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (this.tvAluno.getSelectionModel().getSelectedIndex() != -1) {
                Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
                this.janelaInfoAluno.show(janelaAluno);
                this.janelaInfoAluno.getController().mostrarAluno(aluno);
            }
        }
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

    public void carregarDados() {
        Thread t = new Thread(() -> {
            this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 50));
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
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
        if (!this.txtCelular.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(from.get(Aluno_.celular), txtCelular.getText() + "%"));
        }
        if (!this.txtCidade.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(from.get(Aluno_.cidade), txtCidade.getText() + "%"));
        }
        if (this.ccFiltroExtra.isSelected()) {
            if (!this.txtRG.getText().isEmpty()) {
                condicao = builder.and(condicao, builder.like(from.get(Aluno_.RG), txtRG.getText() + "%"));
            }
            if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
                Curso curso = cbCurso.getValue();
                Root<Historico> fromHistorico = query.from(Historico.class);
                condicao = builder.and(condicao, builder.equal(fromHistorico.get(Historico_.curso), curso), builder.equal(from.get(Aluno_.id), fromHistorico.get(Historico_.aluno)));
            }
            if (!this.dpMatricula.getEditor().getText().isEmpty()) {
                LocalDate dataMatricula = dpMatricula.getValue();
                condicao = builder.and(condicao, builder.equal(from.get(Aluno_.dataMatricula), dataMatricula));
            }
            if (!this.dpNascimento.getEditor().getText().isEmpty()) {
                LocalDate dataNascimento = dpNascimento.getValue();
                condicao = builder.and(condicao, builder.equal(from.get(Aluno_.dataNascimento), dataNascimento));
            }
        }
        Query<Aluno> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        this.tvAluno.setItems(FXCollections.observableArrayList(queryPronta.getResultList()));
    }

    @Override
    public void afterShow() {
        this.txtLimite.setText("50");       
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaAluno = control;
    }
}
