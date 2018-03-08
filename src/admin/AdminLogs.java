/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/admin/AdminLogs.fxml")
public class AdminLogs implements Initializable {
    
    @FXML
    private TextArea txtLog;
    private File log;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log = new File("log.txt");
    }    
    
    @FXML
    private void actionExcluir(ActionEvent event) {
        if (DialogFX.showConfirmation("Tem certeza que deseja apagar o log?", "Apagar Log")) {
            if (log.exists()) {
                if (log.delete()) {
                    this.txtLog.setText("");
                } else {
                    DialogFX.showMessage("Não foi possivel apagar o log", "Log não apagado", DialogType.ERRO);
                }
            }
        }
    }
    
    public void carregarLog() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.carrega();
            });
        });
        t.setDaemon(true);
        t.start();
    }
    
    private void carrega() {        
        if (log.exists()) {
            try {
                List<String> logs = Files.readAllLines(Paths.get(log.getPath()));
                this.txtLog.setText("");
                logs.forEach((String logTexto) -> {
                    this.txtLog.appendText(logTexto + "\n");
                });
            } catch (IOException ex) {
                this.txtLog.setText("Houve um erro ao acessar o log: " + ex.getMessage());
            }
        } else {
            this.txtLog.setText("Nenhum Log encontrado");
        }
    }
}
