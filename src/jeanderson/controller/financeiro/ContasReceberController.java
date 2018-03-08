/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.financeiro;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.annotations.ValidateField;
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
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
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
import jeanderson.model.Mensalidade;
import jeanderson.model.Mensalidade_;
import jeanderson.util.FuncoesUtil;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/financeiro/ContasReceber.fxml", title = "Contas a Receber")
public class ContasReceberController extends EasyFXFunctions implements Initializable{

    @FXML
    private TableView<Mensalidade> tbMensalidade;
    @FXML
    private TableColumn<Mensalidade, Integer> tcParcelaNumero;
    @FXML
    private TableColumn<Mensalidade, MensalidadeTipo> tcTipoMensalidade;
    @FXML
    private TableColumn<Mensalidade, PagamentoTipo> tcFormaPagamento;
    @FXML
    private TableColumn<Mensalidade, LocalDate> tcDataVencimento;
    @FXML
    private TableColumn<Mensalidade, Boolean> tcSituacao;
    @FXML
    private TableColumn<Mensalidade, LocalDate> tcSituacaoVencimento;
    @FXML
    private TableColumn<Mensalidade, Double> tcValor;
    @FXML
    private TableColumn<Mensalidade, String> tcAluno;
    @FXML
    @NotClear
    @ValidateField(fieldName = "Limite")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    private Separator spCima1;
    @FXML
    private Label lbFiltra;
    @FXML
    private Separator spCima2;
    @FXML
    private Separator spLado;
    @FXML
    private Label lbValor;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorInicial;
    @FXML
    private Label lbAte1;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorMaximo;
    @FXML
    private Label lbData;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimentoInicial;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimentoFinal;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtParcelaNumero;
    @FXML
    private JFXComboBox<MensalidadeTipo> cbTipo;
    @FXML
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimento;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    @FXML
    private JFXComboBox<Boolean> cbSituacaoVencimento;
    @FXML
    private JFXCheckBox ccFiltroExtra;
    private HelpLimit helpLimit;
    private ControlWindow<ContasReceberController> janelaContasReceber;
    private ControlWindow<InfoContasReceberController> janelaInfoContas;
    @FXML
    private JFXTextField txtAluno;
    @FXML
    private Label lbQuantidadeMensalidadeVencida;
    @FXML
    private Label lbQuantidadeMatriculaVencida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tcAluno.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getAluno().getNome()));
        this.tcParcelaNumero.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getNumeroDaParcela()));
        this.tcTipoMensalidade.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getTipoDaMensalidade()));
        this.tcFormaPagamento.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getFormaPagamento()));
        this.tcDataVencimento.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataVencimento()));
        this.tcSituacaoVencimento.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataVencimento()));
        this.tcSituacaoVencimento.setCellFactory(objeto -> FuncoesUtil.factoryColunaSituacaoVencimento());
        this.tcDataVencimento.setCellFactory(TextFieldTableCell.forTableColumn(Converter.DATAS.getStringConverter()));
        this.tcSituacao.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getSituacao()));
        this.tcSituacao.setCellFactory(cell -> FuncoesUtil.factoryColunaSituacaoConta());
        this.tcValor.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getValorParaPagar()));
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));


        this.spCima1.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.spCima2.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.spLado.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.lbFiltra.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.lbAte1.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.lbValor.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.lbData.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.dpVencimentoInicial.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.dpVencimentoFinal.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.txtValorInicial.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.txtValorMaximo.visibleProperty().bindBidirectional(this.ccFiltroExtra.selectedProperty());
        this.dpVencimento.disableProperty().bind(this.ccFiltroExtra.selectedProperty());

        this.cbTipo.setItems(FXCollections.observableArrayList(MensalidadeTipo.values()));
        this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbSituacao.setItems(FXCollections.observableArrayList(true, false));
        this.cbSituacaoVencimento.setItems(FXCollections.observableArrayList(true, false));
        this.cbSituacao.setConverter(Converter.SITUACAO_PAGAMENTO.getStringConverter());
        this.cbSituacaoVencimento.setConverter(Converter.SITUACAO_VENCIMENTO.getStringConverter());

        this.janelaInfoContas = new ControlWindow(InfoContasReceberController.class);
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        this.carregarTabela();
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
            if (tbMensalidade.getSelectionModel().getSelectedIndex() != -1) {
                Mensalidade mensalidade = tbMensalidade.getSelectionModel().getSelectedItem();
                this.janelaInfoContas.show(janelaContasReceber);
                janelaInfoContas.getController().carregarMensalidade(mensalidade);
            }
        }
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaContasReceber = control;
    }

    public void carregarTabela() {
        Thread t = new Thread(() -> {
            this.countMensalidadeEMatricula();
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (helpLimit.isMaiorQueAnterior()) {
                this.tbMensalidade.getItems().addAll(BancoDeDados.mensalidade().getMensalidades(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tbMensalidade.setItems(BancoDeDados.mensalidade().getMensalidades(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            }

        });
        t.setDaemon(true);
        t.start();
    }

    private void countMensalidadeEMatricula() {
        Long mensalidade = BancoDeDados.mensalidade().countMensalidadesVencidas();
        Long matricula = BancoDeDados.mensalidade().countMatriculasNaoPagas();
        Platform.runLater(() -> {
            lbQuantidadeMatriculaVencida.setText(matricula.toString());
            lbQuantidadeMensalidadeVencida.setText(mensalidade.toString());
        });
    }

    @Override
    public void afterShow() {
        this.txtLimite.setText("50");
    }

    private void filtrar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Mensalidade> query = builder.createQuery(Mensalidade.class);
        Root<Mensalidade> from = query.from(Mensalidade.class);
        Predicate condicao = builder.and();
        if (!this.txtAluno.getText().isEmpty()) {
            Root<Aluno> fromAluno = query.from(Aluno.class);
            condicao = builder.and(condicao, builder.like(fromAluno.get(Aluno_.nome), txtAluno.getText() + "%"), builder.equal(fromAluno.get(Aluno_.id), from.get(Mensalidade_.aluno)));
        }
        if (!this.txtParcelaNumero.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.numeroDaParcela), Integer.parseInt(txtParcelaNumero.getText())));
        }
        if (this.cbTipo.getSelectionModel().getSelectedIndex() != -1) {
            condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.tipoDaMensalidade), cbTipo.getValue()));
        }
        if (this.cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.formaPagamento), cbFormaPagamento.getValue()));
        }
        if (this.cbSituacao.getSelectionModel().getSelectedIndex() != -1) {
            condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.situacao), cbSituacao.getValue()));
        }
        if (this.cbSituacaoVencimento.getSelectionModel().getSelectedIndex() != -1) {
            boolean situacaoVencimento = cbSituacaoVencimento.getValue();
            LocalDate dataAtual = LocalDate.now();
            if (situacaoVencimento) {
                Predicate greaterThan = builder.greaterThanOrEqualTo(from.get(Mensalidade_.dataVencimento), dataAtual);
                condicao = builder.and(condicao, greaterThan);
            } else {
                Predicate lessThan = builder.lessThan(from.get(Mensalidade_.dataVencimento), dataAtual);
                condicao = builder.and(condicao, lessThan);
            }

        }
        if (ccFiltroExtra.isSelected()) {
            if (!this.txtValorInicial.getText().isEmpty() && !this.txtValorMaximo.getText().isEmpty()) {
                Double valorMin = FuncoesUtil.validaValor(txtValorInicial.getText());
                Double valorMax = FuncoesUtil.validaValor(txtValorMaximo.getText());
                Predicate condicaoDeValor = builder.between(from.get(Mensalidade_.valorParaPagar), valorMin, valorMax);
                condicao = builder.and(condicao, condicaoDeValor);
            }
            if (!this.dpVencimentoInicial.getEditor().getText().isEmpty() && !this.dpVencimentoFinal.getEditor().getText().isEmpty()) {
                LocalDate dataInicial = dpVencimentoInicial.getValue();
                LocalDate dataFinal = dpVencimentoFinal.getValue();
                Predicate condicaoDeData = builder.between(from.get(Mensalidade_.dataVencimento), dataInicial, dataFinal);
                condicao = builder.and(condicao, condicaoDeData);
            }
        } else {
            if (!this.dpVencimento.getEditor().getText().isEmpty()) {
                condicao = builder.and(condicao, builder.equal(from.get(Mensalidade_.dataVencimento), dpVencimento.getValue()));
            }
        }
        Query<Mensalidade> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Mensalidade> lista = FXCollections.observableArrayList(queryPronta.setMaxResults(Integer.parseInt(txtLimite.getText())).getResultList());
        this.tbMensalidade.setItems(lista);
    }
}
