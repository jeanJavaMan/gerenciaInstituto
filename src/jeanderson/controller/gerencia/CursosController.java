/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.model.Curso_;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Cursos.fxml", title = "Cursos cadastrados")
public class CursosController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Curso> tvCurso;
    @FXML
    private TableColumn<Curso, Integer> tcCodigo;
    @FXML
    private TableColumn<Curso, String> tcNome;
    @FXML
    private TableColumn<Curso, String> tcCarga;
    @FXML
    private TableColumn<Curso, Double> tcValor;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCodigo;
    @FXML
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCarga;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValor;
    private ControlWindow<InfoCursos> janelaInfo;
    private ControlWindow<CursosController> janelaCursos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tcCodigo.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.tcCarga.setCellValueFactory(objeto -> new SimpleObjectProperty<>(Long.toString(objeto.getValue().getCargaHoraria().toHours())));
        this.tcValor.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getValor()));


        this.janelaInfo = new ControlWindow(InfoCursos.class);
    }

    public void carregar() {
        Thread t = new Thread(() -> {
            this.tvCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        this.carregar();
    }

    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(this::filtrar);
        t.setDaemon(true);
        t.start();
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Curso> cq = cb.createQuery(Curso.class);
        Root<Curso> from = cq.from(Curso.class);
        Predicate condicao = cb.and();
        if (!this.txtCodigo.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Curso_.id), Integer.parseInt(txtCodigo.getText())));
        }
        if (!this.txtNome.getText().trim().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Curso_.nome), txtNome.getText() + "%"));
        }
        if (!this.txtCarga.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Curso_.cargaHoraria), Duration.ofHours(Long.parseLong(txtCarga.getText()))));
        }
        if (!this.txtValor.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Curso_.valor), FuncoesUtil.validaValor(txtValor.getText())));
        }
        Query<Curso> query = sessao.createQuery(cq.select(from).where(condicao));
        ObservableList<Curso> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        this.tvCurso.setItems(lista);
    }

    @FXML
    private void mouseClickTabela(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvCurso.getSelectionModel().getSelectedIndex() != -1) {
                this.janelaInfo.show(janelaCursos);
                Curso curso = tvCurso.getSelectionModel().getSelectedItem();
                this.janelaInfo.getController().carregaCurso(curso);
            }
        }
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaCursos = control;
    }
}
