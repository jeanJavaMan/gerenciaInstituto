/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.cadastro;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.ClearFields;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.NotClear;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import static br.jeanderson.enums.MaskType.NUMBER_ONLY;
import br.jeanderson.enums.ValidateType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
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
import jeanderson.enums.Converter;
import jeanderson.enums.MensalidadeTipo;
import jeanderson.enums.PagamentoTipo;
import jeanderson.enums.Semana;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Curso;
import jeanderson.model.Frequencia;
import jeanderson.model.Historico;
import jeanderson.model.Mensalidade;
import jeanderson.model.Turma;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/cadastro/FazerMatricula.fxml", title = "Fazer matrículas de Alunos")
public class FazerMatriculaController extends EasyFXFunctions implements Initializable {

    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    private TableView<Turma> tvTurma;
    @FXML
    private TableColumn<Turma, Integer> tcTurmaCodigo;
    @FXML
    private TableColumn<Turma, String> tcTurmaHorario;
    @FXML
    private TableColumn<Turma, Integer> tcTurmaQuantidade;
    @FXML
    private TableColumn<Turma, Boolean> tcTurmaDisponibilidade;
    @FXML
    private JFXCheckBox ccSegunda;
    @FXML
    private JFXCheckBox ccTerca;
    @FXML
    private JFXCheckBox ccQuinta;
    @FXML
    private JFXCheckBox ccQuarta;
    @FXML
    private JFXCheckBox ccSexta;
    @FXML
    private JFXCheckBox ccSabado;
    @FXML
    private JFXCheckBox ccDomingo;
    @FXML
    private TableView<Turma> tvTurmaSelecionada;
    @FXML
    private TableColumn<Turma, String> tcTurmaSelecionadaCurso;
    @FXML
    private TableColumn<Turma, Integer> tcTurmaSelecionadaCodigo;
    @FXML
    private TableColumn<Turma, String> tcTurmaSelecionadaHorario;
    @FXML
    private JFXButton btnRemoverTurma;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "CPF", type = ValidateType.CPF)
    @MaskFormatter(showMask = false, type = MaskType.CPF_DIG)
    private JFXTextField txtCPF;
    @FXML
    private JFXTextField txtRG;
    @FXML
    @ValidateField(fieldName = "Data de nascimento", type = ValidateType.DATA)
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpNascimento;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtTelefone;
    @FXML
    @ValidateField(fieldName = "Celular", type = ValidateType.TELEFONE)
    @MaskFormatter(showMask = false, type = MaskType.TEL_DIG)
    private JFXTextField txtCelular;
    @FXML
    @ValidateField(fieldName = "Endereço")
    private JFXTextField txtEndereco;
    @FXML
    @ValidateField(fieldName = "Bairro")
    private JFXTextField txtBairro;
    @FXML
    @NotClear
    @ValidateField(fieldName = "Cidade")
    private JFXTextField txtCidade;
    @FXML
    @NotClear
    @ValidateField(fieldName = "UF")
    private JFXTextField txtUF;
    @FXML
    private JFXCheckBox ccResponsavel;
    @FXML
    private JFXTextField txtResponsavelPor;
    @FXML
    private JFXTextArea txtObservacao;
    @FXML
    private TableView<Curso> tvCursoSelecionado;
    @FXML
    private TableColumn<Curso, String> tcCursoNome;
    @FXML
    private TableColumn<Curso, Double> tcCursoValor;
    @FXML
    @NotClear
    private JFXTextField txtValorCurso;
    @FXML
    private JFXComboBox<PagamentoTipo> cbFormaPagamento;
    @FXML
    private JFXComboBox<Integer> cbParcelas;
    @FXML
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpVencimento;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtMatriculaValor;
    @FXML
    private JFXCheckBox ccMatriculaPaga;
    @FXML
    @NotClear
    @MaskFormatter(showMask = false, type = NUMBER_ONLY)
    private JFXTextField txtDesconto;
    @FXML
    @NotClear
    private JFXTextField txtValorParcelado;
    @FXML
    @NotClear
    private JFXTextField txtValorTotal;
    @FXML
    private JFXCheckBox ccNaoGerarMensalidade;
    @FXML
    private JFXTabPane tabEtapas;
    @FXML
    @NotClear
    private JFXTextField txtJuros;
    @FXML
    @NotClear
    @ValidateField(fieldName = "Data de Matrícula", type = ValidateType.DATA)
    @MaskFormatter(showMask = false, type = MaskType.DATA_BARRA_DIG)
    private JFXDatePicker dpMatricula;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.cbFormaPagamento.setItems(FXCollections.observableArrayList(PagamentoTipo.values()));
        this.cbParcelas.setItems(FuncoesUtil.getQuantidadeParcela());

        this.tcTurmaCodigo.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcTurmaHorario.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getHorario()));
        this.tcTurmaQuantidade.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getAlunos().size()));
        this.tcTurmaDisponibilidade.setCellValueFactory(objeto -> objeto.getValue().isDisponivel());
        this.tcTurmaDisponibilidade.setCellFactory(cell -> FuncoesUtil.factoryColunaDisponibilidade());

        this.tcTurmaSelecionadaCurso.setCellValueFactory(objeto -> objeto.getValue().getCurso().nomeProperty());
        this.tcTurmaSelecionadaCodigo.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getId()));
        this.tcTurmaSelecionadaHorario.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getHorario()));

        this.tcCursoNome.setCellValueFactory(objeto -> objeto.getValue().nomeProperty());
        this.tcCursoValor.setCellValueFactory(objeto -> new SimpleObjectProperty<>(objeto.getValue().getValor()));

        this.tvTurma.getSelectionModel().selectedItemProperty().addListener(this::actionSelecionaTurma);
        this.txtResponsavelPor.visibleProperty().bindBidirectional(ccResponsavel.selectedProperty());
        this.txtMatriculaValor.disableProperty().bindBidirectional(ccMatriculaPaga.selectedProperty());
        txtCPF.focusedProperty().addListener(this::verificaCPF);
        txtRG.focusedProperty().addListener(this::verificaRG);
    }

    @FXML
    private void actionConfirmarTurma(ActionEvent event) {
        if (this.tvTurma.getSelectionModel().getSelectedIndex() != -1) {
            Turma turma = tvTurma.getSelectionModel().getSelectedItem();
            if (turma.isDisponivel().get()) {
                this.tvTurmaSelecionada.getItems().add(turma);
                calculaValorTotal(turma.getCurso(), "+");
                this.tvCursoSelecionado.getItems().add(turma.getCurso());
            } else {
                DialogFX.showMessage("Está turma se encontra indisponível!", "Turma indisponível", DialogType.WARNING);
            }
        } else {
            DialogFX.showMessage("Por favor selecione uma turma antes!", "Turma não selecionada!", DialogType.WARNING);
        }
    }

    @FXML
    private void actionRemoverTurma(ActionEvent event) {
        if (tvTurmaSelecionada.getSelectionModel().getSelectedIndex() != -1) {
            Turma turma = tvTurmaSelecionada.getSelectionModel().getSelectedItem();
            tvTurmaSelecionada.getItems().remove(turma);
            tvCursoSelecionado.getItems().remove(turma.getCurso());
            calculaValorTotal(turma.getCurso(), "-");
            this.actionParcelaSelecionada(event);
        }
    }

    @FXML
    private void actionConfirmarMatricula(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this) && this.verificarPagamento() && this.verificaTurma()) {
            Thread t = new Thread(() -> {
                Platform.runLater(this::confirmarMatricula);
            });
            t.setDaemon(true);
            t.start();
        }
    }

    private void confirmarMatricula() {
        if (tvTurmaSelecionada.getItems().size() > 0) {
            Aluno aluno = preparaAluno();
            ResultadoBD result = BancoDeDados.save(aluno);
            if (result.resultado()) {
                gerarFrequencia(aluno);
                ObservableList<Turma> turmas = this.tvTurmaSelecionada.getItems();
                boolean resultadoDaTurma = cadastrarNaTurma(turmas, aluno);
                boolean resultadoHistorico = gerarHistorico(aluno);
                if (resultadoDaTurma) {
                    if (resultadoHistorico) {
                        if (!this.ccNaoGerarMensalidade.isSelected()) {
                            boolean resultadoSalvaMensalidade = gerarMensalidades(aluno);
                            if (resultadoSalvaMensalidade) {
                                DialogFX.showMessage("Aluno matrículado com sucesso! Matrícula: " + aluno.getId(), "Aluno Matrículado", DialogType.SUCESS);
                                this.afterShow();
                                FunctionAnnotations.clearFieldsWithAnnotations(this);
                            }
                        } else {
                            this.afterShow();
                            FunctionAnnotations.clearFieldsWithAnnotations(this);
                            DialogFX.showMessage("Aluno matrículado com sucesso! Matrícula: " + aluno.getId(), "Aluno Matrículado", DialogType.SUCESS);
                        }
                    }
                } else {
                    DialogFX.showMessage("Como houve um problema ao atribuir o aluno a uma turma, as mensalidades não foram geradas!"
                            + " Observação: o Aluno já se encontra Cadastrado, Agora coloque ele em uma Turma e Gere as Mensalidades", "Mensalidades não geradas", DialogType.WARNING);
                }
            } else {
                DialogFX.showMessage("Não foi possível salvar o aluno! Motivo: " + result.mensagem(), "Aluno não salvo", DialogType.ERRO);
            }
        } else {
            DialogFX.showMessage("Nenhuma Turma foi selecionada", "Turma não selecionada.", DialogType.WARNING);
        }
    }

    @FXML
    private void actionSelecionaCurso(ActionEvent event) {
        if (this.cbCurso.getSelectionModel().getSelectedIndex() != -1) {
            Curso curso = cbCurso.getValue();
            Thread t = new Thread(() -> {
                Platform.runLater(() -> {
                    this.tvTurma.setItems(BancoDeDados.turma().getTurmaPorCurso(curso));
                });
            });
            t.setDaemon(true);
            t.start();
        }
    }

    private void actionSelecionaTurma(Observable valor) {
        if (this.tvTurma.getSelectionModel().getSelectedIndex() != -1) {
            Turma turma = tvTurma.getSelectionModel().getSelectedItem();
            this.marcarDiasDaSemanaTurma(turma);
        }
    }

    private void marcarDiasDaSemanaTurma(Turma turma) {
        limparDiasDaSemana();
        List<Semana> semanaDeAulas = turma.getDiasDaSemana();
        semanaDeAulas.forEach((diaDaSemana) -> {
            switch (diaDaSemana) {
                case SEGUNDA:
                    this.ccSegunda.setSelected(true);
                    break;
                case TERCA:
                    this.ccTerca.setSelected(true);
                    break;
                case QUARTA:
                    this.ccQuarta.setSelected(true);
                    break;
                case QUINTA:
                    this.ccQuinta.setSelected(true);
                    break;
                case SEXTA:
                    this.ccSexta.setSelected(true);
                    break;
                case SABADO:
                    this.ccSabado.setSelected(true);
                    break;
                case DOMINGO:
                    this.ccDomingo.setSelected(true);
                    break;
            }
        });
    }

    private void limparDiasDaSemana() {
        this.ccSegunda.setSelected(false);
        this.ccTerca.setSelected(false);
        this.ccQuarta.setSelected(false);
        this.ccQuinta.setSelected(false);
        this.ccSexta.setSelected(false);
        this.ccSabado.setSelected(false);
        this.ccDomingo.setSelected(false);
    }

    private void calculaValorTotal(Curso curso, String funcao) {
        Double valorTotal = FuncoesUtil.validaValor(txtValorCurso.getText());
        if (funcao.equals("+")) {
            valorTotal += curso.getValor();
        } else {
            valorTotal -= curso.getValor();
        }
        this.txtValorCurso.setText(FuncoesUtil.toDecimalFormat(valorTotal));
        this.txtValorTotal.setText(txtValorCurso.getText());
    }

    public void carregarCursos() {
        Thread t = new Thread(() -> {
            this.cbCurso.setItems(BancoDeDados.curso().queryAll());
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void mouseClickTurmaSelecionada(MouseEvent event) {
        if (tvTurmaSelecionada.getSelectionModel().getSelectedIndex() != -1) {
            this.btnRemoverTurma.setDisable(false);
        }
    }

    @FXML
    private void actionParcelaSelecionada(ActionEvent event) {
        if (this.cbParcelas.getSelectionModel().getSelectedIndex() != -1) {
            Integer quantidadeParcela = cbParcelas.getValue();
            Double valorTotal = FuncoesUtil.validaValor(txtValorTotal.getText());
            Double valorDividido = (valorTotal / quantidadeParcela);
            this.txtValorParcelado.setText(FuncoesUtil.toDecimalFormat(valorDividido));
        }
    }

    @FXML
    private void actionFormaPagamento(ActionEvent event) {
        if (this.cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            PagamentoTipo pagTipo = cbFormaPagamento.getValue();
            if (pagTipo == PagamentoTipo.A_VISTA) {
                this.cbParcelas.setDisable(true);
                this.cbParcelas.getSelectionModel().select(0);
                this.txtValorParcelado.setText("0");
                this.txtValorParcelado.setDisable(true);
            } else {
                this.cbParcelas.setDisable(false);
                this.txtValorParcelado.setDisable(false);
            }
        }
    }

    @FXML
    private void actionNaoGerarMensalidade(ActionEvent event) {
        boolean selected = this.ccNaoGerarMensalidade.isSelected();
        this.tvCursoSelecionado.setDisable(selected);
        this.txtValorCurso.setDisable(selected);
        this.txtValorParcelado.setDisable(selected);
        this.txtDesconto.setDisable(selected);
        this.cbFormaPagamento.setDisable(selected);
        this.cbParcelas.setDisable(selected);
        this.txtValorTotal.setDisable(selected);
        this.txtMatriculaValor.setDisable(selected);
        this.ccMatriculaPaga.setDisable(selected);
        this.dpVencimento.setDisable(selected);
        this.txtJuros.setDisable(selected);
    }

    @Override
    public void afterShow() {
        this.tvTurma.getItems().clear();
        this.tvTurmaSelecionada.getItems().clear();
        this.tvCursoSelecionado.getItems().clear();
        this.cbParcelas.getSelectionModel().select(0);
        this.txtDesconto.setText("0");
        this.txtMatriculaValor.setText("100.00");
        this.txtValorCurso.setText("0");
        this.txtValorParcelado.setText("0");
        this.txtValorTotal.setText("0");
        this.txtJuros.setText("0");
        this.tabEtapas.getSelectionModel().select(0);
        this.txtCidade.setText("Águas Lindas de Goiás");
        this.txtUF.setText("GO");
        this.tvCursoSelecionado.setDisable(false);
        this.txtValorTotal.setDisable(false);
        this.txtValorParcelado.setDisable(false);
        this.txtDesconto.setDisable(false);
        this.cbFormaPagamento.setDisable(false);
        this.cbParcelas.setDisable(false);
        this.txtValorCurso.setDisable(false);
        this.txtMatriculaValor.setDisable(false);
        this.ccMatriculaPaga.setDisable(false);
        this.dpVencimento.setDisable(false);
        this.dpMatricula.setValue(LocalDate.now());
    }

    private boolean verificarPagamento() {
        if (this.ccNaoGerarMensalidade.isSelected()) {
            return true;
        } else if (this.txtDesconto.getText().isEmpty() || this.dpVencimento.getEditor().getText().isEmpty() || this.cbFormaPagamento.getSelectionModel().getSelectedIndex() == -1) {
            DialogFX.showMessage("Data de vencimento, forma de pagamento ou desconto não podem está vázios, por favor preencha", "Campos não preenchidos", DialogType.WARNING);
            return false;
        } else {
            return true;
        }
    }

    private void gerarFrequencia(Aluno aluno) {
        List<Frequencia> frequenciaPorCurso = new ArrayList<>();
        tvCursoSelecionado.getItems().forEach(curso -> {
            Frequencia frequencia = new Frequencia();
            frequencia.setAluno(aluno);
            frequencia.setCurso(curso);
            frequencia.setCargaHoraria(curso.getCargaHoraria());
            frequencia.setCargaCumprida(Duration.ZERO);
            frequencia.setPorcentagemConcluida(0);
            frequenciaPorCurso.add(frequencia);
        });
        for (Frequencia fq : frequenciaPorCurso) {
            ResultadoBD resultadoFq = BancoDeDados.save(fq);
            if (!resultadoFq.resultado()) {
                DialogFX.showMessageAndWait("Houve um erro ao criar uma frequência para o aluno. Motivo: " + resultadoFq.mensagem() + "\nMas iremos executar os processos restantes!", "Erro ao gerar Frequência", DialogType.ERRO);
                break;
            }
        }
    }

    private boolean gerarMensalidades(Aluno aluno) {
        boolean resultadoSalvaMensalidade = false;
        List<Mensalidade> mensalidades = preparaMensalidade(aluno);
        int countMensalidadeCadastradas = 0;
        for (Mensalidade mensal : mensalidades) {
            ResultadoBD resultadoMensalidade = BancoDeDados.save(mensal);
            if (!resultadoMensalidade.resultado()) {
                if (countMensalidadeCadastradas < 1) {
                    DialogFX.showMessageAndWait("Houve um erro ao gerar as mensalidades do aluno!Motivo: " + resultadoMensalidade.mensagem() + ""
                            + "\nO Aluno já se encontrada cadastrado, vá em Gerência depois em Alunos Matriculados e gere novamente a mensalidade!", "Erro ao gerar mensalidade.", DialogType.ERRO);
                } else {
                    DialogFX.showMessageAndWait("Houve um erro ao gerar as mensalidades a partir da mensalidade número: " + (countMensalidadeCadastradas + 1) + ".Mensalidades anteriores a essa foram geradas!", "Nem todas as mensalidades foram geradas!", DialogType.ERRO);
                }
                resultadoSalvaMensalidade = false;
                break;
            }
            resultadoSalvaMensalidade = true;
            countMensalidadeCadastradas++;
        }
        return resultadoSalvaMensalidade;
    }

    private boolean cadastrarNaTurma(ObservableList<Turma> turmas, Aluno aluno) {
        boolean resultadoDaTurma = false;
        for (Turma turma : turmas) {
            turma.addAluno(aluno);
            ResultadoBD resultadoTurma = BancoDeDados.update(turma);
            if (!resultadoTurma.resultado()) {
                DialogFX.showMessageAndWait("Aluno foi cadastrado, mas não foi possível atribuir a turma número: " + turma.getId() + " para ele! Motivo: " + resultadoTurma.mensagem(), "Não foi possível atribuir Turma", DialogType.ERRO);
                resultadoDaTurma = false;
                break;
            }
            resultadoDaTurma = true;
        }
        return resultadoDaTurma;
    }

    private List<Mensalidade> preparaMensalidade(Aluno aluno) {
        List<Mensalidade> mensalidades = new ArrayList<>();

        LocalDate dataDeGeracao = LocalDate.now();
        LocalDate dataVencimento = dpVencimento.getValue();

        Mensalidade mensalidade = new Mensalidade();
        mensalidade.setAluno(aluno);
        mensalidade.setFormaPagamento(PagamentoTipo.A_VISTA);
        mensalidade.setTipoDaMensalidade(MensalidadeTipo.MATRICULA);
        if (ccMatriculaPaga.isSelected()) {
            mensalidade.setDataDePagamento(dataDeGeracao);
            mensalidade.setSituacao(true);
        } else {
            mensalidade.setSituacao(false);
        }
        mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtMatriculaValor.getText()));
        mensalidade.addCursosReferentes(tvCursoSelecionado.getItems());
        mensalidade.setNumeroDaParcela(0);
        mensalidade.setDataDeGeracao(dataDeGeracao);
        mensalidades.add(mensalidade);
        int quantidadeParcela = cbParcelas.getValue();
        if (quantidadeParcela <= 1) {
            mensalidade = new Mensalidade();
            mensalidade.setAluno(aluno);
            mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
            mensalidade.setTipoDaMensalidade(MensalidadeTipo.MENSALIDADE);
            mensalidade.setSituacao(false);
            mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtValorTotal.getText()));
            mensalidade.setNumeroDaParcela(1);
            mensalidade.setDataDeGeracao(dataDeGeracao);
            mensalidade.setDataVencimento(dataVencimento);
            mensalidade.addCursosReferentes(tvCursoSelecionado.getItems());
            mensalidades.add(mensalidade);
        } else {
            for (int i = 0; i < quantidadeParcela; i++) {
                mensalidade = new Mensalidade();
                mensalidade.setAluno(aluno);
                mensalidade.setFormaPagamento(cbFormaPagamento.getValue());
                mensalidade.setTipoDaMensalidade(MensalidadeTipo.MENSALIDADE);
                mensalidade.setSituacao(false);
                mensalidade.setValorParaPagar(FuncoesUtil.validaValor(txtValorParcelado.getText()));
                mensalidade.setNumeroDaParcela((i + 1));
                mensalidade.setDataDeGeracao(dataDeGeracao);
                mensalidade.setDataVencimento(dataVencimento.plusMonths(i));
                mensalidade.addCursosReferentes(tvCursoSelecionado.getItems());
                mensalidades.add(mensalidade);
            }
        }
        return mensalidades;
    }

    private Aluno preparaAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome(txtNome.getText().trim());
        aluno.setTelefone(txtTelefone.getText().trim());
        aluno.setCelular(txtCelular.getText());
        aluno.setCpf(txtCPF.getText().trim());
        if (!txtRG.getText().isEmpty()) {
            aluno.setRG(txtRG.getText().trim());
        }
        aluno.setEndereco(txtEndereco.getText().trim());
        aluno.setBairro(txtBairro.getText().trim());
        aluno.setCidade(txtCidade.getText().trim());
        aluno.setDataNascimento(dpNascimento.getValue());
        aluno.setDataMatricula(dpMatricula.getValue());
        aluno.setFuncionarioMatricula(SessaoLogin.USUARIO_LOGADO.getCurrentUser().getFuncionario());
        aluno.setUf(txtUF.getText().trim());
        aluno.setResponsavel(ccResponsavel.isSelected());
        aluno.setResponsavelPor(txtResponsavelPor.getText());
        aluno.setObservacao(txtObservacao.getText());
        return aluno;
    }

    private boolean gerarHistorico(Aluno aluno) {
        List<Historico> historicos = new ArrayList<>();
        boolean resultadoDaOperacao = true;
        tvCursoSelecionado.getItems().forEach(curso -> {
            Historico historico = new Historico();
            historico.setAluno(aluno);
            historico.setCurso(curso);
            historico.setDataMatriculaCurso(dpMatricula.getValue());
            historico.setSituacao(Boolean.FALSE);
            historicos.add(historico);
        });
        for (Historico historico : historicos) {
            ResultadoBD result = BancoDeDados.save(historico);
            if (!result.resultado()) {
                DialogFX.showMessageAndWait("Houve um erro ao tentar gerar um histórico para o Aluno! Motivo: " + result.mensagem() + "\nObs: o aluno já se encontra cadastrado."
                        + " Não será gerado uma mensalidade para o aluno, tente gerar uma mensalidade para que seja gerado um historico junto!", "Erro ao gerar historico", DialogType.ERRO);
                resultadoDaOperacao = false;
                break;
            }
        }
        return resultadoDaOperacao;
    }

    @FXML
    private void keyReleasedDesconto(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            this.txtValorTotal.setText(txtValorCurso.getText());
            Double valorTotal = FuncoesUtil.validaValor(txtValorCurso.getText());
            if (valorTotal > 0 && !txtDesconto.getText().isEmpty()) {
                int porcentagem = Integer.parseInt(this.txtDesconto.getText());
                Double valorComDesconto = valorTotal - ((valorTotal * porcentagem) / 100);
                this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorComDesconto));
                this.actionParcelaSelecionada(null);
            } else {
                this.actionParcelaSelecionada(null);
            }
        }
    }

    @FXML
    private void keyReleasedJuros(KeyEvent event) {
        if (cbFormaPagamento.getSelectionModel().getSelectedIndex() != -1) {
            if (this.txtJuros.getText().isEmpty()) {
                this.txtValorTotal.setText(txtValorCurso.getText());
                if (cbFormaPagamento.getValue() != PagamentoTipo.A_VISTA) {
                    this.actionParcelaSelecionada(null);
                }
            } else {
                if (this.cbFormaPagamento.getValue() == PagamentoTipo.A_VISTA) {
                    Double valorTotal = FuncoesUtil.validaValor(txtValorCurso.getText());
                    Double juros = FuncoesUtil.validaValor(txtJuros.getText());
                    Double valorComJuros = FuncoesUtil.calculaJuros(valorTotal, juros);
                    this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorComJuros));
                } else {
                    this.actionParcelaSelecionada(null);
                    Double valorParcelado = FuncoesUtil.validaValor(txtValorParcelado.getText());
                    Double juros = FuncoesUtil.validaValor(txtJuros.getText());
                    Double valorComJuros = FuncoesUtil.calculaJuros(valorParcelado, juros);
                    this.txtValorParcelado.setText(FuncoesUtil.toDecimalFormat(valorComJuros));
                    int quantidadeParcelas = this.cbParcelas.getValue();
                    Double valorTotal = (valorComJuros * quantidadeParcelas);
                    this.txtValorTotal.setText(FuncoesUtil.toDecimalFormat(valorTotal));
                }
            }
        } else {
            this.txtJuros.setText("0");
            DialogFX.showMessage("Selecione antes uma Forma de Pagamento", "Forma de pagamento não selecionados", DialogType.INFORMATION);
        }
    }

    private boolean verificaTurma() {
        boolean turmaFoiSelecionada = !tvTurmaSelecionada.getItems().isEmpty();
        if (turmaFoiSelecionada) {
            return true;
        } else {
            DialogFX.showMessage("Nenhuma Turma foi selecionada!", "Por favor Selecione a Turma", DialogType.WARNING);
            return false;
        }
    }

    private void verificaCPF(Observable valor) {
        if (!txtCPF.isFocused() && txtCPF.getText().length() == 14) {
            Thread t = new Thread(() -> {
                if (BancoDeDados.aluno().verificaCPF(txtCPF.getText())) {
                    Platform.runLater(() -> {
                        DialogFX.showMessageAndWait("Já existe um aluno cadastrado com o CPF informado!", "CPF já existente", DialogType.WARNING);
                        txtCPF.requestFocus();
                    });
                }

            });
            t.setDaemon(true);
            t.start();
        }
    }

    private void verificaRG(Observable valor) {
        if (!txtRG.isFocused() && !txtRG.getText().isEmpty()) {
            Thread t = new Thread(() -> {
                if (BancoDeDados.aluno().verificaRG(txtRG.getText())) {
                    Platform.runLater(() -> {
                        DialogFX.showMessageAndWait("Já existe um aluno cadastrado com o RG informado!", "RG já existente", DialogType.WARNING);
                        txtRG.requestFocus();
                    });
                }

            });
            t.setDaemon(true);
            t.start();
        }
    }

}
