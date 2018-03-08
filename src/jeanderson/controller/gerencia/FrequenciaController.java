/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.AutoCompleteComboBox;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Frequencia;
import jeanderson.model.Frequencia_;
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
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/Frequencia.fxml", title = "Frequência de Alunos")
public class FrequenciaController extends EasyFXFunctions implements Initializable {

    @FXML
    private TableView<Frequencia> tvFrequencia;
    @FXML
    private TableColumn<Frequencia, Integer> tcMatricula;
    @FXML
    private TableColumn<Frequencia, String> tcAluno;
    @FXML
    private TableColumn<Frequencia, String> tcCurso;
    @FXML
    private TableColumn<Frequencia, Integer> tcConcluido;
    @FXML
    private JFXTextField txtMatricula;
    @FXML
    private JFXTextField txtAluno;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private JFXComboBox<String> cbConcluido;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimite;

    private ControlWindow<InfoFrequenciaController> janelaInfoFrequencia;
    private ControlWindow<FrequenciaController> janelaFrequencia;
    private ControlWindow<NovaFrequenciaController> janelaNovaFrequencia;
    private ObservableList<String> listaPorcentagens;
    private HelpLimit helpLimit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.janelaInfoFrequencia = new ControlWindow(InfoFrequenciaController.class);
        this.janelaNovaFrequencia = new ControlWindow(NovaFrequenciaController.class);

        this.helpLimit = new HelpLimit(Integer.parseInt(txtLimite.getText()));

        this.tcMatricula.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getAluno().getId()));
        this.tcAluno.setCellValueFactory(objeto -> objeto.getValue().getAluno().nomeProperty());
        this.tcCurso.setCellValueFactory(objeto -> objeto.getValue().getCurso().nomeProperty());
        this.tcConcluido.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getPorcentagemConcluida()));
        this.tcConcluido.setCellFactory(cell -> FuncoesUtil.factoryColunaPorcentagem());
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());

        carregarListaPorcentagem();
    }

    private void carregarListaPorcentagem() {
        this.listaPorcentagens = FXCollections.observableArrayList();
        this.listaPorcentagens.add("0 - 25");
        this.listaPorcentagens.add("26 - 50");
        this.listaPorcentagens.add("51 - 75");
        this.listaPorcentagens.add("76 - 100");
        this.cbConcluido.setItems(listaPorcentagens);
    }

    private Integer[] pegarPorcentagemSelecionada() {
        int index = cbConcluido.getSelectionModel().getSelectedIndex();
        Integer[] valor = new Integer[2];
        switch (index) {
            case 0:
                valor[0] = 0;
                valor[1] = 25;
                return valor;
            case 1:
                valor[0] = 26;
                valor[1] = 50;
                return valor;
            case 2:
                valor[0] = 51;
                valor[1] = 75;
                return valor;
            case 3:
                valor[0] = 76;
                valor[1] = 100;
                return valor;
            default:
                valor[0] = 0;
                valor[1] = 0;
                return valor;
        }
    }

    public void carregarTabela() {
        Thread t = new Thread(() -> {
            this.helpLimit.atualizaLimit(Integer.parseInt(txtLimite.getText()));
            if (helpLimit.isMaiorQueAnterior()) {
                this.tvFrequencia.getItems().addAll(BancoDeDados.frequencia().getFrequencias(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            } else {
                this.tvFrequencia.setItems(BancoDeDados.frequencia().getFrequencias(helpLimit.getValorInicial(), helpLimit.getValorMaximo()));
            }
        });
        t.setDaemon(true);
        t.start();
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
    private void mouseClickFrequencia(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvFrequencia.getSelectionModel().getSelectedIndex() != -1) {
                Frequencia frequencia = tvFrequencia.getSelectionModel().getSelectedItem();
                this.janelaInfoFrequencia.show(janelaFrequencia);
                this.janelaInfoFrequencia.getController().carregarCursos();
                this.janelaInfoFrequencia.getController().carregaFrequencia(frequencia);
            }
        }
    }

    public void carregarCursos() {
        Thread t = new Thread(() -> {
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    private void filtrar() throws NumberFormatException {
        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Frequencia> cq = cb.createQuery(Frequencia.class);
        Root<Frequencia> from = cq.from(Frequencia.class);
        Predicate condicao = cb.and();
        if (!this.txtMatricula.getText().isEmpty()) {
            condicao = cb.and(condicao, cb.equal(from.get(Frequencia_.aluno), Integer.parseInt(txtMatricula.getText())));
        }
        if (!this.txtAluno.getText().isEmpty()) {
            Join<Frequencia, Aluno> alunoJoin = from.join(Frequencia_.aluno);
            condicao = cb.and(condicao, cb.like(alunoJoin.get(Aluno_.nome), txtAluno.getText() + "%"));
        }
        if (cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            condicao = cb.and(condicao, cb.equal(from.get(Frequencia_.curso), curso));
        }
        if (cbConcluido.getSelectionModel().getSelectedIndex() != -1) {
            Integer[] porcentagemSelecionada = this.pegarPorcentagemSelecionada();
            Predicate condicaoNova = cb.between(from.get(Frequencia_.porcentagemConcluida), porcentagemSelecionada[0], porcentagemSelecionada[1]);
            condicao = cb.and(condicao, condicaoNova);
        }
        Query<Frequencia> query = sessao.createQuery(cq.select(from).where(condicao));
        query.setFirstResult(0).setMaxResults(Integer.parseInt(txtLimite.getText()));
        ObservableList<Frequencia> lista = FXCollections.observableArrayList(query.getResultList());
        this.tvFrequencia.setItems(lista);
        sessao.close();
    }

//    public String toSql(String hqlQueryText, SessionFactory session) {
//        if (hqlQueryText != null && hqlQueryText.trim().length() > 0) {
//            final QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
//            final SessionFactoryImplementor factory
//                    = (SessionFactoryImplementor) session;
//            QueryTranslator translator = translatorFactory.
//                    createQueryTranslator(
//                            hqlQueryText,
//                            hqlQueryText,
//                            Collections.EMPTY_MAP, factory,null
//                    );
//            translator.compile(Collections.EMPTY_MAP, false);
//            return translator.getSQLString();
//            
//        }
//        return null;
//    }

    @Override
    public void afterShow() {
        this.txtLimite.setText("50");
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaFrequencia = control;
        this.carregarCursos();
    }

    @FXML
    private void actionNovaFrequencia(ActionEvent event) {
        this.janelaNovaFrequencia.show(janelaFrequencia);
        this.janelaNovaFrequencia.getController().carregarDados();
    }

}
