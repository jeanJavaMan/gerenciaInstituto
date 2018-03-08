/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.relatorio;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.enums.Converter;
import jeanderson.enums.FuncionarioTipo;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.Curso;
import jeanderson.model.Funcionario;
import jeanderson.model.Historico;
import jeanderson.model.Historico_;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/relatorio/MatriculaRelatorio.fxml", title = "Relatório de Matrículas")
public class MatriculaRelatorio extends EasyFXFunctions implements Initializable {

    @FXML
    private BarChart<String, Integer> bcGraficos;
    @FXML
    @NotClear
    private JFXComboBox<Funcionario> cbFuncionario;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private CheckBox ccVerTodos;
    @FXML
    private CategoryAxis caCategoria;
    private ObservableList<String> meses;
    private Month[] mesesIngles;
    private List<Aluno> alunoPorFuncionario;
    private Funcionario funcionarioLogado;
    private ControlWindow<MatriculaRelatorio> janelaMatriculaRelatorio;
    @FXML
    @NotClear
    @ValidateField(fieldName = "Ano Referente")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtAno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        cbFuncionario.setConverter(Converter.FUNCIONARIO.getStringConverter());
        String[] localMeses = DateFormatSymbols.getInstance(Locale.getDefault()).getMonths();
        this.meses = FXCollections.observableArrayList(localMeses);
        this.mesesIngles = Month.values();
        caCategoria.setCategories(this.meses);
        cbFuncionario.disableProperty().bindBidirectional(ccVerTodos.selectedProperty());
        txtAno.setText("" + LocalDate.now().getYear());
    }

    public void carregarDados() {
        Thread t = new Thread(this::carrega);
        t.setDaemon(true);
        t.start();
    }

    private void carrega() {
        cbFuncionario.setItems(BancoDeDados.funcionario().getFuncionarios());
        cbCurso.setItems(BancoDeDados.curso().queryAll());
        this.funcionarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario();
        //this.alunoPorFuncionario = BancoDeDados.aluno().getAlunoPorFuncionario(funcionarioLogado);
        this.alunoPorFuncionario = BancoDeDados.aluno().getAlunoPorFuncionario(funcionarioLogado, Integer.parseInt(txtAno.getText()));
        XYChart.Series dados = this.geraDados(alunoPorFuncionario, "Total de Alunos: " + alunoPorFuncionario.size());
        Platform.runLater(() -> {
            bcGraficos.setData(FXCollections.observableArrayList(dados));
            cbFuncionario.getSelectionModel().select(funcionarioLogado);
        });
    }

    @FXML
    private void actionAtualizar(ActionEvent event) {
        this.carregarDados();
        FunctionAnnotations.clearFieldsWithAnnotations(this);
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
        CriteriaQuery<Aluno> cq = cb.createQuery(Aluno.class);
        Root<Aluno> fromAluno = cq.from(Aluno.class);
        Predicate condicao = cb.and();
        if (cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Root<Historico> fromHistorico = cq.from(Historico.class);
            condicao = cb.and(condicao, cb.equal(fromAluno.get(Aluno_.id), fromHistorico.get(Historico_.aluno)), cb.equal(fromHistorico.get(Historico_.curso), cbCurso.getValue()));
        }
        if (!txtAno.getText().isEmpty() && txtAno.getText().length() == 4) {
            condicao = cb.and(condicao, cb.equal(cb.function("year", Integer.class, fromAluno.get(Aluno_.dataMatricula)), Integer.parseInt(txtAno.getText())));
        } else {
            Integer ano = LocalDate.now().getYear();
            condicao = cb.and(condicao, cb.equal(cb.function("year", Integer.class, fromAluno.get(Aluno_.dataMatricula)), ano));
            txtAno.setText(ano.toString());
        }
        if (!ccVerTodos.isSelected()) {
            condicao = cb.and(condicao, cb.equal(fromAluno.get(Aluno_.funcionarioMatricula), cbFuncionario.getValue()));
        }
        Query<Aluno> query = sessao.createQuery(cq.select(fromAluno).where(condicao));
        List<Aluno> lista = query.getResultList();
        sessao.close();
        XYChart.Series dados = this.geraDados(lista, "Total de Alunos: " + lista.size());
        Platform.runLater(() -> {
            bcGraficos.setData(FXCollections.observableArrayList(dados));
        });
    }

    private XYChart.Series geraDados(List<Aluno> alunos, String titleName) {
        XYChart.Series dados = new XYChart.Series();
        dados.setName(titleName);
        for (Month mes : mesesIngles) {
            Long quantidade = alunos.stream().filter((Aluno aluno) -> aluno.getDataMatricula().getMonthValue() == (mes.getValue() + 1)).count();
            dados.getData().add(new XYChart.Data<>(meses.get(mes.getValue()), quantidade.intValue()));
        }
        return dados;
    }

    @FXML
    private void actionSalvar(ActionEvent event) {
        DialogFX.showMessageAndWait("Será salvo como um arquivo de Imagem! Clique em Ok para selecionar a pasta onde vai salvar");
        DirectoryChooser escolheDir = new DirectoryChooser();
        escolheDir.setTitle("Escolha a pasta");
        escolheDir.setInitialDirectory(new File("C:/Users/" + System.getProperty("user.name") + "/Pictures"));
        File diretorioSelecionado = escolheDir.showDialog(janelaMatriculaRelatorio.getStage());
        if (diretorioSelecionado != null) {
            try {
                File novoDiretorio = new File(diretorioSelecionado + "/" + funcionarioLogado.getNome() + " Relatorios");
                boolean diretorioCriado = false;
                if (!novoDiretorio.exists()) {
                    diretorioCriado = novoDiretorio.mkdir();
                }
                if (diretorioCriado) {
                    WritableImage snapshot = bcGraficos.snapshot(new SnapshotParameters(), null);
                    BufferedImage fromFXImage = SwingFXUtils.fromFXImage(snapshot, null);
                    String dataScreen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-M-yyy-HH-m"));
                    ImageIO.write(fromFXImage, "png", new File(novoDiretorio + "/relatorioDeMatriculas" + dataScreen + ".png"));
                    DialogFX.showMessage("Imagem salva com sucesso!", "Sucesso!", DialogType.SUCESS);
                } else {
                    DialogFX.showMessage("Houve um erro ao tentar criar o diretório para salvar a Imagem! Tente selecionar outro diretório", "Erro ao tentar salvar", DialogType.ERRO);
                }
            } catch (IOException ex) {
                DialogFX.showMessage("Houve um erro ao tentar salvar a imagem! Motivo: " + ex.getMessage(), "Erro ao tentar salvar", DialogType.ERRO);
                Logger.getLogger(MatriculaRelatorio.class.getName()).log(Level.SEVERE, null, ex);
                Log.salvaLogger(ex);
            }
        }
    }

    @FXML
    private void actionSelecionaFuncionario(ActionEvent event) {
        if (cbFuncionario.getSelectionModel().getSelectedIndex() != -1) {
            Funcionario funcionarioSelecionado = cbFuncionario.getValue();
            if (!funcionarioLogado.getFuncao().equals(FuncionarioTipo.GERENTE) && !funcionarioLogado.getNome().equals(funcionarioSelecionado.getNome())) {
                DialogFX.showMessage("Somente um Gerente pode ver os dados de cada funcionário!", "Acesso não permitido", DialogType.WARNING);
                Platform.runLater(() -> {
                    cbFuncionario.getSelectionModel().select(funcionarioLogado);
                });
            }
        }
    }

    @Override
    public void afterShow() {
        txtAno.setText("" + LocalDate.now().getYear());
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaMatriculaRelatorio = control;
    }
}
