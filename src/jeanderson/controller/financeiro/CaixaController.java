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
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.CaixaTipo;
import jeanderson.enums.Converter;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Caixa;
import jeanderson.model.Caixa_;
import jeanderson.model.Funcionario;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/financeiro/Caixa.fxml",title = "Meu Caixa")
public class CaixaController extends EasyFXFunctions implements Initializable{

    @FXML
    @ValidateField(fieldName = "Descrição")
    private JFXTextField txtDescricao;
    @FXML
    @ValidateField(fieldName = "Tipo")
    private JFXComboBox<CaixaTipo> cbTipo;
    @FXML
    @ValidateField(fieldName = "Valor")
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValor;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    @ValidateField(fieldName = "Data de geração")
    @NotClear
    private JFXDatePicker dpDataGeracao;
    @FXML
    private TableView<Caixa> tvCaixa;
    @FXML
    private TableColumn<Caixa, Integer> tcCodigo;
    @FXML
    private TableColumn<Caixa, String> tcDescricao;
    @FXML
    private TableColumn<Caixa, LocalDate> tcData;
    @FXML
    private TableColumn<Caixa, Double> tcValor;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCodigoFiltro;
    @FXML
    private JFXTextField txtDescricaoFiltro;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpDataFiltro;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtValorFiltro;
    @FXML
    private JFXCheckBox ccTodaAsDatas;
    @FXML
    private JFXSpinner spcarregando;
    @FXML
    private Text txtCarregando;
    @FXML
    private TableColumn<Caixa, CaixaTipo> tcTipo;
    @FXML
    private JFXComboBox<CaixaTipo> cbTipoFiltro;
    @FXML
    private JFXDatePicker dpDataInicial;
    @FXML
    private JFXDatePicker dpDataFinal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcCodigo.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        tcDescricao.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDescricao()));
        tcData.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataGeracao()));
        tcData.setCellFactory(TextFieldTableCell.forTableColumn(Converter.DATAS.getStringConverter()));
        tcValor.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getValor()));
        tcTipo.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getCaixaTipo()));

        tvCaixa.setEditable(true);
        tcDescricao.setCellFactory(TextFieldTableCell.forTableColumn());
        tcTipo.setCellFactory(ComboBoxTableCell.forTableColumn(CaixaTipo.values()));
        tcValor.setCellFactory(TextFieldTableCell.forTableColumn(Converter.VALORES_DECIMAL.getStringConverter()));

        cbTipo.setItems(FXCollections.observableArrayList(CaixaTipo.values()));
        cbTipoFiltro.setItems(FXCollections.observableArrayList(CaixaTipo.values()));
        dpDataFiltro.setValue(LocalDate.now());
        dpDataGeracao.setValue(LocalDate.now());
        txtCarregando.visibleProperty().bindBidirectional(spcarregando.visibleProperty());
        spcarregando.setVisible(false);
        dpDataFiltro.disableProperty().bindBidirectional(ccTodaAsDatas.selectedProperty());
        dpDataInicial.disableProperty().bindBidirectional(ccTodaAsDatas.selectedProperty());
        dpDataFinal.disableProperty().bindBidirectional(ccTodaAsDatas.selectedProperty());
    }

    @FXML
    private void actionInserir(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Funcionario funcionarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario();
            Caixa caixa = new Caixa();
            caixa.setDescricao(txtDescricao.getText());
            caixa.setCaixaTipo(cbTipo.getValue());
            caixa.setDataGeracao(dpDataGeracao.getValue());
            caixa.setValor(FuncoesUtil.validaValor(txtValor.getText()));
            caixa.setFuncionarioNome(funcionarioLogado.getNome());
            ResultadoBD result = BancoDeDados.save(caixa);
            if (result.resultado()) {
                DialogFX.showMessage("Inserido com sucesso!", "Sucesso", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
                tvCaixa.getItems().add(caixa);
            } else {
                DialogFX.showMessage("Houve um erro ao tentar inserir! Motivo: " + result.mensagem(), "Erro ao tentar inserir", DialogType.ERRO);
            }
        }
    }

    @FXML
    private void actionFiltrar(ActionEvent event) {
        Thread t = new Thread(() -> {
            spcarregando.setVisible(true);
            this.filtrar();
            cbTipoFiltro.getSelectionModel().select(-1);
            spcarregando.setVisible(false);
        });
        t.setDaemon(true);
        t.start();
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Caixa> cq = cb.createQuery(Caixa.class);
        Root<Caixa> from = cq.from(Caixa.class);
        Predicate condicao = cb.and();
        if (!txtCodigoFiltro.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Caixa_.id), Integer.parseInt(txtCodigoFiltro.getText())));
        }
        if (!txtDescricaoFiltro.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Caixa_.descricao), txtDescricaoFiltro.getText() + "%"));
        }
        if (!ccTodaAsDatas.isSelected()) {
            if (!dpDataInicial.getEditor().getText().isEmpty() && !dpDataFinal.getEditor().getText().isEmpty()) {
                LocalDate dataInicial = dpDataInicial.getValue();
                LocalDate dataFinal = dpDataFinal.getValue();
                Predicate between = cb.between(from.get(Caixa_.dataGeracao), dataInicial, dataFinal);
                condicao = cb.and(condicao, between);
            } else {
                if (!dpDataFiltro.getEditor().getText().isEmpty()) {
                    condicao = cb.and(condicao, cb.equal(from.get(Caixa_.dataGeracao), dpDataFiltro.getValue()));
                }
            }
        }
        if (!txtValorFiltro.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Caixa_.valor), FuncoesUtil.validaValor(txtValorFiltro.getText())));
        }
        if (cbTipoFiltro.getSelectionModel().getSelectedIndex() != -1) {
            condicao = cb.and(condicao, cb.equal(from.get(Caixa_.caixaTipo), cbTipoFiltro.getValue()));
        }
        Query<Caixa> query = sessao.createQuery(cq.select(from).where(condicao));
        ObservableList<Caixa> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        tvCaixa.setItems(lista);
    }

    @FXML
    private void actionApagar(ActionEvent event) {
        if (tvCaixa.getSelectionModel().getSelectedIndex() != -1) {
            if (DialogFX.showConfirmation("Tem certeza que deseja excluir este produto?", "Excluir")) {
                Caixa caixa = tvCaixa.getSelectionModel().getSelectedItem();
                tvCaixa.getItems().remove(caixa);
                ResultadoBD result = BancoDeDados.delete(caixa);
                if (result.resultado()) {
                    DialogFX.showMessage("Excluído com sucesso!", "Excluído com sucesso", DialogType.SUCESS);
                } else {
                    tvCaixa.getItems().add(caixa);
                    DialogFX.showMessage("Houve um erro ao tentar excluir o produto! Motivo: " + result.mensagem(), "Erro ao excluir", DialogType.ERRO);
                }
            }
        } else {
            DialogFX.showMessage("Nenhum produto selecionado!", "Selecione Antes", DialogType.INFORMATION);
        }
    }

    public void carregarCaixa() {
        Thread t = new Thread(() -> {
            tvCaixa.setItems(BancoDeDados.caixa().getCaixaPorData(dpDataFiltro.getValue()));
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void editCommitDescricao(TableColumn.CellEditEvent<Caixa, String> event) {
        if (verificarAlteracao()) {
            Caixa caixa = tvCaixa.getSelectionModel().getSelectedItem();
            caixa.setDescricao(event.getNewValue());
            this.salvarAlteracoes(caixa);
        } else {
            tcValor.getTableView().getColumns().get(0).setVisible(false);
            tcValor.getTableView().getColumns().get(0).setVisible(true);
        }
    }

    @FXML
    private void editCommitData(TableColumn.CellEditEvent<Caixa, LocalDate> event) {
        if (verificarAlteracao()) {
            Caixa caixa = tvCaixa.getSelectionModel().getSelectedItem();
            caixa.setDataGeracao(event.getNewValue());
            this.salvarAlteracoes(caixa);
        } else {
            tcValor.getTableView().getColumns().get(0).setVisible(false);
            tcValor.getTableView().getColumns().get(0).setVisible(true);
        }
    }

    @FXML
    private void editCommitTipo(TableColumn.CellEditEvent<Caixa, CaixaTipo> event) {
        if (verificarAlteracao()) {
            Caixa caixa = tvCaixa.getSelectionModel().getSelectedItem();
            caixa.setCaixaTipo(event.getNewValue());
            this.salvarAlteracoes(caixa);
        } else {
            tcValor.getTableView().getColumns().get(0).setVisible(false);
            tcValor.getTableView().getColumns().get(0).setVisible(true);
        }
    }

    @FXML
    private void editCommitValor(TableColumn.CellEditEvent<Caixa, Double> event) {
        if (verificarAlteracao()) {
            Caixa caixa = tvCaixa.getSelectionModel().getSelectedItem();
            caixa.setValor(event.getNewValue());
            this.salvarAlteracoes(caixa);
        } else {
            tcValor.getTableView().getColumns().get(0).setVisible(false);
            tcValor.getTableView().getColumns().get(0).setVisible(true);
        }
    }

    private boolean verificarAlteracao() {
        return DialogFX.showConfirmation("Deseja salvar está alteração?", "Salvar alteração");
    }

    private void salvarAlteracoes(Caixa caixa) {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                ResultadoBD result = BancoDeDados.update(caixa);
                if (!result.resultado()) {
                    DialogFX.showMessage("Houve um erro ao tentar alterar! Motivo: " + result.mensagem(), "Erro ao tentar alterar", DialogType.ERRO);
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }
}
