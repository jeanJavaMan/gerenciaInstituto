/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.DefineConfiguration;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/Novidade.fxml",title = "Novidades desta versão")
public class NovidadeController implements Initializable {
    
    @FXML
    private Label txtNovidade;
    @FXML
    private Text txtPaginaAtual;
    @FXML
    private Text txtPaginaTotal;
    @FXML
    private ImageView ivNovidade;
    private List<Image> listaDeImagem;
    private List<String> listaDeTexto;
    private Integer paginaAtual;
    @FXML
    private JFXButton btnAnterior;
    @FXML
    private JFXButton btnProximo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.listaDeImagem = new ArrayList<>();
        this.listaDeTexto = new ArrayList<>();
        this.carregarListas();
        this.paginaAtual = 1;
        
        this.verificaQuantImagens();
        btnAnterior.setDisable(true);
        txtPaginaTotal.setText(Integer.toString(listaDeImagem.size()));
        txtPaginaAtual.setText(paginaAtual.toString());
    }    
    
    @FXML
    private void actionAnterior(ActionEvent event) {
        if (paginaAtual > 1) {
            paginaAtual--;
            this.ivNovidade.setImage(listaDeImagem.get((paginaAtual - 1)));
            this.txtNovidade.setText(listaDeTexto.get((paginaAtual - 1)));
            if (btnProximo.isDisable()) {
                btnProximo.setDisable(false);
            }
            txtPaginaAtual.setText(paginaAtual.toString());
        }
        if(paginaAtual == 1){
            btnAnterior.setDisable(true);
        }
    }
    
    @FXML
    private void actionProximo(ActionEvent event) {
        if (paginaAtual < listaDeImagem.size()) {
            paginaAtual++;
            this.ivNovidade.setImage(listaDeImagem.get((paginaAtual - 1)));
            this.txtNovidade.setText(listaDeTexto.get((paginaAtual - 1)));
            if (btnAnterior.isDisable()) {
                btnAnterior.setDisable(false);
            }
            txtPaginaAtual.setText(paginaAtual.toString());
        }
        if(paginaAtual == listaDeImagem.size()){
            btnProximo.setDisable(true);
        }
    }

    private void verificaQuantImagens() {
        if (listaDeImagem.size() > 1) {
            btnProximo.setDisable(false);
        } else {
            btnProximo.setDisable(true);
        }
    }
    
    private void carregarListas() {
        this.listaDeImagem.add(new Image("/jeanderson/assets/img/novidades/n1.png"));
        this.listaDeImagem.add(new Image("/jeanderson/assets/img/novidades/n2.png"));
        
        this.listaDeTexto.add("Agora é possível colocar uma observação referente a um aluno no Lembrete. Basta marcar a opção e colocar a data para lembrar e pronto."
                + " Conforme a imagem acima.");
        this.listaDeTexto.add("Para saber mais detalhes de cada versão, Basta olhar em Notas da versão.");
        ivNovidade.setImage(listaDeImagem.get(0));
        txtNovidade.setText(listaDeTexto.get(0));
    }
}
