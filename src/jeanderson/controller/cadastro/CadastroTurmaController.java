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
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.enums.Converter;
import jeanderson.enums.Semana;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.model.Turma;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@ClearFields
@DefineConfiguration(url_fxml = "/jeanderson/view/cadastro/CadastraTurma.fxml", title = "Cadastro de Turmas")
public class CadastroTurmaController implements Initializable{

    @FXML
    @ValidateField(fieldName = "Curso da Turma")
    @AutoCompleteComboBox
    private JFXComboBox<Curso> cbCurso;
    @FXML
    @ValidateField(fieldName = "Horário")
    private JFXTextField txtHorario;
    @FXML
    @ValidateField(fieldName = "Limite de Alunos")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtLimiteAlunos;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cbCurso.setConverter(Converter.CURSOS.getStringConverter());
    }

    @FXML
    private void actionSalvar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            Turma turma = new Turma();
            turma.setCurso(cbCurso.getValue());
            turma.setLimiteAlunos(Integer.parseInt(txtLimiteAlunos.getText()));
            turma.setHorario(txtHorario.getText().trim());
            turma.setDiasDaSemana(this.pegarSemanasSelecionadas());
            ResultadoBD result = BancoDeDados.save(turma);
            if (result.resultado()) {
                DialogFX.showMessage("Turma cadastrada com sucesso!\nCódigo da Turma: " + turma.getId(), "Turma Cadastrada", DialogType.SUCESS);
                FunctionAnnotations.clearFieldsWithAnnotations(this);
            } else {
                DialogFX.showMessage("Houve um erro ao cadastrar turma! Motivo: " + result.mensagem(), "Erro ao cadastrar", DialogType.ERRO);
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

    private List<Semana> pegarSemanasSelecionadas() {
        List<Semana> listaSemana = new ArrayList<>();
        if (this.ccSegunda.isSelected()) {
            listaSemana.add(Semana.SEGUNDA);
        }
        if (this.ccTerca.isSelected()) {
            listaSemana.add(Semana.TERCA);
        }
        if (this.ccQuarta.isSelected()) {
            listaSemana.add(Semana.QUARTA);
        }
        if (this.ccQuinta.isSelected()) {
            listaSemana.add(Semana.QUINTA);
        }
        if (this.ccSexta.isSelected()) {
            listaSemana.add(Semana.SEXTA);
        }
        if (this.ccSabado.isSelected()) {
            listaSemana.add(Semana.SABADO);
        }
        if (this.ccDomingo.isSelected()) {
            listaSemana.add(Semana.DOMINGO);
        }
        return listaSemana;
    }
}
