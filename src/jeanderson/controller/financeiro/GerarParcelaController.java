/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.financeiro;

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
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
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
import jeanderson.enums.MensalidadeTipo;
import jeanderson.enums.PagamentoTipo;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Mensalidade;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/financeiro/GerarParcela.fxml", title = "Gerar Parcela")
@ClearFields
public class GerarParcelaController extends EasyFXFunctions implements Initializable {
    
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
    @ValidateField(fieldName = "Curso referente")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXTextField txtValorCurso;
    @FXML
    @ValidateField(fieldName = "Tipo da Mensalidade")
    private JFXComboBox<MensalidadeTipo> cbTipoMensalidade;
    @FXML
    @ValidateField(fieldName = "Forma de Pagamento")
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    @ValidateField(fieldName = "Data de Geração")
    private JFXDatePicker dpGeracao;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpPagamento;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtParcelaNumero;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    @ValidateField(fieldName = "Valor da Parcela")
    private JFXTextField txtValorFinal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbTipoMensalidade.setItems(FXCollections.observableArrayList(MensalidadeTipo.values()));
        this.tvAluno.getSelectionModel().selectedItemProperty().addListener(this::actionSelecionaAluno);
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        
        this.tcAlunoMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcAluno.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.cbSituacao.setItems(FXCollections.observableArrayList(true, false));
        this.cbSituacao.setConverter(Converter.SITUACAO_PAGAMENTO.getStringConverter());
    }
    
    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(this::filtrar);
        t.setDaemon(true);
        t.start();
    }
    
    @FXML
    private void actionSelecionaCurso(ActionEvent event) {
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            this.txtValorCurso.setText(Double.toString(curso.getValor()));
        }
    }
    
    @FXML
    private void actionGerarParcela(ActionEvent event) {
        if (tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            if (FunctionAnnotations.validateFields(this)) {
                Mensalidade mensalidade = new Mensalidade();
                mensalidade.setAluno(tvAluno.getSelectionModel().getSelectedItem());
                mensalidade.setDataDeGeracao(dpGeracao.getValue());
                mensalidade.setDataDePagamento(dpPagamento.getValue());
                mensalidade.setDataVencimento(dpVencimento.getValue());
                mensalidade.setSituacao(cbSituacao.getValue());
                mensalidade.setValorParaPagar(Double.parseDouble(txtValorFinal.getText()));
                mensalidade.setTipoDaMensalidade(cbTipoMensalidade.getValue());
                mensalidade.setNumeroDaParcela(Integer.parseInt(txtParcelaNumero.getText()));
                mensalidade.addCursoReferente(cbCurso.getValue());
                mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
                ResultadoBD result = BancoDeDados.save(mensalidade);
                if (result.resultado()) {
                    DialogFX.showMessage("Parcela gerada com sucesso!", "Sucesso", DialogType.SUCESS);
                    FunctionAnnotations.clearFieldsWithAnnotations(this);
                } else {
                    DialogFX.showMessage("Houve um erro ao gerar a parcela! Motivo: " + result.mensagem(), "Erro ao Gerar parcela", DialogType.ERRO);
                }
            }            
        } else {
            DialogFX.showMessage("Selecione um aluno antes!", "Nenhum aluno selecionado", DialogType.WARNING);
        }
    }
    
    private void actionSelecionaAluno(Observable valor) {
        if (this.tvAluno.getSelectionModel().getSelectedIndex() != -1) {
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            this.txtAlunoSelecionado.setText(aluno.getNome());
            this.txtCPF.setText(aluno.getCpf());
            this.txtRG.setText(aluno.getRG());
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
    
    public void carregarDados() {
        Thread t = new Thread(() -> {
            this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 50));
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }
    
    @Override
    public void afterShow() {
        this.txtParcelaNumero.setText("1");
        this.dpGeracao.setValue(LocalDate.now());
        this.cbSituacao.setValue(false);
        this.carregarDados();
    }
}
