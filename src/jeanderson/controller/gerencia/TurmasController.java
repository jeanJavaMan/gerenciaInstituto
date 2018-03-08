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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.model.Turma;
import jeanderson.model.Turma_;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.HelpLimit;
import jeanderson.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Turmas.fxml", title = "Turmas Cadastradas")
public class TurmasController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Turma> tvTurmas;
    @FXML
    private TableColumn<Turma, Integer> tcCodigoTurma;
    @FXML
    private TableColumn<Turma, String> tcCurso;
    @FXML
    private TableColumn<Turma, String> tcHoraria;
    @FXML
    private TableColumn<Turma, Integer> tcQuantidade;
    @FXML
    private TableColumn<Turma, Boolean> tcDisponibilidade;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCodigoTurma;
    @FXML
    private JFXTextField txtHoraria;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtQuantidade;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXComboBox<Boolean> cbDisponibilidade;
    private HelpLimit helpLimit;
    private ControlWindow<InfoTurmasController> janelaInfoTurma;
    private ControlWindow<TurmasController> janelaTurmas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));

        this.tcCodigoTurma.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcHoraria.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getHorario()));
        this.tcCurso.setCellValueFactory(objeto -> objeto.getValue().getCurso().nomeProperty());
        this.tcQuantidade.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getQuantidadeDeAlunos()));
        this.tcDisponibilidade.setCellValueFactory(objeto -> objeto.getValue().isDisponivel());
        this.tcDisponibilidade.setCellFactory(cell -> FuncoesUtil.factoryColunaDisponibilidade());

        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.cbDisponibilidade.setConverter(Converter.TURMA_DISPONIBILIDADE.getStringConverter());
        this.cbDisponibilidade.setItems(FXCollections.observableArrayList(true, false));
        

        this.janelaInfoTurma = new ControlWindow(InfoTurmasController.class);
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        Thread t = new Thread(() -> {
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (this.helpLimit.isMaiorQueAnterior()) {
                this.tvTurmas.getItems().addAll(BancoDeDados.turma().getTurmas(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tvTurmas.setItems(BancoDeDados.turma().getTurmas(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
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
    private void mouseClickTabela(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (this.tvTurmas.getSelectionModel().getSelectedIndex() != -1) {
                Turma turma = tvTurmas.getSelectionModel().getSelectedItem();
                this.janelaInfoTurma.show(janelaTurmas);
                this.janelaInfoTurma.getController().carregarTurma(turma);
            }
        }
    }

    private void filtrar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> cq = cb.createQuery(Turma.class);
        Root<Turma> from = cq.from(Turma.class);
        Predicate condicao = cb.and();
        if (!this.txtCodigoTurma.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Turma_.id), Integer.parseInt(txtCodigoTurma.getText())));
        }
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Turma_.curso), cbCurso.getValue()));
        }
        if (!this.txtHoraria.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Turma_.horario), txtHoraria.getText() + "%"));
        }
        if (!this.txtQuantidade.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Turma_.quantidadeDeAlunos), Integer.parseInt(txtQuantidade.getText())));
        }
        if (this.cbDisponibilidade.getSelectionModel().getSelectedIndex() != -1) {
            Boolean disponibilidade = cbDisponibilidade.getValue();
            Expression<Integer> limiteAlunos = from.get(Turma_.limiteAlunos);
            Expression<Integer> quantidadeDeAlunos = from.get(Turma_.quantidadeDeAlunos);
            if (disponibilidade) {
                condicao = cb.and(condicao, cb.lessThan(quantidadeDeAlunos, limiteAlunos));
            } else {
                condicao = cb.and(condicao, cb.greaterThanOrEqualTo(quantidadeDeAlunos, limiteAlunos));
            }
        }
        Query<Turma> query = sessao.createQuery(cq.select(from).where(condicao));
        query.setFirstResult(0).setMaxResults(Integer.parseInt(txtLimite.getText()));
        ObservableList<Turma> lista = FXCollections.observableArrayList(query.getResultList());
        if (!lista.isEmpty()) {
            Hibernate.initialize(lista.get(0).getAlunos());
            Hibernate.initialize(lista.get(0).getDiasDaSemana());
        }
        sessao.close();
        this.tvTurmas.setItems(lista);

    }

    public void carregarDados() {
        Thread t = new Thread(() -> {
            this.tvTurmas.setItems(BancoDeDados.turma().getTurmas(0, 50));
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void afterShow() {
        this.txtLimite.setText("50");
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaTurmas = control;
    }

}
