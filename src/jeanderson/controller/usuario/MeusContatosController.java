/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.enums.SessaoLogin;
import jeanderson.enums.Situacao;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Contato;
import jeanderson.model.Contato_;
import jeanderson.model.Curso;
import jeanderson.util.HelpLimit;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/MeusContatos.fxml", title = "Meus Contatos")
public class MeusContatosController extends EasyFXFunctions implements Initializable {

    @FXML
    @ValidateField(fieldName = "Limite")
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;
    @FXML
    private JFXTextField txtNome;
    @FXML
    @MaskFormatter(type = MaskType.TEL_DIG, showMask = false)
    private JFXTextField txtTelefone;
    @FXML
    private JFXComboBox<Situacao> cbSituacao;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private TableView<Contato> tvContato;
    @FXML
    private TableColumn<Contato, String> tcNome;
    @FXML
    private TableColumn<Contato, String> tcTelefone;
    @FXML
    private TableColumn<Contato, Situacao> tcSituacao;
    @FXML
    private TableColumn<Contato, String> tcCurso;
    private HelpLimit helpLimit;
    private ControlWindow<InfoContatoController> janelaInfoContato;
    private ControlWindow<MeusContatosController> janelaMeusContatos;

    @FXML
    void actionAtualizar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            this.carregarTabela();
            FunctionAnnotations.clearFieldsWithAnnotations(this);
        }
    }

    @FXML
    void actionFiltrar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Thread t = new Thread(() -> {
                this.tvContato.setItems(this.montarQuery());
            });
            t.setDaemon(true);
            t.start();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.tcTelefone.setCellValueFactory(objeto -> objeto.getValue().telefoneProperty());
        this.tcSituacao.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getSituacao()));
        this.tcCurso.setCellValueFactory(objeto -> objeto.getValue().getCursoInteresse().nomeProperty());
        this.cbSituacao.setItems(FXCollections.observableArrayList(Situacao.values()));
       
        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());

        this.janelaInfoContato = new ControlWindow(InfoContatoController.class);
    }

    public void carregarTabela() {
        Thread t = new Thread(() -> {
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (helpLimit.isMaiorQueAnterior()) {
                this.tvContato.getItems().addAll(BancoDeDados.contato().queryPorFuncionario(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tvContato.setItems(BancoDeDados.contato().queryPorFuncionario(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void carregarCursos() {
        this.cbCurso.setItems(BancoDeDados.curso().queryAll());
    }

    private ObservableList<Contato> montarQuery() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Contato> query = builder.createQuery(Contato.class);
        Root<Contato> from = query.from(Contato.class);
        Predicate predicate = builder.equal(from.get(Contato_.funcionario), SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario());
        if (!txtNome.getText().trim().isEmpty()) {
            predicate = builder.and(predicate, builder.like(from.get(Contato_.nome), txtNome.getText().trim() + "%"));
        }
        if (!txtTelefone.getText().trim().isEmpty()) {
            predicate = builder.and(predicate, builder.like(from.get(Contato_.telefone), txtTelefone.getText().trim() + "%"));
        }
        if (cbSituacao.getSelectionModel().getSelectedIndex() != -1) {
            predicate = builder.and(predicate, builder.equal(from.get(Contato_.situacao), cbSituacao.getValue()));
        }
        if (cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            predicate = builder.and(predicate, builder.equal(from.get(Contato_.cursoInteresse), cbCurso.getValue()));
        }
        Query<Contato> queryPronta = sessao.createQuery(query.select(from).where(predicate));
        queryPronta.setFirstResult(0);
        queryPronta.setMaxResults(Integer.parseInt(txtLimite.getText()));
        ObservableList<Contato> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        return lista;
    }
    
    @Override
    public void afterShow() {
        this.txtLimite.setText("50");
    }

    @FXML
    private void keyPressDeletarContato(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            if (this.tvContato.getSelectionModel().getSelectedIndex() != -1) {
                Contato contato = tvContato.getSelectionModel().getSelectedItem();
                if (DialogFX.showConfirmation("Tem certeza que desejar apagar " + contato.getNome() + " ?", "Apagar contato")) {
                    ResultadoBD result = BancoDeDados.delete(contato);
                    if (result.resultado()) {
                        DialogFX.showMessage("Contato foi apagado com sucesso!", "Apagado com sucesso!", DialogType.SUCESS);
                        this.carregarTabela();
                    } else {
                        DialogFX.showMessage("Houve um erro ao tentar apagar contato! Motivo: " + result.mensagem(), "Erro ao apagar", DialogType.ERRO);
                    }
                }
            }
        }
    }

    @FXML
    private void mouseClickMostrarContato(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (this.tvContato.getSelectionModel().getSelectedIndex() != -1) {
                Contato contato = tvContato.getSelectionModel().getSelectedItem();
                this.janelaInfoContato.getController().passarContato(contato);
                this.janelaInfoContato.show(janelaMeusContatos);
            }
        }
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaMeusContatos = control;
    }
}
