/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import jeanderson.controller.cadastro.CadastraCursoController;
import jeanderson.controller.cadastro.CadastraFuncionarioController;
import jeanderson.controller.cadastro.CadastraUsuarioController;
import jeanderson.controller.cadastro.CadastroTurmaController;
import jeanderson.controller.cadastro.FazerMatriculaController;
import jeanderson.controller.cadastro.MatricularAlunoController;
import jeanderson.controller.financeiro.CaixaController;
import jeanderson.controller.financeiro.ContasReceberController;
import jeanderson.controller.financeiro.GerarContasController;
import jeanderson.controller.financeiro.GerarParcelaController;
import jeanderson.controller.gerencia.AlunoV2Controller;
import jeanderson.controller.gerencia.CursosController;
import jeanderson.controller.gerencia.FrequenciaController;
import jeanderson.controller.gerencia.Funcionarios;
import jeanderson.controller.gerencia.TurmasController;
import jeanderson.controller.gerencia.UsuariosController;
import jeanderson.controller.relatorio.CaixaRelatorio;
import jeanderson.controller.relatorio.MatriculaRelatorio;
import jeanderson.controller.relatorio.MensalidadeRelatorio;
import jeanderson.controller.relatorio.TurmaRelatorio;
import jeanderson.controller.usuario.AdicionaLembreteController;
import jeanderson.controller.usuario.InfoLembreteController;
import jeanderson.controller.usuario.MeusContatosController;
import jeanderson.controller.usuario.MeusLembretesController;
import jeanderson.controller.usuario.NotaVersaoController;
import jeanderson.controller.usuario.NovidadeController;
import jeanderson.controller.usuario.NovoContatoController;
import jeanderson.enums.Converter;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Lembrete;
import jeanderson.model.Usuario;
import jeanderson.controller.usuario.ReportaErroController;
import jeanderson.util.Log;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/Home.fxml", title = "Gerência Instituto", url_icon = "/jeanderson/assets/img/icone_principal.png")
public class HomeController extends EasyFXFunctions implements Initializable {

