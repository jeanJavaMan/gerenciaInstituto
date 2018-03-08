/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.gerencia;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Curso;
import jeanderson.util.FuncoesUtil;
import jeanderson.util.ResultadoBD;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/gerencia/InfoCurso.fxml", title = "Informações do Curso")
public class InfoCursos extends EasyFXFunctions implements Initializable{

    @FXML
    private JFXTextField txtCodigo;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Carga Horária")
    private JFXTextField txtCargaHoraria;
    @FXML
    @ValidateField(fieldName = "Valor")
    private JFXTextField txtValor;
    @FXML
    private JFXTextArea txtInformacoes;
    @FXML
    private JFXButton btnEditar;
    private Curso curso;
    private BooleanProperty editar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.editar = new SimpleBooleanProperty(false);
        this.txtCodigo.setEditable(false);
        this.txtNome.editableProperty().bindBidirectional(editar);
        this.txtCargaHoraria.editableProperty().bindBidirectional(editar);
        this.txtValor.editableProperty().bindBidirectional(editar);
        this.txtInformacoes.editableProperty().bindBidirectional(editar);
    }

    public void carregaCurso(Curso curso) {
        this.curso = curso;
        txtCodigo.setText(curso.getId().toString());
        txtNome.setText(curso.getNome());
        txtCargaHoraria.setText(Long.toString(curso.getCargaHoraria().toHours()));
        txtValor.setText(FuncoesUtil.toDecimalFormat(curso.getValor()));
        txtInformacoes.setText(curso.getConteudo());
    }

    @FXML
    private void actionEditar(ActionEvent event) {
        if (editar.get()) {
            this.salvarAlteracoes();

        } else {
            this.btnEditar.setText("Salvar");
            this.editar.set(true);
        }

    }

    private void salvarAlteracoes() {
        if (FunctionAnnotations.validateFields(this)) {
            curso.setNome(txtNome.getText());
            curso.setCargaHoraria(Duration.ofHours(Long.parseLong(txtCargaHoraria.getText())));
            curso.setConteudo(txtInformacoes.getText());
            curso.setValor(FuncoesUtil.validaValor(txtValor.getText()));
            ResultadoBD result = BancoDeDados.update(curso);
            if (result.resultado()) {
                DialogFX.showMessage("Alterações feita com sucesso! Atualize a Tabela", "Alterações Feitas!", DialogType.SUCESS);
                this.afterShow();
            } else {
                DialogFX.showMessage("Houve um erro ao tentar salvar as alterações! Motivo: " + result.mensagem(), "Erro ao tentar alterar", DialogType.ERRO);
            }
        }
    }

    @Override
    public void afterShow() {
        this.editar.set(false);
        this.btnEditar.setText("Editar");
    }

    @FXML
    private void actionExcluir(ActionEvent event) {
        DialogFX.showMessageAndWait("Caso este curso esteja referênciado por uma turma,aluno e etc, não será possível apagar-lo até remover todas as referências a ele! ", "Atenção", DialogType.INFORMATION);
        if (DialogFX.showConfirmation("Tem certeza que deseja tentar apagar este curso?", "Apagar curso")) {
            ResultadoBD result = BancoDeDados.delete(curso);
            if (result.resultado()) {
                DialogFX.showMessage("Curso removido com sucesso!", "Curso Excluido", DialogType.SUCESS);
            } else {
                DialogFX.showMessage("Erro ao remover o curso! Provavelmente ele já está referenciado! Motivo: " + result.mensagem(), "Erro ao tentar excluir", DialogType.ERRO);
            }
        }
    }

}
