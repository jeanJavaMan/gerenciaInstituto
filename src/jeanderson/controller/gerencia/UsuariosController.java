/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
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
import jeanderson.enums.FuncionarioTipo;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Funcionario;
import jeanderson.model.Funcionario_;
import jeanderson.model.Usuario;
import jeanderson.model.Usuario_;
import jeanderson.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Usuarios.fxml", title = "Usuários Cadastrados")
public class UsuariosController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Usuario> tvUsuario;
    @FXML
    private TableColumn<Usuario, String> tcFuncionario;
    @FXML
    private TableColumn<Usuario, String> tcUsuario;
    @FXML
    private JFXTextField txtFuncionario;
    @FXML
    private JFXTextField txtUsuario;
    private ControlWindow<InfoUsuarioController> janelaInfoUsuario;
    private ControlWindow<UsuariosController> janelaUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcFuncionario.setCellValueFactory(cell -> cell.getValue().getFuncionario().nomeProperty());
        tcUsuario.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getUsuario()));
        this.janelaInfoUsuario = new ControlWindow(InfoUsuarioController.class);
    }

    @FXML
    private void mouseClickTabela(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvUsuario.getSelectionModel().getSelectedIndex() != -1) {
                Usuario usuario = tvUsuario.getSelectionModel().getSelectedItem();
                Usuario usuarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser();
                if (usuario.getId().equals(usuarioLogado.getId()) || usuarioLogado.getFuncionario().getPrivilegioAdmin() || usuarioLogado.getFuncionario().getFuncao() == FuncionarioTipo.GERENTE) {
                    this.janelaInfoUsuario.show(janelaUsuario);
                    janelaInfoUsuario.getController().carregarUsuario(usuario);
                } else {
                    DialogFX.showMessage("Somente um Gerente ou um Funcionário com privilégio de administrador podem ver dados de outros funcionários!", "Acesso Negado", DialogType.WARNING);
                }
            }
        }
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        FunctionAnnotations.clearFieldsWithAnnotations(this);
        this.carregarUsuarios();
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
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> from = cq.from(Usuario.class);
        Predicate condicao = cb.and();
        if (!txtFuncionario.getText().isEmpty()) {
            Root<Funcionario> fromFuncionario = cq.from(Funcionario.class);
            condicao = cb.and(condicao, cb.like(fromFuncionario.get(Funcionario_.nome), txtFuncionario.getText() + "%"), cb.equal(from.get(Usuario_.funcionario), fromFuncionario.get(Funcionario_.id)));
        }
        if (!txtUsuario.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.like(from.get(Usuario_.usuario), txtUsuario.getText() + "%"));
        }
        Query<Usuario> query = sessao.createQuery(cq.select(from).where(condicao));
        ObservableList<Usuario> lista = FXCollections.observableArrayList(query.getResultList());
        sessao.close();
        tvUsuario.setItems(lista);
    }

    public void carregarUsuarios() {
        Thread t = new Thread(this::carrega);
        t.setDaemon(true);
        t.start();
    }

    private void carrega() {
        tvUsuario.setItems(BancoDeDados.usuario().getAll());
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaUsuario = control;
    }
}
