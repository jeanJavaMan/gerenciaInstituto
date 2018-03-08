/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import org.hibernate.HibernateException;

/**
 * FXML Controller class
 *
 * @author jeanderson
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/StartApp.fxml", title = "Inicializando aplicação", stageStyle = StageStyle.UNDECORATED)
public class StartAppController extends EasyFXFunctions {

    @FXML
    private Button btnFechar;
    private ControlWindow<StartAppController> janela;
    @FXML
    private Text txtMsgFechar;

    public boolean VerificaConexaoBD() {
        try {
            HibernateUtil.getSessionFactory().openSession();
            return true;
        } catch (HibernateException ex) {
            System.err.println(ex);
            Log.salvaLogger(ex);
            HibernateUtil.shutdown();
            return false;
        }
    }

    @FXML
    private void actionFechar(ActionEvent event) {
        this.janela.close();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janela = control;
    }

    @Override
    public void afterShow() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (janela.getStage().isShowing()) {
                    btnFechar.setVisible(true); 
                    txtMsgFechar.setVisible(true);
                }
                timer.cancel();
            }
        }, 30 * 1000);
    }

}
