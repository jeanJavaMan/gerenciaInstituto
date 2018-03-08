/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.controller.HomeController;
import jeanderson.enums.Converter;
import jeanderson.enums.SessaoLogin;
import jeanderson.enums.Situacao;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Contato;
import jeanderson.model.Contato_;
import jeanderson.model.Lembrete;
import jeanderson.model.Lembrete_;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/MeusLembretes.fxml", title = "Meus Lembretes")
public class MeusLembretesController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Lembrete> tvLembrete;
    @FXML
    private TableColumn<Lembrete, LocalDate> tcLembraDia;
    @FXML
    private TableColumn<Lembrete, String> tcContato;
    @FXML
    private TableColumn<Lembrete, Situacao> tcSituacao;
    @FXML
    private JFXDatePicker dpLembrar;
    @FXML
    private JFXTextField txtContato;
    @FXML
    private JFXComboBox<Situacao> cbSituacao;
    private ControlWindow<HomeController> janelaHome;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbSituacao.setItems(FXCollections.observableArrayList(Situacao.values()));
        tcLembraDia.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getDataLembrar()));
        tcLembraDia.setCellFactory(cell -> new TextFieldTableCell<>(Converter.DATAS.getStringConverter()));
        tcContato.setCellValueFactory(objeto -> carregarNome(objeto));
        tcSituacao.setCellValueFactory(objeto -> carregarLembrete(objeto));
    }

    /**
     * Método criado para resolver o problema quando não houver um contato ao
     * lembrete.
     */
    private StringProperty carregarNome(TableColumn.CellDataFeatures<Lembrete, String> cell) {
        if (cell.getValue().getContato() != null) {
            return cell.getValue().getContato().nomeProperty();
        } else if (cell.getValue().getAluno() != null) {
            return new SimpleStringProperty("Observação referente ao aluno: " + cell.getValue().getAluno().getNome());
        } else {
            return new SimpleStringProperty("Lembrete Pessoal: " + cell.getValue().getObservacoes());
        }
    }

    /**
     * Mesma função do método carregarNome.
     *
     * @return
     */
    private SimpleObjectProperty carregarLembrete(TableColumn.CellDataFeatures<Lembrete, Situacao> cell) {
        if (cell.getValue().getContato() != null) {
            return new SimpleObjectProperty(cell.getValue().getContato().getSituacao());
        } else if (cell.getValue().getAluno() != null) {
            return new SimpleObjectProperty("Observações de Aluno");
        } else {
            return new SimpleObjectProperty("Lembrete Pessoal");
        }
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

    public void pegarHome(ControlWindow<HomeController> janelaHome) {
        this.janelaHome = janelaHome;
    }

    @FXML
    private void actionExcluirTodos(ActionEvent event) {
        if (!tvLembrete.getItems().isEmpty()) {
            if (DialogFX.showConfirmation("Tem certeza que deseja apagar todos os seus lembretes?", "Apagar todos os lembretes")) {
                ResultadoBD result = BancoDeDados.lembrete().apagarTodos();
                if (result.resultado()) {
                    DialogFX.showMessage("Todos os lembretes foram apagados com sucesso!", "Lembretes apagados", DialogType.SUCESS);
                    tvLembrete.getItems().clear();
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar apagar todos os lembretes! Motivo: " + result.mensagem(), "Erro ao apagar", DialogType.ERRO);
                }
            }
        } else {
            DialogFX.showMessage("Não há nenhum lembrete para apagar", "Sem lembretes", DialogType.WARNING);
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (tvLembrete.getSelectionModel().getSelectedIndex() != -1) {
            if (DialogFX.showConfirmation("Deseja realmente apagar este lembrete?", "Apagar Lembrete?")) {
                Lembrete lembrete = tvLembrete.getSelectionModel().getSelectedItem();
                ResultadoBD result = BancoDeDados.delete(lembrete);
                if (result.resultado()) {
                    DialogFX.showMessage("Lembrete apagado com sucesso!", "Lembrete apagado!", DialogType.SUCESS);
                    tvLembrete.getItems().remove(lembrete);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar apagar lembrete! Motivo: " + result.mensagem(), "Erro ao apagar lembrete", DialogType.ERRO);
                }
            }
        }
    }

    public void carregarTabela() {
        Thread t = new Thread(() -> {
            this.tvLembrete.setItems(BancoDeDados.lembrete().pegarTodos(SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario()));
        });
        t.setDaemon(true);
        t.start();
    }

    private void filtrar() {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder builder = sessao.getCriteriaBuilder();
        CriteriaQuery<Lembrete> query = builder.createQuery(Lembrete.class);
        Root<Lembrete> from = query.from(Lembrete.class);
        Join<Lembrete, Contato> join = from.join(Lembrete_.contato);
        Predicate condicao = builder.and();
        if (!txtContato.getText().isEmpty()) {
            condicao = builder.and(condicao, builder.like(join.get(Contato_.nome), txtContato.getText() + "%"));
        }
        if (!dpLembrar.getEditor().getText().isEmpty()) {
            condicao = builder.and(condicao, builder.equal(from.get(Lembrete_.dataLembrar), dpLembrar.getValue()));
        }
        if (cbSituacao.getSelectionModel().getSelectedIndex() != -1) {
            condicao = builder.and(condicao, builder.equal(join.get(Contato_.situacao), cbSituacao.getValue()));
        }
        Query<Lembrete> queryPronta = sessao.createQuery(query.select(from).where(condicao));
        ObservableList<Lembrete> lista = FXCollections.observableArrayList(queryPronta.getResultList());
        sessao.close();
        tvLembrete.setItems(lista);
    }

    @FXML
    private void mouseClickTabela(MouseEvent event) {
        if (event.getClickCount() == 2 && tvLembrete.getSelectionModel().getSelectedIndex() != -1) {
            Lembrete lembrete = tvLembrete.getSelectionModel().getSelectedItem();
            this.janelaHome.getController().mostrarJanelaInfoLembrete(lembrete);
        }
    }
}
