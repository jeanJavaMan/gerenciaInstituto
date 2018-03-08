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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import jeanderson.util.FuncoesUtil;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/financeiro/GerarContas.fxml", title = "Gerador de Mensalidades")
public class GerarContasController extends EasyFXFunctions implements Initializable{
    
    @FXML
    @ValidateField(fieldName = "Selecione o Aluno")
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
    @MaskFormatter(showMask = false, type = MaskType.CPF_DIG)
    private JFXTextField txtCPF;
    @FXML
    private JFXTextField txtRG;
    @FXML
    @ValidateField(fieldName = "Curso Referente")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXTextField txtValorCurso;
    @FXML
    @ValidateField(fieldName = "Tipo de Mensalidade")
    private JFXComboBox<MensalidadeTipo> cbTipoMensalidade;
    @FXML
    @ValidateField(fieldName = "Forma de Pagamento")
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    private JFXComboBox<Integer> cbParcelas;
    @FXML
    @ValidateField(fieldName = "Data do 1º Vencimento")
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimento;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtDesconto;
    @FXML
    @NotClear
    private JFXTextField txtValorParcelado;
    @FXML
    @NotClear
    private JFXTextField txtValorTotal;
    @FXML
    @NotClear
    private JFXTextField txtJuros;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbTipoMensalidade.setItems(FXCollections.observableArrayList(MensalidadeTipo.values()));
        this.cbParcelas.setItems(FuncoesUtil.getQuantidadeParcela());
        
        
        this.tvAluno.getSelectionModel().selectedItemProperty().addListener(this::actionSelecionaAluno);
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        
        this.tcAlunoMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcAluno.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
    }
    
    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(this::filtrar);
        t.setDaemon(true);
        t.start();
    }
    
    @FXML
    private void keyReleasedDesconto(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            this.txtValorTotal.setText(txtValorCurso.getText());
            Double valorTotal = FuncoesUtil.validaValor(txtValorTotal.getText());
            if (valorTotal > 0 && !txtDesconto.getText().isEmpty()) {
                int porcentagem = Integer.parseInt(this.txtDesconto.getText());
                Double valorComDesconto = valorTotal - ((valorTotal * porcentagem) / 100);
                this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorComDesconto));
                this.actionSelecionaParcela(null);
            } else {
                this.actionSelecionaParcela(null);
            }
        }
    }
    
    @FXML
    private void keyReleasedJuros(KeyEvent event) {
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1 && cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            if (this.txtJuros.getText().isEmpty()) {
                this.txtValorTotal.setText(txtValorCurso.getText());
                if (cbFormaPagamento.getValue() != PagamentoTipo.A_VISTA) {
                    this.actionSelecionaParcela(null);
                }
            } else {
                if (this.cbFormaPagamento.getValue() == PagamentoTipo.A_VISTA) {
                    Double valorTotal = FuncoesUtil.validaValor(txtValorCurso.getText());
                    Double juros = FuncoesUtil.validaValor(txtJuros.getText());
                    Double valorComJuros = FuncoesUtil.calculaJuros(valorTotal, juros);
                    this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorComJuros));
                } else {
                    this.actionSelecionaParcela(null);
                    Double valorParcelado = FuncoesUtil.validaValor(txtValorParcelado.getText());
                    Double juros = FuncoesUtil.validaValor(txtJuros.getText());
                    Double valorComJuros = FuncoesUtil.calculaJuros(valorParcelado, juros);
                    this.txtValorParcelado.setText(FuncoesUtil.toDecimalFormat(valorComJuros));
                    int quantidadeParcelas = this.cbParcelas.getValue();
                    Double valorTotal = (valorComJuros * quantidadeParcelas);
                    this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorTotal));
                }
            }
        } else {
            this.txtJuros.setText("0");
            DialogFX.showMessage("Selecione antes um Curso e a Forma de Pagamento", "Curso ou forma de pagamento não selecionados", DialogType.INFORMATION);
        }
    }
    
    @FXML
    private void actionGerarMensalidade(ActionEvent event) {
        Thread t = new Thread(() -> {
            Platform.runLater(this::gerarMensalidade);
        });
        t.setDaemon(true);
        t.start();
    }
    
    @FXML
    private void actionSelecionaCurso(ActionEvent event) {
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            this.txtValorCurso.setText(Double.toString(curso.getValor()));
            this.txtValorTotal.setText(txtValorCurso.getText());
        }
    }
    
    @FXML
    private void actionSelecionaFormaPagamento(ActionEvent event) {
        if (this.cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            this.txtValorTotal.setText(txtValorCurso.getText());
            PagamentoTipo pagTipo = cbFormaPagamento.getValue();
            if (pagTipo == PagamentoTipo.A_VISTA) {
                this.cbParcelas.setDisable(true);
                this.cbParcelas.getSelectionModel().select(0);
                this.txtValorParcelado.setText("0");
                this.txtValorParcelado.setDisable(true);
            } else {
                this.cbParcelas.setDisable(false);
                this.txtValorParcelado.setDisable(false);
            }
        }
    }
    
    @FXML
    private void actionSelecionaParcela(ActionEvent event) {
        if (this.cbParcelas.getSelectionModel().getSelectedIndex() != -1) {
            Integer quantidadeParcela = cbParcelas.getValue();
            Double valorTotal = FuncoesUtil.validaValor(txtValorTotal.getText());
            Double valorDividido = (valorTotal / quantidadeParcela);
            this.txtValorParcelado.setText(FuncoesUtil.toDecimalFormat(valorDividido));
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
    
    public void carregarDados() {
        Thread t = new Thread(() -> {
            this.tvAluno.setItems(BancoDeDados.aluno().getAlunos(0, 50));
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }
    
    private void gerarMensalidade() {
        if (FunctionAnnotations.validateFields(this)) {
            LocalDate dataDeGeracao = LocalDate.now();
            LocalDate dataDeVencimento = dpVencimento.getValue();
            Aluno aluno = tvAluno.getSelectionModel().getSelectedItem();
            List<Mensalidade> mensalidades = new ArrayList<>();
            Mensalidade mensalidade;
            int quantidadeParcelas = cbParcelas.getValue();
            if (quantidadeParcelas <= 1) {
                mensalidade = new Mensalidade();
                mensalidade.setAluno(aluno);
                mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
                mensalidade.setTipoDaMensalidade(cbTipoMensalidade.getValue());
                mensalidade.setSituacao(false);
                mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtValorTotal.getText()));
                mensalidade.setNumeroDaParcela(1);
                mensalidade.setDataDeGeracao(dataDeGeracao);
                mensalidade.setDataVencimento(dataDeVencimento);
                mensalidade.addCursoReferente(cbCurso.getValue());
                mensalidades.add(mensalidade);
            } else {
                for (int i = 0; i < quantidadeParcelas; i++) {
                    mensalidade = new Mensalidade();
                    mensalidade.setAluno(aluno);
                    mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
                    mensalidade.setTipoDaMensalidade(cbTipoMensalidade.getValue());
                    mensalidade.setSituacao(false);
                    mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtValorParcelado.getText()));
                    mensalidade.setNumeroDaParcela((i + 1));
                    mensalidade.setDataDeGeracao(dataDeGeracao);
                    mensalidade.setDataVencimento(dataDeVencimento.plusMonths(i));
                    mensalidade.addCursoReferente(cbCurso.getValue());
                    mensalidades.add(mensalidade);
                }
            }
            boolean resultado = true;
            for (Mensalidade mensal : mensalidades) {
                ResultadoBD result = BancoDeDados.save(mensal);
                if (!result.resultado()) {
                    DialogFX.showMessage("Houve um erro ao gerar a mensalidade! Motivo: " + result.mensagem(), "Erro ao gerar mensalidade", DialogType.ERRO);
                    resultado = false;
                    break;
                }
            }
            if (resultado) {
                FunctionAnnotations.clearFieldsWithAnnotations(this);
                this.afterShow();
                DialogFX.showMessage("Mensalidades Geradas com sucesso!", "Geradas com Sucesso!", DialogType.SUCESS);
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
    
    @Override
    public void afterShow() {
        this.txtValorTotal.setText("0");
        this.txtValorParcelado.setText("0");
        this.txtJuros.setText("0");
        this.txtDesconto.setText("0");
        this.cbParcelas.setDisable(true);
        this.cbParcelas.getSelectionModel().select(0);
    }
    
}
