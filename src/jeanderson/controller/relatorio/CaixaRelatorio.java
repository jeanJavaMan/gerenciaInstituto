/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.relatorio;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.jasper.JasperViewFX;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.CaixaTipo;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Caixa;
import jeanderson.model.Caixa_;
import jeanderson.model.Funcionario;
import jeanderson.model.GeraRelatorio;
import jeanderson.util.FuncoesUtil;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/relatorio/CaixaRelatorio.fxml")
public class CaixaRelatorio extends EasyFXFunctions implements Initializable {

    @FXML
    private JFXComboBox<CaixaTipo> cbTipo;
    @FXML
    @MaskFormatter(showMask = false,type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorMin;
    @FXML
    @MaskFormatter(showMask = false,type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorMax;
    @FXML
    @MaskFormatter(showMask = false,type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpDataInicial;
    @FXML
    @MaskFormatter(showMask = false,type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpDataFinal;
    @FXML
    private JFXComboBox<Funcionario> cbFuncionario;
    @FXML
    private JFXButton btnGerar;
    @FXML
    private JFXSpinner spGerando;
    @FXML
    private Text txtGerando;
    private ControlWindow<CaixaRelatorio> janela;
    @FXML
    @MaskFormatter(showMask = false,type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpDataGeracao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtGerando.visibleProperty().bind(spGerando.visibleProperty());
        spGerando.visibleProperty().bind(btnGerar.disableProperty());
        cbFuncionario.setConverter(Converter.FUNCIONARIO.getStringConverter());
        cbTipo.setItems(FXCollections.observableArrayList(CaixaTipo.values()));
    }

    @FXML
    private void actionGerarRelatorio(ActionEvent event) {
        btnGerar.setDisable(true);
        Thread t = new Thread(this::gerar);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janela = control;
    }

    private void gerar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Caixa> cq = cb.createQuery(Caixa.class);
        Root<Caixa> from = cq.from(Caixa.class);
        Predicate condicao = cb.and();
        if (cbTipo.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.equal(from.get(Caixa_.caixaTipo), cbTipo.getValue());
        }
        if (!txtValorMin.getText().isEmpty() || !txtValorMax.getText().isEmpty()) {
            if (!txtValorMin.getText().isEmpty() && txtValorMax.getText().isEmpty()) {
                Predicate minimo = cb.greaterThanOrEqualTo(from.get(Caixa_.valor), FuncoesUtil.validaValor(txtValorMin.getText()));
                condicao = cb.and(condicao, minimo);
            } else if (txtValorMin.getText().isEmpty() && !txtValorMax.getText().isEmpty()) {
                Predicate maximo = cb.lessThanOrEqualTo(from.get(Caixa_.valor), FuncoesUtil.validaValor(txtValorMax.getText()));
                condicao = cb.and(condicao, maximo);
            } else {
                Predicate entre = cb.between(from.get(Caixa_.valor), FuncoesUtil.validaValor(txtValorMin.getText()), FuncoesUtil.validaValor(txtValorMax.getText()));
                condicao = cb.and(condicao, entre);
            }
        }
        if (!dpDataGeracao.getEditor().getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Caixa_.dataGeracao), dpDataGeracao.getValue()));
        } else if (!dpDataInicial.getEditor().getText().isEmpty() && !dpDataFinal.getEditor().getText().isEmpty()) {
            LocalDate dataInicial = dpDataInicial.getValue();
            LocalDate dataFinal = dpDataFinal.getValue();
            Predicate between = cb.between(from.get(Caixa_.dataGeracao), dataInicial, dataFinal);
            condicao = cb.and(condicao, between);
        }
        if (cbFuncionario.getSelectionModel().getSelectedIndex() != -1) {
            Funcionario funcionario = cbFuncionario.getValue();
            condicao = cb.and(condicao, cb.equal(from.get(Caixa_.funcionarioNome), funcionario.getNome()));
        }
        Query<Caixa> query = sessao.createQuery(cq.select(from).where(condicao).orderBy(cb.asc(from.get(Caixa_.dataGeracao))));
        ObservableList<Caixa> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        if (lista.size() > 0) {
            GeraRelatorio gr = new GeraRelatorio();
            JasperPrint jp = gr.gerarRelatorioCaixa(lista);
            if (jp != null) {
                Platform.runLater(() -> {
                    btnGerar.setDisable(false);
                    JasperViewFX view = new JasperViewFX(jp, "Relatorio de Caixa");
                    view.show(janela);
                });
            } else {
                Platform.runLater(() -> {
                    btnGerar.setDisable(false);
                    DialogFX.showMessage("Houve um erro ao gerar o relatório!", "Erro ao Gerar relatorio", DialogType.ERRO);
                });
            }
        } else {
            Platform.runLater(() -> {
                btnGerar.setDisable(false);
                DialogFX.showMessage("Nenhum resultado encontrado com os filtros informado!", "Nenhum Resultado", DialogType.INFORMATION);
            });
        }

    }

    @Override
    public void afterShow() {
        this.carregar();
    }

    public void carregar() {
        Thread t = new Thread(() -> {
            cbFuncionario.setItems(BancoDeDados.funcionario().getFuncionarios());
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionLimparCampos(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
    }

}
