/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.financeiro;

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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.Converter;
import jeanderson.enums.PagamentoTipo;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Mensalidade;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/financeiro/InfoContasReceber.fxml",title = "Informações do Devedor")
public class InfoContasReceberController extends EasyFXFunctions implements Initializable {

    @FXML
    private JFXTextField txtMatricula;
    @FXML
    private JFXTextField txtNome;
    @FXML
    private JFXTextField txtCPF;
    @FXML
    private JFXTextField txtRG;
    @FXML
    @ValidateField(fieldName = "Data de Geração")
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpGeracao;
    @FXML
    @ValidateField(fieldName = "Data de vencimento")
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    @ValidateField(fieldName = "Parcela Número")
    private JFXTextField txtParcelaNumero;
    @FXML
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    @FXML
    @ValidateField(fieldName = "Valor da Mensalidade")
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorMensalidade;
    @FXML
    private JFXListView<String> lvCurso;
    @FXML
    private JFXButton btnEditar;
    private Mensalidade mensalidade;
    private BooleanProperty editar;
    @FXML
    private JFXButton btnPagar;
    private ControlWindow<InfoContasReceberController> janelaInfoContas;
    @FXML
    private JFXDatePicker dpPagamento;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbSituacao.setItems(FXCollections.observableArrayList(true, false));
        this.cbSituacao.setConverter(Converter.SITUACAO_PAGAMENTO.getStringConverter());
        this.editar = new SimpleBooleanProperty(false);
        this.dpGeracao.editableProperty().bindBidirectional(this.editar);
        this.dpVencimento.editableProperty().bindBidirectional(this.editar);
        this.dpPagamento.editableProperty().bindBidirectional(this.editar);
        this.txtParcelaNumero.editableProperty().bindBidirectional(this.editar);
        this.txtValorMensalidade.editableProperty().bindBidirectional(this.editar);
        
        
    }    

    @FXML
    private void actionLancarPagamento(ActionEvent event) {
        if (!this.mensalidade.getSituacao()) {
            if (DialogFX.showConfirmation("Confirma pagamento desta mensalidade?", "Confirma Pagamento")) {
                this.mensalidade.setSituacao(Boolean.TRUE);
                LocalDate dataDePagamento = LocalDate.now();
                this.mensalidade.setDataDePagamento(dataDePagamento);
                ResultadoBD result = BancoDeDados.update(mensalidade);
                if (result.resultado()) {
                    DialogFX.showMessage("Conta paga com sucesso! Atualize a tabela.", "Conta Paga", DialogType.SUCESS);
                    this.cbSituacao.getSelectionModel().select(Boolean.TRUE);
                    this.dpPagamento.setValue(dataDePagamento);
                    this.btnPagar.setDisable(true);
                } else {
                    DialogFX.showMessage("Houve um erro ao pagar a conta! Motivo: " + result.mensagem(), "Erro ao pagar", DialogType.ERRO);
                }
            }
        }
    }

    @FXML
    private void actionEditarInformacoes(ActionEvent event) {
        if (!this.editar.get()) {
            this.editar.set(true);
            this.btnEditar.setText("Salvar Alterações");
        } else {
            if (FunctionAnnotations.validateFields(this)) {
                this.mensalidade.setDataDeGeracao(dpGeracao.getValue());
                this.mensalidade.setDataVencimento(dpVencimento.getValue());
                this.mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
                this.mensalidade.setSituacao(cbSituacao.getValue());
                this.mensalidade.setNumeroDaParcela(Integer.parseInt(txtParcelaNumero.getText()));
                this.mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtValorMensalidade.getText()));
                this.mensalidade.setDataDePagamento(dpPagamento.getValue());
                ResultadoBD result = BancoDeDados.update(mensalidade);
                if (result.resultado()) {
                    DialogFX.showMessage("Informações Editada com sucesso! Atualize a Tabela.", "Editada com Sucesso!", DialogType.SUCESS);
                    this.editar.set(false);
                    this.btnEditar.setText("Editar Informações");
                } else {
                    DialogFX.showMessage("Houve um erro ao editar! Motivo: " + result.mensagem(), "Erro ao editar", DialogType.ERRO);
                }
            }
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (DialogFX.showConfirmation("Tem certeza que Deseja apagar está mensalidade?", "Apagar Mensalidade")) {
            ResultadoBD result = BancoDeDados.delete(mensalidade);
            if (result.resultado()) {
                DialogFX.showMessageAndWait("Mensalidade excluida com sucesso! Atualize a Tabela.", "Mensalidade Excluida", DialogType.SUCESS);
                this.janelaInfoContas.close();
            } else {
                DialogFX.showMessage("Houve um erro ao tentar excluir a mensalidade! Motivo: " + result.mensagem(), "Erro ao tentar excluir", DialogType.ERRO);
            }
        }
    }
    
    public void carregarMensalidade(Mensalidade mensalidade) {
        this.mensalidade = mensalidade;
        this.txtMatricula.setText(mensalidade.getAluno().getId().toString());
        this.txtNome.setText(mensalidade.getAluno().getNome());
        this.txtCPF.setText(mensalidade.getAluno().getCpf());
        this.txtRG.setText(mensalidade.getAluno().getRG());
        this.txtParcelaNumero.setText(mensalidade.getNumeroDaParcela().toString());
        this.dpGeracao.setValue(mensalidade.getDataDeGeracao());
        this.dpVencimento.setValue(mensalidade.getDataVencimento());
        this.cbFormaPagamento.getSelectionModel().select(mensalidade.getFormaPagamento());
        this.cbSituacao.getSelectionModel().select(mensalidade.getSituacao());
        this.lvCurso.setItems(FXCollections.observableArrayList(mensalidade.getCursosReferenteFormatado()));
        this.txtValorMensalidade.setText(FuncoesUtil.toDecimalFormat(mensalidade.getValorParaPagar()));
        this.dpPagamento.setValue(mensalidade.getDataDePagamento());
        this.btnPagar.setDisable(mensalidade.getSituacao());              
    }
    
    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfoContas = control;
    }    
    
    @Override
    public void afterShow() {
        this.editar.set(false);
    }
}
