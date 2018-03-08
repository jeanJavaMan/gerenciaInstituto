/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import admin.classesxml.HibernateConfig;
import admin.classesxml.Mapping;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import com.jfoenix.controls.JFXTextField;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/admin/AdminClassModel.fxml", resizable = false)
public class AdminClassModel extends EasyFXFunctions implements Initializable {

    @FXML
    private TextArea txtClassesExistentes;
    @FXML
    private JFXTextField txtNomeClasse;
    private File arquivo;
    private XStream xstream;
    private HibernateConfig config;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.arquivo = new File("hibernate.cfg.xml");
        this.xstream = new XStream();
        xstream.processAnnotations(HibernateConfig.class);
    }

    public void carregar() {
        Thread t = new Thread(() -> {
            Platform.runLater(() -> {
                this.carrega();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void carrega() {
        if (this.arquivo.exists()) {
            this.config = (HibernateConfig) xstream.fromXML(arquivo);
            config.getFactory().getMappings().forEach((Mapping mapping) -> {
                txtClassesExistentes.appendText(mapping.getClassName() + "\n");
            });
        } else {
            this.txtClassesExistentes.setText("Nenhum arquivo de configuração foi encontrado!");
        }
    }

    @FXML
    private void actionAdicionar(ActionEvent event) {
        if (!txtNomeClasse.getText().isEmpty()) {
            if (arquivo.exists()) {
                if (arquivo.delete()) {
                    try {
                        arquivo.createNewFile();
                        this.config.getFactory().getMappings().add(new Mapping(txtNomeClasse.getText()));
                        String configuracao = xstream.toXML(config);
                        List<String> arqConfig = new ArrayList<>();
                        arqConfig.add(configuracao);
                        Files.write(Paths.get(arquivo.getPath()), arqConfig, StandardOpenOption.WRITE);
                        this.txtClassesExistentes.setText("");
                        this.txtNomeClasse.setText("");
                        this.carregar();
                    } catch (IOException ex) {
                        Logger.getLogger(AdminClassModel.class.getName()).log(Level.SEVERE, null, ex);
                        DialogFX.showMessage("Houve um erro ao atualizar o arquivo! Motivo: " + ex.getMessage(), "Erro", DialogType.ERRO);
                    }
                } else {
                    DialogFX.showMessage("Não foi possivel atualizar o arquivo de configuração", "Não é possivel excluir", DialogType.ERRO);
                }
            }
        }
    }

    @Override
    public void afterShow() {
        this.txtNomeClasse.setText("");
        this.txtClassesExistentes.setText("");
    }
}
