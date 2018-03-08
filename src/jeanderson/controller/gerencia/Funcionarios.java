/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
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
import jeanderson.enums.Converter;
import jeanderson.enums.FuncionarioTipo;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Funcionario;
import jeanderson.model.Funcionario_;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.HelpLimit;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Funcionarios.fxml", title = "Funcionários Cadastrados")
public class Funcionarios extends EasyFXFunctions implements Initializable{

    @FXML
    private TableView<Funcionario> tvFuncionario;
    @FXML
    private TableColumn<Funcionario, String> tcNome;
    @FXML
    private TableColumn<Funcionario, FuncionarioTipo> tcFuncao;
    @FXML
    private TableColumn<Funcionario, String> tcTelefone;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    private JFXComboBox<FuncionarioTipo> cbFuncao;
    private ControlWindow<InfoFuncionario> janelaInfo;
    private ControlWindow<Funcionarios> janelaFuncionarios;
    @FXML
    private TableColumn<Funcionario, Boolean> tcSituacao;
    @FXML
    private JFXComboBox<Boolean> cbSituacao;
    private HelpLimit helpLimit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.janelaInfo = new ControlWindow(InfoFuncionario.class);
        cbFuncao.setItems(FXCollections.observableArrayList(FuncionarioTipo.values()));
        tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        tcFuncao.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getFuncao()));
        tcTelefone.setCellValueFactory(objeto -> objeto.getValue().telefoneProperty());
        tcSituacao.setCellValueFactory(objeto -> objeto.getValue().ativoProperty());
        tcSituacao.setCellFactory(cell -> FuncoesUtil.factoryColunaSituacaoFuncionario());
        cbSituacao.setItems(FXCollections.observableArrayList(true, false));
        cbSituacao.setConverter(Converter.FUNCIONARIO_SITUACAO.getStringConverter());
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));
        
    }

    @FXML
    private void mouseClickTabela(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvFuncionario.getSelectionModel().getSelectedIndex() != -1) {
                Funcionario funcionario = tvFuncionario.getSelectionModel().getSelectedItem();
                janelaInfo.show(janelaFuncionarios);
                janelaInfo.getController().carregarFuncionario(funcionario);
            }
        }
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        Thread t = new Thread(() -> {
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (this.helpLimit.isMaiorQueAnterior()) {
                this.tvFuncionario.getItems().addAll(BancoDeDados.funcionario().getFuncionarios(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tvFuncionario.setItems(BancoDeDados.funcionario().getFuncionarios(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            }
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

    private void filtrar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Funcionario> cq = cb.createQuery(Funcionario.class);
        Root<Funcionario> from = cq.from(Funcionario.class);
        Predicate condicao = cb.and();
        if(!txtNome.getText().isEmpty()){
            condicao = cb.and(condicao, cb.like(from.get(Funcionario_.nome), txtNome.getText()+"%"));
        }
        if(!txtTelefone.getText().isEmpty()){
            condicao = cb.and(condicao, cb.like(from.get(Funcionario_.telefone), txtTelefone.getText()+"%"));
        }
        if(cbFuncao.getSelectionModel().getSelectedIndex() != -1){
            condicao = cb.and(condicao, cb.equal(from.get(Funcionario_.funcao), cbFuncao.getValue()));
        }
        if(cbSituacao.getSelectionModel().getSelectedIndex() != -1){
            condicao = cb.and(condicao, cb.equal(from.get(Funcionario_.ativo), cbSituacao.getValue()));
        }
        Query<Funcionario> query = sessao.createQuery(cq.select(from).where(condicao));
        query.setMaxResults(helpLimit.getValorMaximo());
        ObservableList<Funcionario> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        tvFuncionario.setItems(lista);
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaFuncionarios = control;
    }

    public void carregarDados() {
        Thread t = new Thread(this::carregaFuncionarios);
        t.setDaemon(true);
        t.start();
    }

    private void carregaFuncionarios() {
        this.tvFuncionario.setItems(BancoDeDados.funcionario().getFuncionarios(0, 50));
    }
}
