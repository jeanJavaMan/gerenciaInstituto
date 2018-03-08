/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.AutoCompleteComboBox;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import jeanderson.enums.Converter;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.model.Frequencia;
import jeanderson.model.Historico;
import jeanderson.model.Turma;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoFrequencia.fxml", title = "Frequência do Aluno")
public class InfoFrequenciaController extends EasyFXFunctions implements Initializable {

    @FXML
    private JFXTextField txtNome;
    @FXML
    private JFXTextField txtCPF;
    @FXML
    private JFXTextField txtRG;
    @FXML
    private JFXTextField txtMatricula;
    @FXML
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    @ValidateField(fieldName = "Carga a Cumprir")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtCargaACumprir;
    @FXML
    @ValidateField(fieldName = "Carga Cumprida")
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtCargaCumprida;
    @FXML
    private Text txtPorcentagemConcluida;
    @FXML
    @ValidateField(fieldName = "Horas cumpridas")
    @MaskFormatter(showMask = false, type = MaskType.DECIMAL_ONLY)
    private JFXTextField txtHorasCumpridas;
    @FXML
    private JFXSlider sHorasCumpridas;
    private Frequencia frequencia;
    private ControlWindow<InfoFrequenciaController> janelaInfoFrequencia;
    @FXML
    @ValidateField(fieldName = "Minutos cumpridos")
    private JFXTextField txtMinutosCumpridos;
    @FXML
    private JFXTextField txtFaltaCumprir;
    @FXML
    private JFXSlider sPorcentagemConcluida;
    @FXML
    private ImageView ivBloqueioPorcentagem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
        this.sHorasCumpridas.valueProperty().addListener(this::actionSliderAumento);
        this.sHorasCumpridas.setLabelFormatter(Converter.SLIDER_HORAS.getStringConverter());
    }

    @FXML
    private void actionConfirmarAumento(ActionEvent event) {
        if (!txtHorasCumpridas.getText().isEmpty() && FunctionAnnotations.validateFields(this)) {
            String porcentagemString = txtPorcentagemConcluida.getText().replace("%", "");
            int porcentagem = Integer.parseInt(porcentagemString);
            if (porcentagem < 100) {
                this.frequencia.aumentarCargaCumprida(Duration.ZERO.plusHours(Integer.parseInt(txtHorasCumpridas.getText())).plusMinutes(Integer.parseInt(txtMinutosCumpridos.getText())));
                Integer porcentagemConcluida = FuncoesUtil.calculaPorcentagem((int) frequencia.getCargaHoraria().toHours(), frequencia.getCargaCumprida().toHours());
                this.frequencia.setPorcentagemConcluida(porcentagemConcluida);
                ResultadoBD result = BancoDeDados.update(frequencia);
                if (result.resultado()) {
                    if (porcentagemConcluida >= 95) {
                        this.concluirCurso();
                    }
                    DialogFX.showMessage("Aumentado carga cumprida com sucesso! Atualize a Tabela", "Carga Cumprida aumentada", DialogType.SUCESS);
                    this.txtCargaCumprida.setText(Long.toString(frequencia.getCargaCumprida().toHours()));
                    this.txtPorcentagemConcluida.setText(frequencia.getPorcentagemConcluida().toString() + "%");
                    sPorcentagemConcluida.setValue(porcentagemConcluida);
                    atualizaSliderHoras();
                    afterShow();
                } else {
                    DialogFX.showMessage("Houve um erro ao aumentar a carga horaria cumprida! Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
                }
            } else {
                DialogFX.showMessage("O Aluno já concluiu a carga Horária. Não é possivel aumentar mais.", "Carga Horaria já concluída", DialogType.WARNING);
            }
        }
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        if (DialogFX.showConfirmation("Deseja realmente excluir está frequência?", "Excluir frequência")) {
            ResultadoBD result = BancoDeDados.delete(frequencia);
            if (result.resultado()) {
                DialogFX.showMessageAndWait("Frequência excluída com sucesso! Atualize a Tabela", "Frequência excluida", DialogType.SUCESS);
                this.janelaInfoFrequencia.close();
            } else {
                DialogFX.showMessage("Houve um erro ao excluir a Frequência. Motivo: " + result.mensagem(), "Erro", DialogType.ERRO);
            }
        }
    }

    private void actionSliderAumento(Observable valor) {
        Double valorAumentado = sHorasCumpridas.getValue();
        this.txtHorasCumpridas.setText(Integer.toString(valorAumentado.intValue()));
    }

    public void carregaFrequencia(Frequencia frequencia) {
        this.frequencia = frequencia;
        this.txtMatricula.setText(frequencia.getAluno().getId().toString());
        this.txtNome.setText(frequencia.getAluno().getNome());
        this.txtCPF.setText(frequencia.getAluno().getCpf());
        this.txtRG.setText(frequencia.getAluno().getRG());
        this.txtCargaACumprir.setText(frequencia.getCargaHoraria().toString().replace("PT", ""));
        this.txtCargaCumprida.setText(frequencia.getCargaCumprida().toString().replace("PT", "").replace("H", "H "));
        this.txtPorcentagemConcluida.setText(frequencia.getPorcentagemConcluida().toString() + "%");
        sPorcentagemConcluida.setValue(frequencia.getPorcentagemConcluida());
        this.cbCurso.getSelectionModel().select(frequencia.getCurso());
        atualizaSliderHoras();   
    }

    private void atualizaSliderHoras() {
        Duration diferencaDeHoras = frequencia.getCargaHoraria().minus(frequencia.getCargaCumprida());
        this.sHorasCumpridas.setMax(diferencaDeHoras.toHours());
        this.sHorasCumpridas.setValue(0);
        if (diferencaDeHoras.toString().contains("-")) {
            this.txtFaltaCumprir.setText("0H");
        } else {
            this.txtFaltaCumprir.setText(diferencaDeHoras.toString().replace("PT", "").replace("H", "H "));
        }
    }

    private void concluirCurso() {
        Thread t = new Thread(() -> {
            Historico historico = BancoDeDados.historico().getHistoricoAlunoECurso(frequencia.getAluno(), frequencia.getCurso());
            Turma turma = BancoDeDados.turma().getTurmaPorAlunoECurso(frequencia.getAluno(), frequencia.getCurso());
            if (historico != null) {
                historico.setDataConclusao(LocalDate.now());
                historico.setSituacao(Boolean.TRUE);
                BancoDeDados.update(historico);
            }
            if (turma != null) {
                BancoDeDados.turma().removeAluno(frequencia.getAluno(), turma);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void carregarCursos() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.cbCurso.setItems(BancoDeDados.curso().queryAll());
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaInfoFrequencia = control;
        this.carregarCursos();
    }

    @Override
    public void afterShow() {
        this.txtHorasCumpridas.setText("0");
        this.txtMinutosCumpridos.setText("0");
        this.sHorasCumpridas.setMin(0);
    }

    @FXML
    private void keyReleasedHorasCumpridas(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER && !txtHorasCumpridas.getText().isEmpty()) {
            Integer horaDigitada = Integer.parseInt(txtHorasCumpridas.getText());
            if (horaDigitada <= sHorasCumpridas.getMax()) {
                this.sHorasCumpridas.setValue(horaDigitada);
            } else {
                Double valorMaximo = this.sHorasCumpridas.getMax();
                this.sHorasCumpridas.setValue(valorMaximo);
                this.txtHorasCumpridas.setText(Integer.toString(valorMaximo.intValue()));
            }
        }
    }

}