    @FXML
    private Label lbNomeUsuario;
    @FXML
    private Label lbFuncaoUsuario;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpLembretes;
    @FXML
    private JFXListView<Lembrete> lvLembretes;
    private ControlWindow<HomeController> janelaHome;
    private Usuario usuarioLogado;
    private ControlWindow<NovoContatoController> janelaNovoContato;
    private ControlWindow<MeusContatosController> janelaMeusContatos;
    private ControlWindow<CadastroTurmaController> janelaCadastraTurma;
    private ControlWindow<CadastraCursoController> janelaCadastraCurso;
    private ControlWindow<FazerMatriculaController> janelaFazerMatricula;
    private ControlWindow<FrequenciaController> janelaFrequencia;
    private ControlWindow<AlunoV2Controller> janelaAlunosMatriculados;
    private ControlWindow<ContasReceberController> janelaContasReceber;
    private ControlWindow<GerarContasController> janelaGerarMensalidades;
    private ControlWindow<TurmasController> janelaTurmas;
    private ControlWindow<MeusLembretesController> janelaMeusLembretes;
    private ControlWindow<CadastraFuncionarioController> janelaCadastraFuncionario;
    private ControlWindow<CadastraUsuarioController> janelaCadastraUsuario;
    private ControlWindow<CursosController> janelaCursos;
    private ControlWindow<CaixaController> janelaCaixa;
    //private ControlWindow<NotaVersaoController> janelaNotasVersao;
    private ControlWindow<AdicionaLembreteController> janelaAdicionarLembrete;
    private ControlWindow<InfoLembreteController> janelaInfoLembrete;
    private ControlWindow<ReportaErroController> janelaReportaErro;
    private ControlWindow<MatriculaRelatorio> janelaMatriculaRelatorio;
    private ControlWindow<Funcionarios> janelaFuncionarios;
    private ControlWindow<UsuariosController> janelaUsuarios;
    // private ControlWindow<TutorialController> janelaTutorial;
    private ControlWindow janelaNotaVersao;
    private ControlWindow<MatricularAlunoController> janelaMatriculaAluno;
    private ControlWindow<GerarParcelaController> janelaGerarParcela;
    private ControlWindow<MensalidadeRelatorio> janelaGerarRelatorioMensalidade;
    private ControlWindow<TurmaRelatorio> janelaGerarRelatorioTurma;
    private ControlWindow<CaixaRelatorio> janelaGerarRelatorioCaixa;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.lvLembretes.setCellFactory(cell -> new TextFieldListCell<>(Converter.LEMBRETE_PARCIAL.getStringConverter()));
        this.janelaNovoContato = new ControlWindow(NovoContatoController.class);
        this.janelaMeusContatos = new ControlWindow(MeusContatosController.class);
        this.janelaCadastraTurma = new ControlWindow(CadastroTurmaController.class);
        this.janelaCadastraCurso = new ControlWindow(CadastraCursoController.class);
        this.janelaFazerMatricula = new ControlWindow(FazerMatriculaController.class);
        this.janelaFrequencia = new ControlWindow(FrequenciaController.class);
        this.janelaAlunosMatriculados = new ControlWindow(AlunoV2Controller.class);
        this.janelaContasReceber = new ControlWindow(ContasReceberController.class);
        this.janelaGerarMensalidades = new ControlWindow(GerarContasController.class);
        this.janelaTurmas = new ControlWindow(TurmasController.class);
        this.janelaMeusLembretes = new ControlWindow(MeusLembretesController.class);
        this.janelaCadastraFuncionario = new ControlWindow(CadastraFuncionarioController.class);
        this.janelaCadastraUsuario = new ControlWindow(CadastraUsuarioController.class);
        this.janelaCursos = new ControlWindow(CursosController.class);
        this.janelaCaixa = new ControlWindow(CaixaController.class);
        this.janelaAdicionarLembrete = new ControlWindow(AdicionaLembreteController.class);
        this.janelaInfoLembrete = new ControlWindow(InfoLembreteController.class);
        this.janelaReportaErro = new ControlWindow(ReportaErroController.class);
        this.janelaMatriculaRelatorio = new ControlWindow(MatriculaRelatorio.class);
        this.janelaFuncionarios = new ControlWindow(Funcionarios.class);
        this.janelaUsuarios = new ControlWindow(UsuariosController.class);
        this.janelaNotaVersao = new ControlWindow(NotaVersaoController.class);
        this.janelaMatriculaAluno = new ControlWindow(MatricularAlunoController.class);
        this.janelaGerarParcela = new ControlWindow(GerarParcelaController.class);
        this.janelaGerarRelatorioMensalidade = new ControlWindow(MensalidadeRelatorio.class);
        this.janelaGerarRelatorioTurma = new ControlWindow(TurmaRelatorio.class);
        this.janelaGerarRelatorioCaixa = new ControlWindow(CaixaRelatorio.class);
    }

    @FXML
    private void actionVerTodosLembretes(ActionEvent event) {
        this.janelaMeusLembretes.show(janelaHome);
        this.janelaMeusLembretes.getController().carregarTabela();
        this.janelaMeusLembretes.getController().pegarHome(janelaHome);
    }

    @FXML
    private void actionNovoContato(ActionEvent event) {
        this.janelaNovoContato.show(janelaHome);
        this.janelaNovoContato.getController().carregarCursos();
    }

    @FXML
    private void actionMeusContatos(ActionEvent event) {
        this.janelaMeusContatos.show(janelaHome);
        this.janelaMeusContatos.getController().carregarCursos();
        this.janelaMeusContatos.getController().carregarTabela();
    }

    @FXML
    private void actionFazerMatriculas(ActionEvent event) {
        this.janelaFazerMatricula.show(janelaHome);
        this.janelaFazerMatricula.getController().carregarCursos();

    }

    @FXML
    private void actionCadastrarCursos(ActionEvent event) {
        this.janelaCadastraCurso.show(janelaHome);
    }

    @FXML
    private void actionCadastrarFuncionarios(ActionEvent event) {
        this.janelaCadastraFuncionario.show(janelaHome);
    }

    @FXML
    private void actionCadastrarUsuarios(ActionEvent event) {
        this.janelaCadastraUsuario.show(janelaHome);
        this.janelaCadastraUsuario.getController().carregarFuncionarios();
    }

    @FXML
    private void actionCadastrarTurmas(ActionEvent event) {
        this.janelaCadastraTurma.show(janelaHome);
        this.janelaCadastraTurma.getController().carregarCursos();
    }

    @FXML
    private void actionAlunosMatriculados(ActionEvent event) {
        this.janelaAlunosMatriculados.show(janelaHome);
    }

    @FXML
    private void actionFrequenciaAlunos(ActionEvent event) {
        this.janelaFrequencia.show(janelaHome);
        this.janelaFrequencia.getController().carregarTabela();
    }

    @FXML
    private void actionTurmas(ActionEvent event) {
        this.janelaTurmas.show(janelaHome);
        this.janelaTurmas.getController().carregarDados();
    }

    @FXML
    private void actionContasAReceber(ActionEvent event) {
        this.janelaContasReceber.show(janelaHome);
        this.janelaContasReceber.getController().carregarTabela();
    }

    @FXML
    private void actionGerarContas(ActionEvent event) {
        this.janelaGerarMensalidades.show(janelaHome);
        this.janelaGerarMensalidades.getController().carregarDados();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaHome = control;
    }

    public void carregarDados() {
        this.usuarioLogado = SessaoLogin.USUARIO_LOGADO.getCurrentUser();
        this.lbNomeUsuario.setText(usuarioLogado.getFuncionario().getNome());
        this.lbFuncaoUsuario.setText(usuarioLogado.getFuncionario().getFuncao().toString());
        this.dpLembretes.setValue(LocalDate.now());
        this.carregarLembretes();
    }

    private void carregarLembretes() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                lvLembretes.setItems(BancoDeDados.lembrete().pegarPorData(dpLembretes.getValue(), usuarioLogado.getFuncionario()));
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void actionSelecionaDataLembrete(ActionEvent event) {
        this.carregarLembretes();
    }

    @FXML
    private void actionCursos(ActionEvent event) {
        this.janelaCursos.show(janelaHome);
        this.janelaCursos.getController().carregar();
    }

    @FXML
    private void actionMeuCaixa(ActionEvent event) {
        this.janelaCaixa.show(janelaHome);
        this.janelaCaixa.getController().carregarCaixa();
    }

