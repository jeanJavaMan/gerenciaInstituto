/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import admin.classesxml.HibernateConfig;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.MaskFormatter;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.enums.DialogType;
import br.jeanderson.enums.MaskType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXSpinner;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import jeanderson.util.HibernateUtil;
import org.hibernate.HibernateException;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/admin/AdminBancoDeDados.fxml")
public class AdminBancoDeDados extends EasyFXFunctions implements Initializable{

    @FXML
    @ValidateField(fieldName = "IP")
    @MaskFormatter(showMask = false, type = MaskType.NUMBER_ONLY)
    private JFXTextField txtIP;
    @FXML
    @ValidateField(fieldName = "Porta")
    private JFXTextField txtPorta;
    @FXML
    @ValidateField(fieldName = "Nome")
    private JFXTextField txtNome;
    @FXML
    @ValidateField(fieldName = "Usuario")
    private JFXTextField txtUsuario;
    @FXML
    private JFXTextField txtSenha;
    private HibernateConfig hibernateConfig;
    private File arquivo;
    private XStream xstream;
    private String urlConfig;
    @FXML
    private JFXSpinner spCarregando;
    @FXML
    private Text txtTestando;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.arquivo = new File("hibernate.cfg.xml");
        this.xstream = new XStream();
        xstream.processAnnotations(HibernateConfig.class);
        urlConfig = "jdbc:mysql://?host:?porta/??dataBase?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8_general_ci";

        spCarregando.visibleProperty().bindBidirectional(txtTestando.visibleProperty());
        spCarregando.setVisible(false);
    }

    @FXML
    private void actionSalvar(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            if (arquivo.exists()) {
                try {
                    arquivo.delete();
                    arquivo.createNewFile();
                    String urlPronta = this.urlConfig.replace("?host", txtIP.getText()).replace("?porta", txtPorta.getText()).replace("??dataBase", txtNome.getText());
                    System.out.println("Url Pronta: " + urlPronta);
                    hibernateConfig.getFactory().setURL(urlPronta);
                    hibernateConfig.getFactory().setUSER(txtUsuario.getText());
                    hibernateConfig.getFactory().setPASSWORD(txtSenha.getText());
                    String configuracaoPronta = xstream.toXML(hibernateConfig);
                    System.out.println(configuracaoPronta);
                    List<String> config = new ArrayList<>();
                    config.add(configuracaoPronta);
                    Files.write(Paths.get(arquivo.getPath()), config, StandardOpenOption.WRITE);
                    DialogFX.showMessage("Configurações salvas com sucesso!", "Salvo com sucesso", DialogType.SUCESS);
                } catch (IOException ex) {
                    Logger.getLogger(AdminBancoDeDados.class.getName()).log(Level.SEVERE, null, ex);
                    DialogFX.showMessage("Houve um erro ao tentar salvar configurações. Motivo:  " + ex.getMessage(), "Erro ao salvar", DialogType.ERRO);
                }
            }
        }
    }

    @FXML
    private void actionTestarBanco(ActionEvent event) {
        this.spCarregando.setVisible(true);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return VerificaConexaoBD();
            }

            @Override
            protected void failed() {
                DialogFX.showMessage("Não houve conexão com o Banco de Dados", "Não houve conexão", DialogType.ERRO);                
                spCarregando.setVisible(false);
            }

            @Override
            protected void succeeded() {
                DialogFX.showMessage("Conexão Feita com sucesso!", "Conectado com sucesso", DialogType.SUCESS);
                spCarregando.setVisible(false);
            }

        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

    }

    public void carregarConfiguracoes() {
        Thread t = new Thread(() -> {
            this.carrega();
        });
        t.setDaemon(true);
        t.start();
    }

    public boolean VerificaConexaoBD() {
        try {
            boolean restartSession = HibernateUtil.restartSession();
            System.out.println(restartSession);
            HibernateUtil.getSessionFactory().openSession();
            HibernateUtil.shutdown();
            return true;
        } catch (HibernateException ex) {
            System.err.println(ex);
            Logger.getLogger(AdminBancoDeDados.class.getName()).log(Level.SEVERE, null, ex);
            HibernateUtil.shutdown();
            return false;
        }
    }

    private void carrega() {
        if (arquivo.exists()) {
            this.hibernateConfig = (HibernateConfig) xstream.fromXML(arquivo);
        } else {
            DialogFX.showMessage("Arquivo de configuração não encontrado!", "Arquivo inexistente!", DialogType.ERRO);
        }
    }

    @Override
    public void afterShow() {
        this.txtNome.setText("");
        this.txtUsuario.setText("");
        this.txtSenha.setText("");
        this.spCarregando.setVisible(false);
    }

}
