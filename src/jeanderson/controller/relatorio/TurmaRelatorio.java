/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.relatorio;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.jasper.JasperViewFX;
import br.jeanderson.util.DialogFX;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.model.GeraRelatorio;
import jeanderson.model.Turma;
import jeanderson.model.Turma_;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.HibernateUtil;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/relatorio/TurmaRelatorio.fxml")
public class TurmaRelatorio extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Turma> tvTurma;
    @FXML
    private TableColumn<Turma, Integer> tcTurmaNumero;
    @FXML
    private TableColumn<Turma, String> tcCursoReferente;
    @FXML
    private TableColumn<Turma, String> tcHorario;
    @FXML
    private TableColumn<Turma, Boolean> tcDisponibilidade;
    @FXML
    private JFXTextField txtTurmaNumero;
    @FXML
    private JFXTextField txtHorario;
    @FXML
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXComboBox<Boolean> cbDisponibilidade;
    @FXML
    private JFXButton btnGerar;
    @FXML
    private JFXSpinner spGerando;
    @FXML
    private Text txtGerando;
    private ControlWindow<TurmaRelatorio> janela;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tcTurmaNumero.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcCursoReferente.setCellValueFactory(objeto -> objeto.getValue().getCurso().nomeProperty());
        this.tcDisponibilidade.setCellValueFactory(objeto -> objeto.getValue().isDisponivel());
        this.tcHorario.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getHorario()));
        this.tcDisponibilidade.setCellFactory(cell -> FuncoesUtil.factoryColunaDisponibilidade());
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.cbDisponibilidade.setConverter(Converter.TURMA_DISPONIBILIDADE.getStringConverter());
        this.cbDisponibilidade.setItems(FXCollections.observableArrayList(true, false));
        txtGerando.visibleProperty().bind(spGerando.visibleProperty());
        spGerando.visibleProperty().bind(btnGerar.disableProperty());
    }

    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(this::filtrar);
        t.setDaemon(true);
        t.start();
    }

    private void filtrar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> cq = cb.createQuery(Turma.class);
        Root<Turma> from = cq.from(Turma.class);
        Predicate condicao = cb.and();
        if (!this.txtTurmaNumero.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Turma_.id), Integer.parseInt(txtTurmaNumero.getText())));
        }
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Turma_.curso), cbCurso.getValue()));
        }
        if (!this.txtHorario.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Turma_.horario), txtHorario.getText() + "%"));
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
        query.setFirstResult(0).setMaxResults(30);
        ObservableList<Turma> lista = FXCollections.observableArrayList(query.getResultList());
        if (!lista.isEmpty()) {
            Hibernate.initialize(lista.get(0).getAlunos());
        }
        sessao.close();
        this.tvTurma.setItems(lista);
    }

    public void carregar() {
        Thread t = new Thread(() -> {
            tvTurma.setItems(BancoDeDados.turma().getTurmas(0, 30));
            cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionGerarRelatorio(ActionEvent event) {
        if (tvTurma.getSelectionModel().getSelectedIndex() != -1) {
            btnGerar.setDisable(true);
            Thread t = new Thread(this::gerar);
            t.setDaemon(true);
            t.start();
        } else {
            DialogFX.showMessage("Selecione uma turma antes!", "Selecione", DialogType.INFORMATION);
        }
    }

    private void gerar() {
        Turma turma = tvTurma.getSelectionModel().getSelectedItem();
        if (turma.getAlunos().size() > 0) {
            GeraRelatorio geraRelatorio = new GeraRelatorio();
            JasperPrint jp = geraRelatorio.gerarRelatorioTurma(turma);
            Platform.runLater(() -> {
                btnGerar.setDisable(false);
                if (jp != null) {
                    JasperViewFX view = new JasperViewFX(jp, "Relatorio de Turma");
                    view.show(janela);
                } else {
                    btnGerar.setDisable(false);
                    DialogFX.showMessage("Houve algum erro ao gerar relátorio!", "Erro", DialogType.ERRO);
                }
            });
        } else {
            Platform.runLater(() -> {
                btnGerar.setDisable(false);
                DialogFX.showMessage("Não há nenhum aluno na turma selecionada! Não pode gerar relatórios sem dados.", "Turma vazia", DialogType.INFORMATION);
            });
        }
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janela = control;
    }

    @Override
    public void afterShow() {
        this.carregar();
    }

}
