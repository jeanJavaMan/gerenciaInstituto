/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.application;

import br.jeanderson.control.ControlWindow;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import jeanderson.controller.ErroConexaoController;
import jeanderson.controller.LoginNovo;
import jeanderson.controller.StartAppController;
import jeanderson.util.HibernateUtil;

/**
 *
 * @author jeanderson
 */
public class StartApplication extends Application {
    
    private ControlWindow<StartAppController> janelaStart;
    private ControlWindow<LoginNovo> janelaLogin;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.janelaStart = new ControlWindow(StartAppController.class);
        this.janelaStart.show();
        this.janelaStart.getStage().setOnCloseRequest(evento ->{HibernateUtil.shutdown();});
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return janelaStart.getController().VerificaConexaoBD();
            }

            @Override
            protected void failed() {
               ControlWindow<ErroConexaoController> janelaErro = new ControlWindow(ErroConexaoController.class);
               janelaErro.show();               
               janelaStart.close();
            }

            @Override
            protected void succeeded() {
                janelaLogin = new ControlWindow(LoginNovo.class);
                janelaLogin.show();
                janelaStart.close();
                janelaLogin.getStage().setOnCloseRequest(evento -> {
                    HibernateUtil.shutdown();
                });
            }
            
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();        
    }
    
}