//    private void actionNotasVersao(ActionEvent event) {
//        this.janelaNotasVersao.show(janelaHome);
//        this.janelaNotasVersao.getWindow().getController().carregarImagens();
//        this.janelaNotasVersao.getWindow().getController().pegarHome(janelaHome);
//    }
    @FXML
    private void actionAtualizaLembrete(ActionEvent event) {
        this.carregarLembretes();
    }

    @FXML
    private void actionAdicionarLembrete(ActionEvent event) {
        this.janelaAdicionarLembrete.show(janelaHome);
    }

    @FXML
    private void mouseClickLembrete(MouseEvent event) {
        if (event.getClickCount() == 2 && lvLembretes.getSelectionModel().getSelectedIndex() != -1) {
            Lembrete lembrete = lvLembretes.getSelectionModel().getSelectedItem();
            this.mostrarJanelaInfoLembrete(lembrete);
        }
    }

    public void mostrarJanelaInfoLembrete(Lembrete lembrete) {
        if (lembrete.getContato() == null && lembrete.getAluno() == null) {
            this.janelaInfoLembrete.show(janelaHome);
            janelaInfoLembrete.getController().passarlembrete(lembrete);
        } else if (lembrete.getAluno() != null) {
            DialogFX.showMessage("Para editar esta observação referente ao aluno, vai em Alunos Matriculados e filtre pela matrícula: " + lembrete.getAluno().getId(), "Lembrete de Aluno", DialogType.INFORMATION);
        } else {
            DialogFX.showMessage("É possivel apenas editar lembretes pessoais. Lembrete que são referentes a contatos, você deve ir em Meus contatos para editar!", "Lembrete de Contato", DialogType.INFORMATION);
        }
    }

    @FXML
    private void actionReportarErros(ActionEvent event) {
        this.janelaReportaErro.show(janelaHome);
        this.janelaReportaErro.getController().carregaNome();
    }

    @FXML
    private void actionMatriculaRelatorio(ActionEvent event) {
        this.janelaMatriculaRelatorio.show(janelaHome);
        janelaMatriculaRelatorio.getController().carregarDados();
    }

    @FXML
    private void actionFuncionarios(ActionEvent event) {
        this.janelaFuncionarios.show(janelaHome);
        janelaFuncionarios.getController().carregarDados();
    }

    @FXML
    private void actionUsuarios(ActionEvent event) {
        this.janelaUsuarios.show(janelaHome);
        janelaUsuarios.getController().carregarUsuarios();
    }

    @FXML
    private void actionNotasVersao(ActionEvent event) {
        this.janelaNotaVersao.show(janelaHome);
    }

    @FXML
    private void actionRestaurarVersao(ActionEvent event) {
        if (DialogFX.showConfirmation("Tem certeza que deseja voltar a versão anterior?", "Restaurar Backup?")) {
            try {
                Runtime.getRuntime().exec("java -jar " + new File("Restaura.jar").getAbsolutePath());
                this.janelaHome.close();
            } catch (IOException ex) {
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                Log.salvaLogger(ex);
                DialogFX.showMessage("Não foi possível atualizar! Motivo: " + ex.getMessage(), "Erro ao Atualizar", DialogType.ERRO);
            }
        }
    }

    @FXML
    private void actionMatricularAlunoEmCurso(ActionEvent event) {
        this.janelaMatriculaAluno.show(janelaHome);
        this.janelaMatriculaAluno.getController().carregar();
    }

    @FXML
    private void actionGerarParcela(ActionEvent event) {
        this.janelaGerarParcela.show(janelaHome);
    }

    @FXML
    private void actionGerarRelatorioMensalidades(ActionEvent event) {
        this.janelaGerarRelatorioMensalidade.show(janelaHome);
    }

    @FXML
    private void actionGerarRelatorioTurma(ActionEvent event) {
        this.janelaGerarRelatorioTurma.show(janelaHome);
    }

    @FXML
    private void actionGerarRelatorioCaixa(ActionEvent event) {
        this.janelaGerarRelatorioCaixa.show(janelaHome);
    }
    
    public void exibirJanelaNovidade(){
        ControlWindow<NovidadeController> janelaNovidade = new ControlWindow(NovidadeController.class);
        janelaNovidade.show(janelaHome);
    }
    
}
