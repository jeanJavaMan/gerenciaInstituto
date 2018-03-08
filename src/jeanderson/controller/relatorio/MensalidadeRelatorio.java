/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.relatorio;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.jasper.JasperViewFX;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.enums.MensalidadeTipo;
import jeanderson.enums.PagamentoTipo;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.GeraRelatorio;
import jeanderson.model.Mensalidade;
import jeanderson.model.Mensalidade_;
import jeanderson.util.HibernateUtil;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/relatorio/MensalidadeRelatorio.fxml")
public class MensalidadeRelatorio extends EasyFXFunctions implements Initializable {

    @FXML
    @NotClear
    private JFXCheckBox ccVerTodos;
    @FXML
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
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXComboBox<MensalidadeTipo> cbTipoMensalidade;
    @FXML
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpGeracaoInicial;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpGeracaoAte;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimentoInicial;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimentoAte;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpPagamentoInicial;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpPagamentoAte;
    @FXML
    private JFXButton btnGerar;
    @FXML
    private JFXSpinner spGerando;
    @FXML
    private Text txtGerando;
    @FXML
    @MaskFormatter(type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    @NotClear
    private JFXCheckBox ccVerTodosResultados;
    private ControlWindow<MensalidadeRelatorio> janelaRelatorio;
    @FXML
    private JFXButton btnFiltrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbTipoMensalidade.setItems(FXCollections.observableArrayList(MensalidadeTipo.values()));
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.tcAlunoMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcAluno.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.cbSituacao.setItems(FXCollections.observableArrayList(true, false));
        this.cbSituacao.setConverter(Converter.SITUACAO_PAGAMENTO.getStringConverter());

        txtAluno.disableProperty().bind(ccVerTodos.selectedProperty());
        txtAlunoSelecionado.disableProperty().bind(ccVerTodos.selectedProperty());
        txtMatricula.disableProperty().bind(ccVerTodos.selectedProperty());
        txtCPF.disableProperty().bind(ccVerTodos.selectedProperty());
        txtRG.disableProperty().bind(ccVerTodos.selectedProperty());
        tvAluno.disableProperty().bind(ccVerTodos.selectedProperty());
        btnFiltrar.disableProperty().bind(ccVerTodos.selectedProperty());

        txtGerando.visibleProperty().bind(spGerando.visibleProperty());
        spGerando.visibleProperty().bind(btnGerar.disableProperty());
        txtLimite.disableProperty().bind(ccVerTodosResultados.selectedProperty());
    }

    @FXML
    private void actionMouseClickAluno(MouseEvent event) {
        if (tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            txtAlunoSelecionado.setText(aluno.getNome());
            txtCPF.setText(aluno.getCpf());
            txtRG.setText(aluno.getRG());
        }
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

    @FXML
    private void actionGerarRelatorio(ActionEvent event) {
        btnGerar.setDisable(true);
        Thread t = new Thread(this::gerarRelatorio);
        t.setDaemon(true);
        t.start();
    }

    private void gerarRelatorio() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Mensalidade> query = cb.createQuery(Mensalidade.class);
        Root<Mensalidade> from = query.from(Mensalidade.class);
        Predicate condicao = cb.and();
        if (!ccVerTodos.isSelected() && tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.equal(from.get(Mensalidade_.aluno), tvAluno.getSelectionModel().getSelectedItem());
        }
        if (cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            condicao = cb.and(condicao, cb.like(from.get(Mensalidade_.cursosReferentes), "%" + curso.getNome() + "%"));
        }
        if (cbTipoMensalidade.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Mensalidade_.tipoDaMensalidade), cbTipoMensalidade.getValue()));
        }
        if (cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Mensalidade_.formaPagamento), cbFormaPagamento.getValue()));
        }
        if (cbSituacao.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Mensalidade_.situacao), cbSituacao.getValue()));
            if (cbSituacao.getValue()) {
                query.orderBy(cb.asc(from.get(Mensalidade_.dataDePagamento)));
            } else {
                query.orderBy(cb.asc(from.get(Mensalidade_.dataVencimento)));
            }
        }
        if (!dpGeracaoInicial.getEditor().getText().isEmpty() && !dpGeracaoAte.getEditor().getText().isEmpty()) {
            LocalDate dataInicial = dpGeracaoInicial.getValue();
            LocalDate dataFinal = dpGeracaoAte.getValue();
            Predicate between = cb.between(from.get(Mensalidade_.dataDeGeracao), dataInicial, dataFinal);
            condicao = cb.and(condicao, between);
        }
        if (!dpVencimentoInicial.getEditor().getText().isEmpty() && !dpVencimentoAte.getEditor().getText().isEmpty()) {
            LocalDate dataInicial = dpVencimentoInicial.getValue();
            LocalDate dataFinal = dpVencimentoAte.getValue();
            Predicate between = cb.between(from.get(Mensalidade_.dataVencimento), dataInicial, dataFinal);
            condicao = cb.and(condicao, between);
        }
        if (!dpPagamentoInicial.getEditor().getText().isEmpty() && !dpPagamentoAte.getEditor().getText().isEmpty()) {
            LocalDate dataInicial = dpPagamentoInicial.getValue();
            LocalDate dataFinal = dpPagamentoAte.getValue();
            Predicate between = cb.between(from.get(Mensalidade_.dataDePagamento), dataInicial, dataFinal);
            condicao = cb.and(condicao, between);
        }
        Query<Mensalidade> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        if (ccVerTodosResultados.isSelected() && !txtLimite.getText().isEmpty()) {
            queryPronta.setFirstResult(0).setMaxResults(Integer.parseInt(txtLimite.getText()));
        }
        ObservableList<Mensalidade> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        if (lista.size() > 0) {
            GeraRelatorio geraRelatorio = new GeraRelatorio();
            JasperPrint jp = geraRelatorio.gerarRelatorioMensalidade(lista);
            Platform.runLater(() -> {
                btnGerar.setDisable(false);
                if (jp != null) {
                    JasperViewFX view = new JasperViewFX(jp, "Relatório de Mensalidades");
                    view.show(janelaRelatorio);
                } else {
                    DialogFX.showMessage("Houve algum erro ao tentar gerar o relatório!", "Erro", DialogType.ERRO);
                }

            });
        } else {
            Platform.runLater(() -> {
                btnGerar.setDisable(false);
                DialogFX.showMessage("Não foi retornado nenhum resultado de acordo com os filtros", "Nenhum encontrado", DialogType.INFORMATION);
            });
        }

    }

    private void carregar() {
        Thread t = new Thread(() -> {
            tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 30));
            cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaRelatorio = control;
    }

    @Override
    public void afterShow() {
        ccVerTodosResultados.setSelected(true);
        ccVerTodos.setSelected(true);
        this.carregar();
    }

    @FXML
    private void actionLimparCampos(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
    }

}
