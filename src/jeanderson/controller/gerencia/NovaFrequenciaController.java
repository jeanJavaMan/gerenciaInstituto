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
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Frequencia;
import jeanderson.util.FuncoesUtil;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/NovaFrequencia.fxml",title = "Nova Frequência" )
public class NovaFrequenciaController extends EasyFXFunctions implements Initializable{

    @FXML
    @ValidateField(fieldName = "Aluno")
    private TableView<Aluno> tvAluno;
    @FXML
    private TableColumn<Aluno, Integer> tcAlunoMatricula;
    @FXML
    private TableColumn<Aluno, String> tcAluno;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtMatricula;
    @FXML
    private JFXTextField txtAluno;
    @FXML
    private JFXTextField txtAlunoSelecionado;
    @FXML
    private JFXTextField txtCPF;
    @FXML
    private JFXTextField txtRG;
    @FXML
    @ValidateField(fieldName = "Curso Referente")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXTextField txtCargaHoraria;
    @FXML
    @NotClear
    @ValidateField(fieldName = "Carga Cumprida")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCargaCumprida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tvAluno.getSelectionModel().selectedItemProperty().addListener(this::actionSelecionaAluno);
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());

        this.tcAlunoMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcAluno.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        
    }

    public void carregarDados() {
        Thread t = new Thread(() -> {
            this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 50));
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
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
    private void actionSelecionaCurso(ActionEvent event) {
        if (cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            txtCargaHoraria.setText(Long.toString(curso.getCargaHoraria().toHours()) + "H");
        }
    }

    @FXML
    private void actionGerarFrequencia(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            Curso curso = cbCurso.getValue();
            Frequencia frequencia = new Frequencia();
            frequencia.setAluno(aluno);
            frequencia.setCurso(curso);
            frequencia.setCargaHoraria(curso.getCargaHoraria());
            Duration CargaCumprida = Duration.ofHours(Long.parseLong(txtCargaCumprida.getText()));
            frequencia.setCargaCumprida(CargaCumprida);
            Integer porcentagemConcluida = FuncoesUtil.calculaPorcentagem((int) curso.getCargaHoraria().toHours(), CargaCumprida.toHours());
            frequencia.setPorcentagemConcluida(porcentagemConcluida);
            ResultadoBD result = BancoDeDados.save(frequencia);
            if (result.resultado()) {
                DialogFX.showMessage("Nova frequência gerada com sucesso! Atualize a Tabela", "Gerado com sucesso!", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar gerar a frequência! Motivo: " + result.mensagem(), "Erro ao gerar", DialogType.ERRO);
            }
        }
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Aluno> cq = cb.createQuery(Aluno.class);
        Root<Aluno> from = cq.from(Aluno.class);
        Predicate condicao = cb.and();
        if (!this.txtMatricula.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Aluno_.id), Integer.parseInt(txtMatricula.getText())));
        }
        if (!this.txtAluno.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Aluno_.nome), txtAluno.getText() + "%"));
        }
        Query<Aluno> query = sessao.createQuery(cq.select(from).where(condicao));
        ObservableList<Aluno> lista = FXCollections.observableArrayList(query.getResultList());
        this.tvAluno.setItems(lista);
    }

    private void actionSelecionaAluno(Observable valor) {
        if (this.tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            this.txtAlunoSelecionado.setText(aluno.getNome());
            this.txtCPF.setText(aluno.getCpf());
            this.txtRG.setText(aluno.getRG());
        }
    }

    @Override
    public void afterShow() {
        txtCargaCumprida.setText("0");
    }
}
